package goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator;

import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.enums.InsertType;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object.*;
import goldbigdragon.github.io.object.BatchObject;
import goldbigdragon.github.io.util.DbHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class AbsoluteDataDBQuery extends DbHandler {

    public static final String TABLE_SETTINGS = "setting";
    public static final String TABLE_ABSOLUTE_TARGET_DATA_COUNT = "absolute_target_data_count";
    public static final String TABLE_ABSOLUTE_DATA_COUNT = "absolute_data_count";
    public static final String TABLE_ABSOLUTE_DATA_DIFFERENCE = "absolute_data_difference";
    public static final String TABLE_ABSOLUTE_TARGET_GEO_DATA_COUNT = "absolute_target_geo_data_count";
    public static final String TABLE_ABSOLUTE_GEO_DATA_COUNT = "absolute_geo_data_count";
    public static final String TABLE_ABSOLUTE_GEO_DATA_DIFFERENCE = "absolute_geo_data_difference";
    public static final String TABLE_GEO_DATA_DISTANCE = "geo_data_distance";

    private static BatchObject staticBatch = null;
    public static InsertType staticBatchTarget = null;

//    CREATE
    public void createAllTables(int rows, int columns, double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        createDefaultTable();
        createAbsoluteTargetDataTable();
        createAbsoluteCountDataTable();
        createAbsoluteDifferenceDataTable();
        createAbsoluteGeoTargetDataTable();
        createAbsoluteGeoCountDataTable();
        createAbsoluteGeoDifferenceDataTable();
        createAbsoluteGeoDistanceDataTable();
        insertDefaultSettings(rows, columns, startLatitude, startLongitude, endLatitude, endLongitude);
    }

    private void createDefaultTable(){
        String sql =
                "CREATE TABLE IF NOT EXISTS '" + TABLE_SETTINGS + "' (\n" +
                        "`rows` INTEGER DEFAULT 1,\n" +
                        "`columns` INTEGER DEFAULT 1,\n" +
                        "`startLatitude` REAL DEFAULT 0,\n" +
                        "`startLongitude` REAL DEFAULT 0,\n" +
                        "`endLatitude` REAL DEFAULT 0,\n" +
                        "`endLongitude` REAL DEFAULT 0\n" +
                        ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : createDefaultTable() 에러!");
        }
    }

    private void createAbsoluteTargetDataTable(){
        String sql =
                "CREATE TABLE IF NOT EXISTS '" + TABLE_ABSOLUTE_TARGET_DATA_COUNT + "' (\n" +
                        "dataType TEXT NOT NULL,\n" +
                        "category TEXT NOT NULL,\n" +
                        "element TEXT DEFAULT NULL,\n" +
                        "subQuery TEXT DEFAULT NULL,\n" +
                        "timeType TEXT NOT NULL,\n" +
                        "targetTime TEXT NOT NULL,\n" +
                        "timeCount REAL DEFAULT 0\n" +
                        ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : createAbsoluteCountDataTable() 에러!");
        }
    }

    private void createAbsoluteCountDataTable(){
        String sql =
                "CREATE TABLE IF NOT EXISTS '" + TABLE_ABSOLUTE_DATA_COUNT + "' (\n" +
                        "category TEXT NOT NULL,\n" +
                        "element TEXT NOT NULL,\n" +
                        "timeType TEXT NOT NULL,\n" +
                        "targetTime TEXT NOT NULL,\n" +
                        "timeCount INTEGER DEFAULT 0\n" +
                        ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : createAbsoluteCountDataTable() 에러!");
        }
    }

    private void createAbsoluteDifferenceDataTable(){
        String sql =
                "CREATE TABLE IF NOT EXISTS '" + TABLE_ABSOLUTE_DATA_DIFFERENCE + "' (\n" +
                "category TEXT NOT NULL,\n" +
                "element TEXT NOT NULL,\n" +
                "targetDataType TEXT NOT NULL,\n" +
                "targetCategory TEXT NOT NULL,\n" +
                "targetElement TEXT DEFAULT NULL,\n" +
                "yearCount REAL DEFAULT 0,\n" +
                "monthCount REAL DEFAULT 0,\n" +
                "hourCount REAL DEFAULT 0,\n" +
                "weekCount REAL DEFAULT 0,\n" +
                "totalAverage REAL DEFAULT 0\n" +
                ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : createAbsoluteDifferenceDataTable() 에러!");
        }
    }

    private void createAbsoluteGeoTargetDataTable(){
        String sql =
                "CREATE TABLE IF NOT EXISTS '" + TABLE_ABSOLUTE_TARGET_GEO_DATA_COUNT + "' (\n" +
                        "sector INTEGER DEFAULT 0,\n" +
                        "dataType TEXT NOT NULL,\n" +
                        "category TEXT NOT NULL,\n" +
                        "element TEXT DEFAULT NULL,\n" +
                        "subQuery TEXT DEFAULT NULL,\n" +
                        "timeType TEXT NOT NULL,\n" +
                        "targetTime TEXT NOT NULL,\n" +
                        "timeCount DOUBLE DEFAULT 0\n" +
                        ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : createAbsoluteCountDataTable() 에러!");
        }
    }

    private void createAbsoluteGeoCountDataTable(){
        String sql =
                "CREATE TABLE IF NOT EXISTS '" + TABLE_ABSOLUTE_GEO_DATA_COUNT + "' (\n" +
                        "sector INTEGER DEFAULT 0,\n" +
                        "category TEXT NOT NULL,\n" +
                        "element TEXT NOT NULL,\n" +
                        "timeType TEXT NOT NULL,\n" +
                        "targetTime TEXT NOT NULL,\n" +
                        "timeCount INTEGER DEFAULT 0\n" +
                        ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : createAbsoluteGeoCountDataTable() 에러!");
        }
    }

    private void createAbsoluteGeoDifferenceDataTable(){
        String sql =
                "CREATE TABLE IF NOT EXISTS '" + TABLE_ABSOLUTE_GEO_DATA_DIFFERENCE + "' (\n" +
                "sector INTEGER DEFAULT 0,\n" +
                "category TEXT NOT NULL,\n" +
                "element TEXT NOT NULL,\n" +
                "targetDataType TEXT NOT NULL,\n" +
                "targetCategory TEXT NOT NULL,\n" +
                "targetElement TEXT DEFAULT NULL,\n" +
                "yearCount INTEGER DEFAULT 0,\n" +
                "monthCount INTEGER DEFAULT 0,\n" +
                "hourCount INTEGER DEFAULT 0,\n" +
                "weekCount INTEGER DEFAULT 0,\n" +
                "totalAverage REAL DEFAULT 0\n" +
                ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : createAbsoluteGeoDifferenceDataTable() 에러!");
        }
    }

    private void createAbsoluteGeoDistanceDataTable(){
        String sql =
                "CREATE TABLE IF NOT EXISTS '" + TABLE_GEO_DATA_DISTANCE + "' (\n" +
                        "sector INTEGER DEFAULT 0,\n" +
                        "category TEXT NOT NULL,\n" +
                        "element TEXT NOT NULL,\n" +
                        "targetDataType TEXT NOT NULL,\n" +
                        "targetCategory TEXT NOT NULL,\n" +
                        "targetElement TEXT DEFAULT NULL,\n" +
                        "timeType TEXT NOT NULL,\n" +
                        "timeString TEXT DEFAULT '0',\n" +
                        "distance REAL DEFAULT -1\n" +
                        ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : createAbsoluteGeoDistanceDataTable() 에러!");
        }
    }


    //    INSERT
    private void insertDefaultSettings(int row, int column, double startLatitude, double startLongitude, double endLatitude, double endLongitude){
        String dataInsertSql = "INSERT INTO '"+ TABLE_SETTINGS +
                "' (rows, columns, startLatitude, startLongitude, endLatitude, endLongitude)" +
                " VALUES(?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement pstmt = conn.prepareStatement(dataInsertSql);
            pstmt.setInt(1, row);
            pstmt.setInt(2, column);
            pstmt.setDouble(3, startLatitude);
            pstmt.setDouble(4, startLongitude);
            pstmt.setDouble(5, endLatitude);
            pstmt.setDouble(6, endLongitude);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : insertDefaultSettings() 실패!");
        }
    }

    public void startOfInsertStream(InsertType insertType) {
        endOfInsertStream();
        staticBatchTarget = insertType;
        try {
            if(insertType == InsertType.ABSOLUTE_TARGET_COUNT) {
                String dataInsertSql = "INSERT INTO '"+ TABLE_ABSOLUTE_TARGET_DATA_COUNT +
                        "' (dataType, category, element, subQuery, timeType, targetTime, timeCount)" +
                        " VALUES(?, ?, ?, ?, ?, ?, ?)";
                staticBatch = new BatchObject(conn, dataInsertSql, 5000, "AbsoluteTargetCountData");
            } else if(insertType == InsertType.ABSOLUTE_GEO_TARGET_COUNT) {
                String dataInsertSql = "INSERT INTO '"+ TABLE_ABSOLUTE_TARGET_GEO_DATA_COUNT +
                        "' (sector, dataType, category, element, subQuery, timeType, targetTime, timeCount)" +
                        " VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
                staticBatch = new BatchObject(conn, dataInsertSql, 5000, "AbsoluteGeoTargetCountData");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : " + insertType.name() + "을 위한 startOfInsertStream() 실패!");
        }
    }

    public void endOfInsertStream() {
        if(staticBatch != null) {
            try {
                staticBatch.executeBatch();
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("[SQLITE] : " + staticBatchTarget + "() 종료 실패!");
            }
        }
        staticBatchTarget = null;
        staticBatch = null;
    }

    synchronized public void insertAbsoluteTargetCountData(QueryObject qo, String timeType, String targetTime, double timeCount) {
        try {
            staticBatch.pstmt.setString(1, qo.dataType.code);
            staticBatch.pstmt.setString(2, qo.category);
            staticBatch.pstmt.setString(3, qo.element);
            staticBatch.pstmt.setString(4, qo.subQuery);
            staticBatch.pstmt.setString(5, timeType);
            staticBatch.pstmt.setString(6, targetTime);
            staticBatch.pstmt.setDouble(7, timeCount);
            if(staticBatch.addBatch()) {
                conn.commit();
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("[SQLITE] : insertAbsoluteDataCount() 실패!");
        }
    }

    synchronized public void insertAbsoluteGeoTargetCountData(QueryObject queryObject, String timeType, String targetTime, double timeCount) {
        try {
            staticBatch.pstmt.setInt(1, queryObject.sectorObject.sector);
            staticBatch.pstmt.setString(2, queryObject.dataType.code);
            staticBatch.pstmt.setString(3, queryObject.category);
            staticBatch.pstmt.setString(4, queryObject.element);
            staticBatch.pstmt.setString(5, queryObject.subQuery);
            staticBatch.pstmt.setString(6, timeType);
            staticBatch.pstmt.setString(7, targetTime);
            staticBatch.pstmt.setDouble(8, timeCount);
            if(staticBatch.addBatch()) {
                conn.commit();
            }
        } catch (Exception e) {
            System.out.println("[SQLITE] : insertAbsoluteGeoCountData() 실패!");
        }
    }

//
//    synchronized public boolean insertAbsoluteGeoDistanceData(List<AbsoluteGeoDistanceDataObject> absoluteDistanceGeoDataObjectList) {
//        String dataInsertSql = "INSERT INTO '"+ TABLE_ABSOLUTE_GEO_DATA_DIFFERENCE +
//                "' (sector, category, element, yearAverage, monthAverage, hourAverage, weekAverage, totalAverage)" +
//                " VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
//        try {
//            BatchObject dataInsertBatch = new BatchObject(conn, dataInsertSql, 3000, "AbsoluteGeoDistanceData");
//            for(AbsoluteGeoDistanceDataObject agddo : absoluteDistanceGeoDataObjectList) {
//                dataInsertBatch.pstmt.setInt(1, agddo.sector);
//                dataInsertBatch.pstmt.setString(2, agddo.category);
//                dataInsertBatch.pstmt.setString(3, agddo.element);
//                dataInsertBatch.pstmt.setDouble(4, agddo.yearAverage);
//                dataInsertBatch.pstmt.setDouble(5, agddo.monthAverage);
//                dataInsertBatch.pstmt.setDouble(6, agddo.hourAverage);
//                dataInsertBatch.pstmt.setDouble(7, agddo.weekAverage);
//                dataInsertBatch.pstmt.setDouble(8, agddo.totalAverage);
//                if(dataInsertBatch.addBatch()) {
//                    conn.commit();
//                }
//            }
//            dataInsertBatch.executeBatch();
//            conn.commit();
//            conn.setAutoCommit(true);
//            return true;
//        } catch (SQLException e) {
//            System.out.println("[SQLITE] : insertAbsoluteGeoDistanceData() 실패!");
//        }
//        return false;
//    }
}