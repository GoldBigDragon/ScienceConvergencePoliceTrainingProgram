package goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.threads;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.DataType;
import goldbigdragon.github.io.enums.ThreadType;
import goldbigdragon.github.io.enums.TimeEnum;
import goldbigdragon.github.io.function.BaseThread;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.Absolute;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.AbsoluteDataDBQuery;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.enums.InsertType;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object.QueryObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;

import java.util.HashMap;
import java.util.Map;

public class AbsoluteTargetDataCalculateThread extends BaseThread {

    public AbsoluteTargetDataCalculateThread(int count){
        super(ThreadType.BASIC, "AbsoluteDataCalculate"+count);
        Absolute.queryGet = 0;
    }

    @Override
    public void run() {
        QueryObject qo;
        String stringTimeCount;
        String[] splitted;

        int totalCount;
        Map<String, Double> totalPointValue;
        Map<String, Double> hasPointElement;
        String timeKey;
        double timeValue;

        QueryObject hasPointQueryObject;
        String category;
        String element;
        for(;;) {
            qo = Absolute.getQuery();
            if(qo == null) {
                break;
            } else {
                if(qo.dataType == DataType.POINT) {
                    hasPointElement = MainController.modelDb.getHasPointElement(qo);
                    totalPointValue = new HashMap<>();
                    for(String key : hasPointElement.keySet()) {
                        splitted = key.split(Main.SPLIT_SYMBOL);
                        category = splitted[0];
                        element = splitted[1];
                        hasPointQueryObject = new QueryObject(DataType.ELEMENT, category, element, null, qo.timeEnum, 0);
                        if(qo.time < 10 && qo.timeEnum != TimeEnum.WEEK) {
                            stringTimeCount = "0"+qo.time;
                        } else {
                            stringTimeCount = Integer.toString(qo.time);
                        }
                        totalCount = Absolute.absoluteDB.getAbsoluteTargetCount(hasPointQueryObject, stringTimeCount, true);
                        if(totalCount > 0) {
                            timeKey = qo.timeEnum.name() + Main.SPLIT_SYMBOL + stringTimeCount;
                            if(totalPointValue.containsKey(timeKey)){
                                timeValue = totalPointValue.get(timeKey);
                            } else {
                                timeValue = 0;
                            }
                            timeValue += (totalCount * hasPointElement.get(key));
                            totalPointValue.put(timeKey, timeValue);
                        }
                    }
                    if(AbsoluteDataDBQuery.staticBatchTarget == InsertType.ABSOLUTE_TARGET_COUNT) {
                        for(String key : totalPointValue.keySet()) {
                            splitted = key.split(Main.SPLIT_SYMBOL);
                            Absolute.absoluteDB.insertAbsoluteTargetCountData(qo, splitted[0], splitted[1], totalPointValue.get(key));
                        }
                    } else if(AbsoluteDataDBQuery.staticBatchTarget == InsertType.ABSOLUTE_GEO_TARGET_COUNT) {
                        for(String key : totalPointValue.keySet()) {
                            splitted = key.split(Main.SPLIT_SYMBOL);
                            Absolute.absoluteDB.insertAbsoluteGeoTargetCountData(qo, splitted[0], splitted[1], totalPointValue.get(key));
                        }
                    }
                } else {
                    if(qo.time < 10 && qo.timeEnum != TimeEnum.WEEK) {
                        stringTimeCount = "0"+qo.time;
                    } else {
                        stringTimeCount = Integer.toString(qo.time);
                    }
                    Absolute.absoluteDB.getAbsoluteTargetCount(qo, stringTimeCount, false);
                }
            }
        }
        interrupt();
    }
}