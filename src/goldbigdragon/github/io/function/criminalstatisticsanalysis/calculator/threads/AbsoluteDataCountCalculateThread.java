package goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.threads;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.DataType;
import goldbigdragon.github.io.enums.ThreadType;
import goldbigdragon.github.io.enums.TimeEnum;
import goldbigdragon.github.io.function.BaseThread;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.Absolute;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.AbsoluteDataAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object.QueryObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object.SectorObject;
import goldbigdragon.github.io.object.BatchObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AbsoluteDataCountCalculateThread extends BaseThread {

    public static List<String> elementList;
    public static int queryGet;
    private static BatchObject countBatch = null;
    private static BatchObject geoBatch = null;

    public static void init(){
        elementList = new ArrayList<>();
        try {
            String countBatchQuery = "INSERT INTO '"+AbsoluteDataAPI.TABLE_ABSOLUTE_DATA_COUNT+"' " +
                    "(category, element, timeType, targetTime, timeCount) " +
                    "VALUES (?, ?, ?, ?, ?)";
            String geoBatchQuery = "INSERT INTO '"+ AbsoluteDataAPI.TABLE_ABSOLUTE_GEO_DATA_COUNT+"' " +
                    "(sector, category, element, timeType, targetTime, timeCount) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            countBatch = new BatchObject(Absolute.absoluteDB.getConnection(), countBatchQuery, 5000, "AbsoluteCountData");
            geoBatch = new BatchObject(Absolute.absoluteDB.getConnection(), geoBatchQuery, 5000, "AbsoluteGeoCountData");
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
        geoBatch = null;
    }

    public AbsoluteDataCountCalculateThread(int count){
        super(ThreadType.BASIC, "AbsoluteCountCalculate"+count);
    }

    @Override
    public void run() {
        QueryObject qo;
        String timeString;
        int startTime;
        int endTime;
        int timeCount;
        for(;;) {
            qo = getQuery();
            if(qo == null) {
                break;
            } else {
                try{
                    if(qo.timeEnum == TimeEnum.YEAR) {
                        startTime = Absolute.absoluteDB.getEdgeTime(true, qo);
                        endTime = Absolute.absoluteDB.getEdgeTime(false, qo);
                        if(startTime != -1 && endTime != -1 && startTime != 0 && endTime != 0) {
                            for(int count = startTime; count <= endTime; count++) {
                                timeString = Integer.toString(count);
                                timeCount = Absolute.absoluteDB.getSelectAbsoluteCountQuery(qo, timeString);
                                if(timeCount != 0) {
                                    if(qo.sectorObject == null) {
                                        putPstmt(qo, timeString, timeCount);
                                    } else {
                                        putGeoPstmt(qo, timeString, timeCount);
                                    }
                                }
                            }
                        }
                    } else {
                        for(int count = qo.timeEnum.startTime; count <= qo.timeEnum.endTime; count++) {
                            if(qo.timeEnum != TimeEnum.WEEK && count < 10) {
                                timeString = "0"+count;
                            } else {
                                timeString = Integer.toString(count);
                            }
                            timeCount = Absolute.absoluteDB.getSelectAbsoluteCountQuery(qo, timeString);
                            if(timeCount != 0) {
                                if(qo.sectorObject == null) {
                                    putPstmt(qo, timeString, timeCount);
                                } else {
                                    putGeoPstmt(qo, timeString, timeCount);
                                }
                            }
                        }
                    }
                } catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }
        interrupt();
    }

    synchronized static private void putPstmt(QueryObject qo, String timeString, int timeCount) throws SQLException{
        countBatch.pstmt.setString(1, qo.category);
        countBatch.pstmt.setString(2, qo.element);
        countBatch.pstmt.setString(3, qo.timeEnum.name());
        countBatch.pstmt.setString(4, timeString);
        countBatch.pstmt.setInt(5, timeCount);
        if(countBatch.addBatch()) {
            Absolute.absoluteDB.getConnection().commit();
        }
    }

    synchronized static private void putGeoPstmt(QueryObject qo, String timeString, int timeCount) throws SQLException{
        geoBatch.pstmt.setInt(1, qo.sectorObject.sector);
        geoBatch.pstmt.setString(2, qo.category);
        geoBatch.pstmt.setString(3, qo.element);
        geoBatch.pstmt.setString(4, qo.timeEnum.name());
        geoBatch.pstmt.setString(5, timeString);
        geoBatch.pstmt.setInt(6, timeCount);
        if(geoBatch.addBatch()) {
            Absolute.absoluteDB.getConnection().commit();
        }
    }

    synchronized public static QueryObject getQuery(){
        QueryObject qo = null;
        if(queryGet < elementList.size()) {
            String[] splitted = elementList.get(queryGet).split(Main.SPLIT_SYMBOL);
            queryGet++;
            qo = new QueryObject(DataType.ELEMENT, splitted[1], splitted[2], null, TimeEnum.valueOf(splitted[0]), 0);
            if(splitted.length > 3){
                int sector = Integer.parseInt(splitted[3]);
                double sectorStartLati = Double.parseDouble(splitted[4]);
                double sectorStartLongi = Double.parseDouble(splitted[5]);
                double sectorEndLati = Double.parseDouble(splitted[6]);
                double sectorEndLongi = Double.parseDouble(splitted[7]);
                SectorObject so = new SectorObject(sector, sectorStartLati, sectorStartLongi, sectorEndLati, sectorEndLongi);
                qo.sectorObject = so;
            }
        }
        return qo;
    }
}