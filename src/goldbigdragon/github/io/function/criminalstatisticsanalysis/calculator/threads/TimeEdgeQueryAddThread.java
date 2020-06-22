package goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.threads;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.DataType;
import goldbigdragon.github.io.enums.ThreadType;
import goldbigdragon.github.io.enums.TimeEnum;
import goldbigdragon.github.io.function.BaseThread;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.Absolute;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object.QueryObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;

import java.util.List;
import java.util.Map;

public class TimeEdgeQueryAddThread extends BaseThread {

    public static List<QueryObject> queryList;
    public static int queryGet;

    public TimeEdgeQueryAddThread(int count){
        super(ThreadType.BASIC, "TimeEdgeQueryCreator"+count);
        queryGet = 0;
    }

    @Override
    public void run() {
        QueryObject qo;
        int startTime;
        int endTime;
        int timeCount;

        Map<String, Double> hasPointElement;
        String category;
        String element;
        String[] splitted;
        int minYear;
        int maxYear;

        for(;;) {
            qo = getQuery();
            if(qo == null) {
                break;
            } else {
                if(qo.dataType == DataType.POINT) {
                    hasPointElement = MainController.modelDb.getHasPointElement(qo);
                    QueryObject subQo;
                    minYear = Integer.MAX_VALUE;
                    maxYear = Integer.MIN_VALUE;
                    for(String key : hasPointElement.keySet()) {
                        splitted = key.split(Main.SPLIT_SYMBOL);
                        category = splitted[0];
                        element = splitted[1];
                        subQo = new QueryObject(DataType.ELEMENT, category, element, null, qo.timeEnum, 0);
                        startTime = Absolute.absoluteDB.getEdgeTime(true, subQo);
                        endTime = Absolute.absoluteDB.getEdgeTime(false, subQo);
                        if(startTime != -1 && endTime != -1 && !(qo.timeEnum == TimeEnum.YEAR && (startTime == 0 || endTime == 0))) {
                            if(minYear > startTime)
                                minYear = startTime;
                            if(maxYear < endTime) {
                                maxYear = endTime;
                            }
                        }
                    }
                    if(minYear != Integer.MAX_VALUE && maxYear != Integer.MIN_VALUE) {
                        for(timeCount = minYear; timeCount <= maxYear; timeCount++) {
                            subQo = new QueryObject(DataType.POINT, qo.category, null, null, qo.timeEnum, timeCount);
                            subQo.sectorObject = qo.sectorObject;
                            addQuery(subQo, timeCount);
                        }
                    }
                } else {
                    startTime = Absolute.absoluteDB.getEdgeTime(true, qo);
                    endTime = Absolute.absoluteDB.getEdgeTime(false, qo);
                    if(startTime != -1 && endTime != -1) {
                        for(timeCount = startTime; timeCount <= endTime; timeCount++) {
                            addQuery(qo, timeCount);
                        }
                    }
                }
            }
        }
        interrupt();
    }

    synchronized private static QueryObject getQuery(){
        QueryObject query = null;
        if(queryGet < queryList.size()) {
            query = queryList.get(queryGet);
            queryGet++;
            return query;
        }
        return query;
    }

    synchronized private void addQuery(QueryObject qo, int time){
        QueryObject query = new QueryObject(qo);
        query.time = time;
        if(!Absolute.queryList.contains(query)) {
            Absolute.queryList.add(query);
        }
    }
}
