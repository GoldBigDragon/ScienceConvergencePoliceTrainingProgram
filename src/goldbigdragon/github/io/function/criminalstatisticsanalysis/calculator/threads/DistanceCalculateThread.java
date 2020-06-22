package goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.threads;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.ThreadType;
import goldbigdragon.github.io.enums.TimeEnum;
import goldbigdragon.github.io.function.BaseThread;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.Absolute;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.AbsoluteDataAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.DataAPI;
import goldbigdragon.github.io.object.BatchObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DistanceCalculateThread extends BaseThread {

    public static List<String> elementList;
    public static int queryGet;
    private static BatchObject weekBatch = null;
    private static BatchObject hourBatch = null;
    private static BatchObject monthBatch = null;
    private static BatchObject yearBatch = null;

    private static List<String> allFailedList = null;
    private static List<String> yearBatchFailedList = null;
    private static List<String> monthBatchFailedList = null;
    private static List<String> hourBatchFailedList = null;
    private static List<String> weekBatchFailedList = null;

    public static void init(int autoExecute){
        elementList = new ArrayList<>();
        allFailedList = new ArrayList<>();
        yearBatchFailedList = new ArrayList<>();
        monthBatchFailedList = new ArrayList<>();
        hourBatchFailedList = new ArrayList<>();
        weekBatchFailedList = new ArrayList<>();
        try {
            StringBuilder prefix = new StringBuilder();
            StringBuilder suffix = new StringBuilder();
            prefix.append("INSERT INTO '" + AbsoluteDataAPI.TABLE_GEO_DATA_DISTANCE + "' (sector, category, element, targetDataType, targetCategory, targetElement, timeType, timeString, distance) ");
            prefix.append("SELECT ? AS sector, main AS category, sub AS element, " +
                    "? AS targetDataType, ? AS targetCategory, ? AS targetElement, '");
            suffix.append("' AS timeType, ? AS timeString, " +
                    "IFNULL((" +
                    "AVG(((" +
                    "IFNULL(ACOS(SIN(" +
                    "CASE WHEN ? > ((startLatitude+endLatitude) / 2)  " +
                    "THEN (((startLatitude+endLatitude) / 2) * PI() / 180) " +
                    "ELSE (? * PI() / 180) END " +
                    ") * SIN(" +
                    "CASE WHEN ? > ((startLatitude+endLatitude) / 2) " +
                    "THEN (? * PI() / 180) " +
                    "ELSE (((startLatitude+endLatitude) / 2) * PI() / 180) END" +
                    ") + COS(" +
                    "CASE WHEN ? > ((startLatitude+endLatitude) / 2) " +
                    "THEN (((startLatitude+endLatitude) / 2) * PI() / 180) " +
                    "ELSE (? * PI() / 180) END" +
                    ") * " +
                    "COS(" +
                    "CASE WHEN ? > ((startLatitude+endLatitude) / 2) " +
                    "THEN (? * PI() / 180) " +
                    "ELSE (((startLatitude+endLatitude) / 2) * PI() / 180) END" +
                    ") * COS(" +
                    "CASE WHEN ? > ((startLongitude+endLongitude) / 2) " +
                    "THEN (((startLongitude+endLongitude) / 2) - ?) " +
                    "ELSE (? - ((startLongitude+endLongitude) / 2)) END" +
                    ")), 0)*180 / PI())* 60 * 1.1515 * 1609.344)) " +
                    "), -1) AS distance " +
                    "FROM '"+ DataAPI.TABLE_DATA_GENERAL +"' ");
            suffix.append("WHERE ");
            suffix.append("(startLatitude >= ? AND endLatitude <= ?) AND ");
            suffix.append("(startLongitude >= ? AND endLongitude <= ?) AND ");
            suffix.append("(");

            weekBatch = new BatchObject(Absolute.absoluteDB.getConnection(), prefix.toString() + TimeEnum.WEEK.name() + suffix.toString() + "(strftime('"+TimeEnum.WEEK.symbol+"', startTime) <= ? AND strftime('"+TimeEnum.WEEK.symbol+"', endTime) >= ?) OR ((strftime('%s', endTime) - strftime('%s', startTime)) > "+TimeEnum.WEEK.afterCount+") ) GROUP BY main, sub;", autoExecute, "WeekTimeGeoDataDistance");
            hourBatch = new BatchObject(Absolute.absoluteDB.getConnection(), prefix.toString() + TimeEnum.HOUR.name() + suffix.toString() + "(strftime('"+TimeEnum.HOUR.symbol+"', startTime) <= ? AND strftime('"+TimeEnum.HOUR.symbol+"', endTime) >= ?) OR ((strftime('%s', endTime) - strftime('%s', startTime)) > "+TimeEnum.HOUR.afterCount+") ) GROUP BY main, sub;", autoExecute, "HourTimeGeoDataDistance");
            monthBatch = new BatchObject(Absolute.absoluteDB.getConnection(), prefix.toString() + TimeEnum.MONTH.name() + suffix.toString() + "(strftime('"+TimeEnum.MONTH.symbol+"', startTime) <= ? AND strftime('"+TimeEnum.MONTH.symbol+"', endTime) >= ?) OR ((strftime('%s', endTime) - strftime('%s', startTime)) > "+TimeEnum.MONTH.afterCount+") ) GROUP BY main, sub;", autoExecute, "MonthTimeGeoDataDistance");
            yearBatch = new BatchObject(Absolute.absoluteDB.getConnection(), prefix.toString() + TimeEnum.YEAR.name() + suffix.toString() + "(strftime('"+TimeEnum.YEAR.symbol+"', startTime) <= ? AND strftime('"+TimeEnum.YEAR.symbol+"', endTime) >= ?) ) GROUP BY main, sub;", autoExecute, "YearTimeGeoDataDistance");
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void end(){
        try{
            if(yearBatch != null)
                yearBatch.executeBatch();
            if(weekBatch != null)
                weekBatch.executeBatch();
            if(hourBatch != null)
                hourBatch.executeBatch();
            if(monthBatch != null)
                monthBatch.executeBatch();
            Absolute.absoluteDB.getConnection().commit();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        queryGet = 0;
        elementList = null;
        yearBatch = null;
        weekBatch = null;
        hourBatch = null;
        monthBatch = null;
        allFailedList = null;
        yearBatchFailedList = null;
        monthBatchFailedList = null;
        hourBatchFailedList = null;
        weekBatchFailedList = null;
    }

    public DistanceCalculateThread(int count){
        super(ThreadType.BASIC, "AbsoluteCountDifferenceCalculate"+count);
    }

    @Override
    public void run() {
        String query;

        String[] splitted = null;
        int sector = 0;
        TimeEnum timeType = null;
        String targetTime= null;
        double sectorStartLati = 0;
        double sectorEndLati = 0;
        double sectorStartLongi = 0;
        double sectorEndLongi = 0;
        double targetX = 0;
        double targetY = 0;
        String targetDataType= null;
        String targetCategory= null;
        String targetElement= null;
        for(;;) {
            query = getQuery();
            if(query == null) {
                break;
            } else {
                try {
                    splitted = query.split(Main.SPLIT_SYMBOL);
                    sector = Integer.parseInt(splitted[0]);
                    timeType = TimeEnum.valueOf(splitted[1]);
                    targetTime = splitted[2];
                    sectorStartLati = Double.parseDouble(splitted[3]);
                    sectorStartLongi = Double.parseDouble(splitted[4]);
                    sectorEndLati = Double.parseDouble(splitted[5]);
                    sectorEndLongi = Double.parseDouble(splitted[6]);
                    targetX = Double.parseDouble(splitted[7]);
                    targetY = Double.parseDouble(splitted[8]);
                    targetDataType = splitted[9];
                    targetCategory = splitted[10];
                    targetElement = splitted[11];
                    if(targetElement == null || targetElement.length() < 1 || targetElement.equalsIgnoreCase("null")) {
                        targetElement = null;
                    }
                    if(timeType == TimeEnum.YEAR) {
                        yearBatchFailedList.add(query);
                        pstmt(sector, targetDataType, targetCategory, targetElement, targetTime, targetX, targetY, sectorStartLati, sectorStartLongi, sectorEndLati, sectorEndLongi, yearBatch, timeType);
                    } else if(timeType == TimeEnum.WEEK){
                        weekBatchFailedList.add(query);
                        pstmt(sector, targetDataType, targetCategory, targetElement, targetTime, targetX, targetY, sectorStartLati, sectorStartLongi, sectorEndLati, sectorEndLongi, weekBatch, timeType);
                    } else if(timeType == TimeEnum.HOUR){
                        hourBatchFailedList.add(query);
                        pstmt(sector, targetDataType, targetCategory, targetElement, targetTime, targetX, targetY, sectorStartLati, sectorStartLongi, sectorEndLati, sectorEndLongi, hourBatch, timeType);
                    } else if(timeType == TimeEnum.MONTH){
                        monthBatchFailedList.add(query);
                        pstmt(sector, targetDataType, targetCategory, targetElement, targetTime, targetX, targetY, sectorStartLati, sectorStartLongi, sectorEndLati, sectorEndLongi, monthBatch, timeType);
                    }
                } catch(SQLException e) {
                    if(timeType == TimeEnum.YEAR) {
                        allFailedList.addAll(yearBatchFailedList);
                    } else if(timeType == TimeEnum.WEEK){
                        allFailedList.addAll(weekBatchFailedList);
                    } else if(timeType == TimeEnum.HOUR){
                        allFailedList.addAll(hourBatchFailedList);
                    } else if(timeType == TimeEnum.MONTH){
                        allFailedList.addAll(monthBatchFailedList);
                    }
                    System.out.println(timeType.name()+ "/"+targetTime+" : " + targetCategory +"/"+targetElement+"/");
//                    org.sqlite.SQLiteException: [SQLITE_ERROR] SQL error or missing database (Domain error)
                    e.printStackTrace();
                    System.out.println("[SQLite] : AbsoluteCountDifferenceCalculateThread 에러!");
                }
            }
        }
        interrupt();
    }

    synchronized private static void pstmt(int sector, String targetDataType, String targetCategory, String targetElement, String timeString, double targetLocX, double targetLocY, double startLatitude, double startLongitude, double endLatitude, double endLongitude, BatchObject bo, TimeEnum te) throws SQLException{
        bo.pstmt.setInt(1, sector);
        bo.pstmt.setString(2, targetDataType);
        bo.pstmt.setString(3, targetCategory);
        bo.pstmt.setString(4, targetElement);
        bo.pstmt.setString(5, timeString);
        bo.pstmt.setDouble(6, targetLocX);
        bo.pstmt.setDouble(7, targetLocX);
        bo.pstmt.setDouble(8, targetLocX);
        bo.pstmt.setDouble(9, targetLocX);
        bo.pstmt.setDouble(10, targetLocX);
        bo.pstmt.setDouble(11, targetLocX);
        bo.pstmt.setDouble(12, targetLocX);
        bo.pstmt.setDouble(13, targetLocX);
        bo.pstmt.setDouble(14, targetLocY);
        bo.pstmt.setDouble(15, targetLocY);
        bo.pstmt.setDouble(16, targetLocY);
        bo.pstmt.setDouble(17, startLatitude);
        bo.pstmt.setDouble(18, endLatitude);
        bo.pstmt.setDouble(19, startLongitude);
        bo.pstmt.setDouble(20, endLongitude);
        bo.pstmt.setString(21, timeString);
        bo.pstmt.setString(22, timeString);
        if(bo.addBatch()) {
            Absolute.absoluteDB.getConnection().commit();
            if(te == TimeEnum.YEAR) {
                yearBatchFailedList.clear();
            } else if(te == TimeEnum.WEEK){
                weekBatchFailedList.clear();
            } else if(te == TimeEnum.HOUR){
                hourBatchFailedList.clear();
            } else if(te == TimeEnum.MONTH){
                monthBatchFailedList.clear();
            }
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

    public static List<String> getErrorDatas(){
        return allFailedList;
    }
}