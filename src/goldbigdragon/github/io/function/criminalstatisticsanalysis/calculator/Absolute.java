package goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.TimeEnum;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.enums.InsertType;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object.QueryObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object.SectorObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.threads.*;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.object.VisualizationObject;
import goldbigdragon.github.io.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Absolute {

    public static AbsoluteDataAPI absoluteDB;
    public static Map<String, Map<TimeEnum,List<Integer>>> absoluteTargetTimeGradeMap;

    public static List<QueryObject> queryList;
    public static int queryGet;

    public void clear(){
        absoluteDB.close();
        absoluteDB = null;
        absoluteTargetTimeGradeMap = null;
        queryList = null;
        queryGet = 0;
        TimeEdgeQueryAddThread.queryList = null;
        AbsoluteDataCountCalculateThread.end();
    }

    synchronized public static QueryObject getQuery(){
        QueryObject returnString = null;
        if(queryGet < queryList.size()) {
            returnString = queryList.get(queryGet);
            queryGet++;
            return returnString;
        }
        return returnString;
    }

    public int calculate(String filePath, int rows, int columns, double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        DateUtil du = new DateUtil();
        du.calcStart("시각화");
        int totalSector = rows*columns;
        absoluteDB = new AbsoluteDataAPI(filePath);
        absoluteDB.createAllTables(rows, columns, startLatitude, startLongitude, endLatitude, endLongitude);

        addAbsoluteTargetCountQuery();
        if( ! absoluteDB.isDataCreatedWell(true)) {
            absoluteDB.close();
            return -1;
        }

        addAbsoluteGeoCount(rows, columns, startLatitude, startLongitude, endLatitude, endLongitude, false);
        if( ! absoluteDB.isDataCreatedWell(false)) {
            absoluteDB.close();
            return -2;
        }
        addAbsoluteGeoCount(rows, columns, startLatitude, startLongitude, endLatitude, endLongitude, true);
        calcAbsoluteCountDifference(totalSector);
        calcDistance(rows, columns, startLatitude, startLongitude, endLatitude, endLongitude);
        absoluteDB.close();
        absoluteDB = new AbsoluteDataAPI(filePath);
        absoluteDB.dropDefaultTables();
        du.calcEnd();
        return 0;
    }

    private void addAbsoluteTargetCountQuery(){
        TimeEdgeQueryAddThread.queryList = new ArrayList<>();
        queryList = new ArrayList<>();
        TimeEnum[] timeEnums = {TimeEnum.YEAR, TimeEnum.MONTH, TimeEnum.HOUR, TimeEnum.WEEK};
        absoluteDB.startOfInsertStream(InsertType.ABSOLUTE_TARGET_COUNT);
        TimeEdgeQueryAddThread.queryList = new ArrayList<>();
        queryList = new ArrayList<>();
        for(VisualizationObject vo : MainController.visualizationTarget) {
            for (TimeEnum te : timeEnums) {
                if(te == TimeEnum.YEAR) {
                    TimeEdgeQueryAddThread.queryList.add(new QueryObject(vo, te, 0));
                } else {
                    for (int count = te.startTime; count <= te.endTime; count++) {
                        Absolute.queryList.add(new QueryObject(vo, te, count));
                    }
                }
            }
        }
        getTimeEdge();
        dataCalculate();
        queryList = null;
        absoluteDB.endOfInsertStream();
    }

    private void addAbsoluteGeoCount(int rows, int columns, double startLatitude, double startLongitude, double endLatitude, double endLongitude, boolean isTarget){
        double latiSize = (endLatitude - startLatitude) / rows;
        double longiSize = (endLongitude - startLongitude) / columns;
        int sector = 0;
        double sectorStartLati;
        double sectorEndLati;
        double sectorStartLongi;
        double sectorEndLongi;
        TimeEnum[] timeEnums = {TimeEnum.YEAR, TimeEnum.MONTH, TimeEnum.HOUR, TimeEnum.WEEK};
        QueryObject qo;
        SectorObject so;
        if(isTarget) {
            absoluteDB.startOfInsertStream(InsertType.ABSOLUTE_GEO_TARGET_COUNT);
            for(int rowCount = 0; rowCount < rows; rowCount++) {
                sectorStartLati = endLatitude - (latiSize * (rowCount+1));
                sectorEndLati = endLatitude - (latiSize * (rowCount));
                for(int columnCount = 0; columnCount < columns; columnCount++) {
                    queryList = new ArrayList<>();
                    sectorStartLongi = startLongitude + (longiSize * (columnCount));
                    sectorEndLongi = startLongitude + (longiSize * (columnCount+1));
                    TimeEdgeQueryAddThread.queryList = new ArrayList<>();
                    so = new SectorObject(sector, sectorStartLati, sectorStartLongi, sectorEndLati, sectorEndLongi);
                    for(VisualizationObject vo : MainController.visualizationTarget) {
                        for (TimeEnum te : timeEnums) {
                            if(te == TimeEnum.YEAR) {
                                qo = new QueryObject(vo, te, 0);
                                qo.sectorObject = so;
                                TimeEdgeQueryAddThread.queryList.add(qo);
                            } else {
                                for(int count = te.startTime; count <= te.endTime; count++) {
                                    qo = new QueryObject(vo, te, count);
                                    qo.sectorObject = so;
                                    queryList.add(qo);
                                }
                            }
                        }
                    }
                    getTimeEdge();
                    dataCalculate();
                    sector++;
                }
            }
            queryList = null;
            absoluteDB.endOfInsertStream();
        } else {
            AbsoluteDataCountCalculateThread.init();
            String prefix;
            List<AbsoluteDataCountCalculateThread> dataInsertThreads;
            for(TimeEnum te : timeEnums) {
                for(String category : MainController.model.keySet()) {
                    for (String element : MainController.model.get(category)) {
                        dataInsertThreads = new ArrayList<>();
                        prefix = te.name()+Main.SPLIT_SYMBOL+category+Main.SPLIT_SYMBOL+element;
                        AbsoluteDataCountCalculateThread.elementList.add(prefix);
                        sector = 0;
                        for(int rowCount = 0; rowCount < rows; rowCount++) {
                            sectorStartLati = endLatitude - (latiSize * (rowCount+1));
                            sectorEndLati = endLatitude - (latiSize * (rowCount));
                            for(int columnCount = 0; columnCount < columns; columnCount++) {
                                sectorStartLongi = startLongitude + (longiSize * (columnCount));
                                sectorEndLongi = startLongitude + (longiSize * (columnCount+1));
                                AbsoluteDataCountCalculateThread.elementList.add(prefix + Main.SPLIT_SYMBOL +
                                        sector + Main.SPLIT_SYMBOL +sectorStartLati + Main.SPLIT_SYMBOL +sectorStartLongi
                                        + Main.SPLIT_SYMBOL + sectorEndLati + Main.SPLIT_SYMBOL + sectorEndLongi);
                                sector++;
                            }
                        }
                        for(int count = 0; count < Main.maxThreads; count++) {
                            dataInsertThreads.add(new AbsoluteDataCountCalculateThread(count));
                        }
                        for(AbsoluteDataCountCalculateThread dct : dataInsertThreads) {
                            dct.start();
                        }
                        try{
                            for(AbsoluteDataCountCalculateThread dct : dataInsertThreads) {
                                dct.join();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            AbsoluteDataCountCalculateThread.end();
        }
    }

    private void calcAbsoluteCountDifference(int sectorAmount){
//        TODO :
//        할 때 마다 계산 결과가 다름. 쿼리에 ORDER BY 라도 넣어주기
        AbsoluteCountDifferenceCalculateThread.init(sectorAmount);
        String prefix;
        List<AbsoluteCountDifferenceCalculateThread> dataInsertThreads;
        for(String category : MainController.model.keySet()) {
            for (String element : MainController.model.get(category)) {
                prefix = category+Main.SPLIT_SYMBOL+element;
                dataInsertThreads = new ArrayList<>();
                AbsoluteCountDifferenceCalculateThread.elementList.clear();
                AbsoluteCountDifferenceCalculateThread.queryGet = 0;
                AbsoluteCountDifferenceCalculateThread.elementList.add(prefix);
                for(int sector = 0; sector < sectorAmount; sector++) {
                    AbsoluteCountDifferenceCalculateThread.elementList.add(prefix + Main.SPLIT_SYMBOL + sector);
                }
                if(!AbsoluteCountDifferenceCalculateThread.elementList.isEmpty()) {
                    for(int count = 0; count < Main.maxThreads; count++) {
                        dataInsertThreads.add(new AbsoluteCountDifferenceCalculateThread(count));
                    }
                    for(AbsoluteCountDifferenceCalculateThread dct : dataInsertThreads) {
                        dct.start();
                    }
                    try{
                        for(AbsoluteCountDifferenceCalculateThread dct : dataInsertThreads) {
                            dct.join();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        AbsoluteCountDifferenceCalculateThread.end();
    }

    private void calcDistance(int rows, int columns, double startLatitude, double startLongitude, double endLatitude, double endLongitude){
        QueryObject qo;
        double latiSize = (endLatitude - startLatitude) / rows;
        double longiSize = (endLongitude - startLongitude) / columns;
        List<DistanceCalculateThread> dataInsertThreads;
        Map<Integer, List<Double>> sectorStartEndLatiLongi = new HashMap<>();

        int rowJump;
        int columnJump;
        double sectorStartLati;
        double sectorEndLati;
        double sectorStartLongi;
        double sectorEndLongi;
        List<Double> startLatiLongiList;
        for(int sector = 0; sector < rows*columns; sector++) {
            rowJump = sector/rows;
            columnJump = sector - rowJump*rows;
            startLatiLongiList = new ArrayList<>();
            sectorStartLati = endLatitude - (latiSize * (rowJump+1));
            sectorEndLati = endLatitude - (latiSize * (rowJump));
            sectorStartLongi = startLongitude + (longiSize * (columnJump));
            sectorEndLongi = startLongitude + (longiSize * (columnJump+1));
            startLatiLongiList.add(sectorStartLati);
            startLatiLongiList.add(sectorStartLongi);
            startLatiLongiList.add(sectorEndLati);
            startLatiLongiList.add(sectorEndLongi);
            sectorStartEndLatiLongi.put(sector, startLatiLongiList);
        }

        Map<Integer, List<Double>> sectorTarget;
        List<Double> sectorTargetLocList;
        List<String> targetList;
        TimeEnum[] timeEnums = {TimeEnum.YEAR, TimeEnum.MONTH, TimeEnum.HOUR, TimeEnum.WEEK};

        List<String> yearList = Absolute.absoluteDB.getYears();

        double lati;
        double longi;
        int sector;
        String timeString;

        int minTime = 0;
        int maxTime = 0;
        String key;

        DistanceCalculateThread.init(500);
        for(VisualizationObject vo : MainController.visualizationTarget) {
            qo = new QueryObject(vo, TimeEnum.YEAR, 0);
            for (TimeEnum te : timeEnums) {
                dataInsertThreads = new ArrayList<>();
                DistanceCalculateThread.elementList = new ArrayList<>();
                DistanceCalculateThread.queryGet = 0;
                targetList = new ArrayList<>();
                if(te == TimeEnum.YEAR) {
                    minTime = Integer.parseInt(yearList.get(0));
                    maxTime = Integer.parseInt(yearList.get(yearList.size()-1));
                } else if(te == TimeEnum.MONTH){
                    minTime = 1;
                    maxTime = 12;
                } else if(te == TimeEnum.WEEK){
                    minTime = 0;
                    maxTime = 6;
                } else if(te == TimeEnum.HOUR){
                    minTime = 0;
                    maxTime = 23;
                }

                for(int count = minTime; count <= maxTime; count++) {
                    if(te != TimeEnum.WEEK && count < 10) {
                        timeString = "0"+count;
                    } else {
                        timeString = Integer.toString(count);
                    }
                    targetList.addAll(Absolute.absoluteDB.getTargetAverageLoc(qo.category, qo.element, te, timeString));
                    if(!targetList.isEmpty()) {
                        sectorTarget = new HashMap<>();

                        for(String targetLatiLongi : targetList) {
                            lati = Double.parseDouble(targetLatiLongi.split(Main.SPLIT_SYMBOL)[0]);
                            longi = Double.parseDouble(targetLatiLongi.split(Main.SPLIT_SYMBOL)[1]);
                            sector = getSector(lati, longi, sectorStartEndLatiLongi);
                            if(sector != -1) {
                                if(sectorTarget.containsKey(sector)) {
                                    sectorTargetLocList = sectorTarget.get(sector);
                                    sectorTargetLocList.set(0, (sectorTargetLocList.get(0) + lati)/2);
                                    sectorTargetLocList.set(1, (sectorTargetLocList.get(1) + longi)/2);
                                } else {
                                    sectorTargetLocList = new ArrayList<>();
                                    sectorTargetLocList.add(lati);
                                    sectorTargetLocList.add(longi);
                                }
                                sectorTarget.put(sector, sectorTargetLocList);
                            }
                        }
                        if(!sectorTarget.isEmpty()) {
                            for(int targetSector : sectorTarget.keySet()) {
                                key = targetSector + Main.SPLIT_SYMBOL + te.name() + Main.SPLIT_SYMBOL + timeString + Main.SPLIT_SYMBOL +
                                        sectorStartEndLatiLongi.get(targetSector).get(0) + Main.SPLIT_SYMBOL +
                                        sectorStartEndLatiLongi.get(targetSector).get(1) + Main.SPLIT_SYMBOL +
                                        sectorStartEndLatiLongi.get(targetSector).get(2) + Main.SPLIT_SYMBOL +
                                        sectorStartEndLatiLongi.get(targetSector).get(3) + Main.SPLIT_SYMBOL +
                                        sectorTarget.get(targetSector).get(0) + Main.SPLIT_SYMBOL +
                                        sectorTarget.get(targetSector).get(1) + Main.SPLIT_SYMBOL +
                                        qo.dataType.code + Main.SPLIT_SYMBOL +
                                        qo.category + Main.SPLIT_SYMBOL +
                                        qo.element;
                                DistanceCalculateThread.elementList.add(key);
                            }
                        }
                    }
                }
                if(!DistanceCalculateThread.elementList.isEmpty()) {
                    for(int count = 0; count < Main.maxThreads; count++) {
                        dataInsertThreads.add(new DistanceCalculateThread(count));
                    }
                    for(DistanceCalculateThread dct : dataInsertThreads) {
                        dct.start();
                    }
                    try{
                        for(DistanceCalculateThread dct : dataInsertThreads) {
                            dct.join();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        List<String> errorDatas = DistanceCalculateThread.getErrorDatas();
        DistanceCalculateThread.end();
        if(!errorDatas.isEmpty()) {
            dataInsertThreads = new ArrayList<>();
            DistanceCalculateThread.init(50);
            DistanceCalculateThread.elementList.addAll(DistanceCalculateThread.getErrorDatas());
            if(!DistanceCalculateThread.elementList.isEmpty()) {
                for(int count = 0; count < Main.maxThreads; count++) {
                    dataInsertThreads.add(new DistanceCalculateThread(count));
                }
                for(DistanceCalculateThread dct : dataInsertThreads) {
                    dct.start();
                }
                try{
                    for(DistanceCalculateThread dct : dataInsertThreads) {
                        dct.join();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            errorDatas = DistanceCalculateThread.getErrorDatas();
            DistanceCalculateThread.end();
            if(!errorDatas.isEmpty()) {
                dataInsertThreads = new ArrayList<>();
                DistanceCalculateThread.init(5);
                DistanceCalculateThread.elementList.addAll(DistanceCalculateThread.getErrorDatas());
                if(!DistanceCalculateThread.elementList.isEmpty()) {
                    for(int count = 0; count < Main.maxThreads; count++) {
                        dataInsertThreads.add(new DistanceCalculateThread(count));
                    }
                    for(DistanceCalculateThread dct : dataInsertThreads) {
                        dct.start();
                    }
                    try{
                        for(DistanceCalculateThread dct : dataInsertThreads) {
                            dct.join();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                DistanceCalculateThread.end();
            }
        }
    }

    private int getSector(double lati, double longi, Map<Integer, List<Double>> sectorMap){
        int returnSector = -1;
        List<Double> latiLongi;
        for(int sector : sectorMap.keySet()) {
            latiLongi = sectorMap.get(sector);
            if(lati >= latiLongi.get(0) && lati <= latiLongi.get(2) &&
            longi >= latiLongi.get(1) && longi <= latiLongi.get(3)) {
                returnSector = sector;
                break;
            }
        }
        return returnSector;
    }

    private void getTimeEdge(){
        List<TimeEdgeQueryAddThread> queryCreateThreads = new ArrayList<>();
        for(int count = 0; count < 4; count++) {
            queryCreateThreads.add(new TimeEdgeQueryAddThread(count));
        }
        for(TimeEdgeQueryAddThread dcqct : queryCreateThreads) {
            dcqct.start();
        }
        try{
            for(TimeEdgeQueryAddThread dcqct : queryCreateThreads) {
                dcqct.join();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        TimeEdgeQueryAddThread.queryList = null;
    }

    private void dataCalculate(){
        List<AbsoluteTargetDataCalculateThread> dataInsertThreads = new ArrayList<>();
        for(int count = 0; count < Main.maxThreads; count++) {
            dataInsertThreads.add(new AbsoluteTargetDataCalculateThread(count));
        }
        for(AbsoluteTargetDataCalculateThread dct : dataInsertThreads) {
            dct.start();
        }
        try{
            for(AbsoluteTargetDataCalculateThread dct : dataInsertThreads) {
                dct.join();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
