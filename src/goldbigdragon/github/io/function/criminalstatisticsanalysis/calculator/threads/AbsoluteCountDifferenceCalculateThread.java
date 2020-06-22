package goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.threads;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.DataType;
import goldbigdragon.github.io.enums.ThreadType;
import goldbigdragon.github.io.enums.TimeEnum;
import goldbigdragon.github.io.function.BaseThread;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.Absolute;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.AbsoluteDataAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object.TargetDifferenceObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.object.VisualizationObject;
import goldbigdragon.github.io.object.BatchObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AbsoluteCountDifferenceCalculateThread extends BaseThread {

    public static List<String> elementList;
    public static int queryGet;
    private static BatchObject countBatch = null;
    private static BatchObject geoBatch = null;

    private static List<TargetDifferenceObject> targetList;
    private static List<TargetDifferenceObject> geoTargetList;

    private TimeEnum[] timeEnums = {TimeEnum.YEAR, TimeEnum.MONTH, TimeEnum.HOUR, TimeEnum.WEEK};

    public static void init(int sectorAmount){
        targetList = new ArrayList<>();
        geoTargetList = new ArrayList<>();
        elementList = new ArrayList<>();
        try {
            String countBatchQuery = "INSERT INTO '"+AbsoluteDataAPI.TABLE_ABSOLUTE_DATA_DIFFERENCE+"' " +
                    "(category, element, targetDataType, targetCategory, targetElement, yearCount, monthCount, weekCount, hourCount, totalAverage) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String geoBatchQuery = "INSERT INTO '"+ AbsoluteDataAPI.TABLE_ABSOLUTE_GEO_DATA_DIFFERENCE+"' " +
                    "(sector, category, element, targetDataType, targetCategory, targetElement, yearCount, monthCount, weekCount, hourCount, totalAverage) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            countBatch = new BatchObject(Absolute.absoluteDB.getConnection(), countBatchQuery, 5000, "AbsoluteCountDifferenceData");
            geoBatch = new BatchObject(Absolute.absoluteDB.getConnection(), geoBatchQuery, 5000, "AbsoluteGeoCountDifferenceData");

            TargetDifferenceObject tdo;
            TargetDifferenceObject geoTdo;
            DataType dt;
            String category;
            String element;
            String[] splitted;
            TimeEnum[] timeEnums = {TimeEnum.YEAR, TimeEnum.MONTH, TimeEnum.HOUR, TimeEnum.WEEK};
            for(VisualizationObject vo : MainController.visualizationTarget){
                dt = vo.type;
                if(dt == DataType.ELEMENT) {
                    splitted = vo.name.split(Main.SPLIT_SYMBOL);
                    category = splitted[0];
                    element = splitted[1];
                } else {
                    category = vo.name;
                    element = null;
                }
                tdo = new TargetDifferenceObject(dt.code, category, element);
                tdo.dataType = dt.code;
                for(TimeEnum te : timeEnums) {
                    tdo.timeMap.put(te, Absolute.absoluteDB.getTimeGrade(tdo, te, true));
                }
                targetList.add(tdo);
                for(int count = 0; count < sectorAmount; count++) {
                    geoTdo = new TargetDifferenceObject(dt.code, category, element);
                    geoTdo.dataType = dt.code;
                    geoTdo.sector = count;
                    for(TimeEnum te : timeEnums) {
                        geoTdo.timeMap.put(te, Absolute.absoluteDB.getTimeGrade(geoTdo, te, true));
                    }
                    geoTargetList.add(geoTdo);
                }
            }
        } catch(SQLException e){

        }
    }

    public static void end(){
        try{
            if(countBatch != null)
                countBatch.executeBatch();
            if(geoBatch != null)
                geoBatch.executeBatch();
            Absolute.absoluteDB.getConnection().commit();
        } catch(SQLException e) {
        }
        queryGet = 0;
        elementList = null;
        countBatch = null;
        geoTargetList = null;
        targetList = null;
        geoBatch = null;
    }

    public AbsoluteCountDifferenceCalculateThread(int count){
        super(ThreadType.BASIC, "AbsoluteCountDifferenceCalculate"+count);
    }

    @Override
    public void run() {
        String query;
        String category;
        String element;
        int sector;
        TargetDifferenceObject targetData;
        for(;;) {
            query = getQuery();
            if(query == null) {
                break;
            } else {
                try {
                    String[] splitted = query.split(Main.SPLIT_SYMBOL);
                    category = splitted[0];
                    element = splitted[1];
                    targetData = new TargetDifferenceObject(DataType.ELEMENT.code, category, element);
                    if(splitted.length > 2) {
                        sector = Integer.parseInt(splitted[2]);
                        targetData.sector = sector;
                    }
                    for(TimeEnum te : timeEnums) {
                        targetData.timeMap.put(te, Absolute.absoluteDB.getTimeGrade(targetData, te, false));
                    }
                    if(splitted.length > 2) {
                        getTime(targetData, geoTargetList);
                    } else {
                        getTime(targetData, targetList);
                    }
                } catch(SQLException e) {
                    System.out.println("[SQLite] : AbsoluteCountDifferenceCalculateThread 에러!");
                }
            }
        }
        interrupt();
    }

    synchronized private void getTime(TargetDifferenceObject tdo, List<TargetDifferenceObject> compareTdo) throws SQLException{
        int size;
        double avg;
        double yearCount = 0;
        double monthCount = 0;
        double weekCount = 0;
        double hourCount = 0;
        double totalAverage;
        for(TargetDifferenceObject tdo2 : compareTdo) {
            if(tdo.sector == tdo2.sector) {
                for(TimeEnum te : timeEnums) {
                    if(tdo.timeMap.get(te).size() <= tdo2.timeMap.get(te).size()) {
                        size = tdo.timeMap.get(te).size();
                    } else {
                        size = tdo2.timeMap.get(te).size();
                    }
                    avg = 0;
                    for(int count = 0; count < size; count++) {
                        avg += Math.abs(tdo.timeMap.get(te).get(count)-tdo2.timeMap.get(te).get(count));
                    }
                    if(avg != 0) {
                        avg/=size;
                    }
                    if(te == TimeEnum.YEAR) {
                        yearCount = avg;
                    } else if(te == TimeEnum.MONTH) {
                        monthCount = avg;
                    } else if(te == TimeEnum.WEEK) {
                        weekCount = avg;
                    } else if(te == TimeEnum.HOUR) {
                        hourCount = avg;
                    }
                }
                totalAverage = (yearCount+monthCount+hourCount+weekCount)/4;
                if(totalAverage != 0) {
                    if(tdo.sector != -1) {
                        putGeoPstmt(tdo.sector, tdo.category, tdo.element, tdo2.dataType, tdo2.category, tdo2.element, yearCount, monthCount, weekCount, hourCount, totalAverage);
                    } else {
                        putPstmt(tdo.category, tdo.element, tdo2.dataType, tdo2.category, tdo2.element, yearCount, monthCount, weekCount, hourCount, totalAverage);
                    }
                }
            }
        }
    }

    synchronized static private void putPstmt(String category, String element, String targetDataType, String targetCategory, String targetElement, double yearCount, double monthCount, double weekCount, double hourCount, double totalAverage) throws SQLException{
        countBatch.pstmt.setString(1, category);
        countBatch.pstmt.setString(2, element);
        countBatch.pstmt.setString(3, targetDataType);
        countBatch.pstmt.setString(4, targetCategory);
        countBatch.pstmt.setString(5, targetElement);
        countBatch.pstmt.setDouble(6, yearCount);
        countBatch.pstmt.setDouble(7, monthCount);
        countBatch.pstmt.setDouble(8, weekCount);
        countBatch.pstmt.setDouble(9, hourCount);
        countBatch.pstmt.setDouble(10, totalAverage);
        if(countBatch.addBatch()) {
            Absolute.absoluteDB.getConnection().commit();
        }
    }

    synchronized static private void putGeoPstmt(int sector, String category, String element, String targetDataType, String targetCategory, String targetElement, double yearCount, double monthCount, double weekCount, double hourCount, double totalAverage) throws SQLException{
        geoBatch.pstmt.setInt(1, sector);
        geoBatch.pstmt.setString(2, category);
        geoBatch.pstmt.setString(3, element);
        geoBatch.pstmt.setString(4, targetDataType);
        geoBatch.pstmt.setString(5, targetCategory);
        geoBatch.pstmt.setString(6, targetElement);
        geoBatch.pstmt.setDouble(7, yearCount);
        geoBatch.pstmt.setDouble(8, monthCount);
        geoBatch.pstmt.setDouble(9, weekCount);
        geoBatch.pstmt.setDouble(10, hourCount);
        geoBatch.pstmt.setDouble(11, totalAverage);
        if(geoBatch.addBatch()) {
            Absolute.absoluteDB.getConnection().commit();
        }
    }

    synchronized public static String getQuery(){
        String returnString = null;
        if(queryGet < elementList.size()) {
            returnString = elementList.get(queryGet);
            queryGet++;
        }
        return returnString;
    }
}