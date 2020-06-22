package goldbigdragon.github.io.function.criminalstatisticsanalysis.main.threads;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.ThreadType;
import goldbigdragon.github.io.enums.TimeEnum;
import goldbigdragon.github.io.function.BaseThread;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;

import java.util.*;

public class DataRangeCalculateThread extends BaseThread {

    TimeEnum selectTimeType;
    public static List<String> queryList;
    static int queryGet;

    public DataRangeCalculateThread(TimeEnum selectTimeType, int count){
        super(ThreadType.BASIC, "DataRangeCalculate"+count);
        this.selectTimeType = selectTimeType;
        queryGet = 0;
    }

    @Override
    public void run() {
        List<Double> tempLatilongti;
        String[] splitted;
        String query;
        for(;;) {
            query = getQuery();
            if(query == null) {
                break;
            } else {
                splitted = query.split(Main.SPLIT_SYMBOL);
                if(selectTimeType != TimeEnum.ALL) {
                    tempLatilongti = MainController.dataDb.getStartEndLatiLongi(selectTimeType, splitted[0], splitted[1], splitted[2], splitted[3]);
                } else {
                    tempLatilongti = MainController.dataDb.getDataRange(splitted[0], splitted[1]);
                }
                check(tempLatilongti);
            }
        }
    }

    synchronized private static String getQuery(){
        if(queryGet < queryList.size()) {
            String returnString = queryList.get(queryGet);
            queryGet++;
            return returnString;
        }
        return null;
    }

    synchronized private void check(List<Double> tempLatilongti){
        if(tempLatilongti != null && tempLatilongti.size() > 3) {
            if(tempLatilongti.get(0) < MainController.selectedLatiLongti.get(0))
                MainController.selectedLatiLongti.set(0, tempLatilongti.get(0));
            if(tempLatilongti.get(1) < MainController.selectedLatiLongti.get(1))
                MainController.selectedLatiLongti.set(1, tempLatilongti.get(1));
            if(tempLatilongti.get(2) > MainController.selectedLatiLongti.get(2))
                MainController.selectedLatiLongti.set(2, tempLatilongti.get(2));
            if(tempLatilongti.get(3) > MainController.selectedLatiLongti.get(3))
                MainController.selectedLatiLongti.set(3, tempLatilongti.get(3));
            MainController.added = true;
        }
    }
}
