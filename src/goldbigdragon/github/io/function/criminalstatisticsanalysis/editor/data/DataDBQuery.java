package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.object.BatchObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.DataObject;
import goldbigdragon.github.io.util.DbHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataDBQuery extends DbHandler {

    public static final String TABLE_DATA_GENERAL = "data_general";
    public static final String TABLE_DATA_ADDITIONAL = "data_additional_";

//    CREATE
    public void createGeneralTable() {
        String sql = "CREATE TABLE IF NOT EXISTS '" + TABLE_DATA_GENERAL + "' ( " +
                "num INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "main TEXT NOT NULL, " +
                "sub TEXT NOT NULL, " +
                "startTime TEXT DEFAULT \"1995-05-19 22:10:00\", " +
                "endTime TEXT DEFAULT \"2095-05-19 22:10:00\", " +
                "amount INTEGER NOT NULL DEFAULT 0, " +
                "startLatitude REAL DEFAULT 0, " +
                "startLongitude REAL DEFAULT 0, " +
                "endLatitude REAL DEFAULT 0, " +
                "endLongitude REAL DEFAULT 0 " +
                ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : " + TABLE_DATA_GENERAL + " 테이블 생성 실패");
        }
    }

    public void createBasicAdditionalDataTable(String category){
        String createWhenNotExistTableQuery = "CREATE TABLE IF NOT EXISTS '" +TABLE_DATA_ADDITIONAL + category + "' (num INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, main TEXT NOT NULL, sub TEXT NOT NULL, target INTEGER DEFAULT 0 NOT NULL);";
        try {
            PreparedStatement columnSelectPstmt = conn.prepareStatement(createWhenNotExistTableQuery);
            columnSelectPstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + createWhenNotExistTableQuery + "' 에러");
            return;
        }
    }

    public void createAdditionalDataTable(String category, List<String> additionalDataColumnList) {
        StringBuilder sb = new StringBuilder();
        String[] splitted;
        String type;
        String name;

        sb.append("CREATE TABLE IF NOT EXISTS '");
        sb.append(TABLE_DATA_ADDITIONAL);
        sb.append(category);
        sb.append("' (\n");
        sb.append("num INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n");
        sb.append("main TEXT NOT NULL,\n");
        sb.append("sub TEXT NOT NULL,\n");
        for(String additionalDataColumn : additionalDataColumnList) {
            splitted = additionalDataColumn.split(Main.SPLIT_SYMBOL);
            type = splitted[0];
            name = splitted[1];
            if(type.equals("TEXT")) {
                if(splitted.length > 2 && ! splitted[2].equals("NULL")) {
                    sb.append("'" + name + "' TEXT DEFAULT '" + splitted[2] +"',\n");
                } else {
                    sb.append("'" + name + "' TEXT DEFAULT NULL,\n");
                }
            }
            else if(type.equals("INTEGER")) {
                if(splitted.length > 2) {
                    sb.append("'" + name + "' INTEGER DEFAULT " + splitted[2] +",\n");
                } else {
                    sb.append("'" + name + "' INTEGER DEFAULT 0,\n");
                }
            }
            else if(type.equals("LONG")) {
                if(splitted.length > 2) {
                    sb.append("'" + name + "' LONG DEFAULT " + splitted[2] +",\n");
                } else {
                    sb.append("'" + name + "' LONG DEFAULT 0,\n");
                }
            }
            else if(type.equals("REAL")) {
                if(splitted.length > 2) {
                    sb.append("'" + name + "' REAL DEFAULT " + splitted[2] +",\n");
                } else {
                    sb.append("'" + name + "' REAL DEFAULT 0,\n");
                }
            }
            else if(type.equals("BOOLEAN")) {
                if(splitted.length > 2) {
                    sb.append("'" + name + "' BOOLEAN DEFAULT " + splitted[2] +",\n");
                } else {
                    sb.append("'" + name + "' BOOLEAN DEFAULT false,\n");
                }
            }
        }
        sb.append("target INTEGER DEFAULT 0 NOT NULL\n");
        sb.append(");");
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : " + TABLE_DATA_ADDITIONAL + category + " 테이블 생성 실패");
        }
    }

    public void createAdditionalDataTable(List<DataObject> dataList) {
        Map<String, List<String>> additionalDataInfoMap = new HashMap<>();
        List<String> categories = new ArrayList<>();
        List<String> adList;
        String category;
        String adInfo;
        String[] splitted;
        for(DataObject dataObject : dataList) {
            category = dataObject.getMain().getValue();
            if(!categories.contains(category)) {
                categories.add(category);
            }
            if(additionalDataInfoMap.containsKey(category)) {
                adList = additionalDataInfoMap.get(category);
            } else {
                adList = new ArrayList<>();
            }
            for(String ad : dataObject.getAdditionalData()) {
                splitted = ad.split(Main.SPLIT_SYMBOL);
                adInfo = splitted[0] + Main.SPLIT_SYMBOL + splitted[1];
                if(! adList.contains(adInfo)) {
                    adList.add(adInfo);
                }
            }
            additionalDataInfoMap.put(category, adList);
        }
        for(String category2 : additionalDataInfoMap.keySet()) {
            createAdditionalDataTable(category2, additionalDataInfoMap.get(category2));
        }
    }


//    SELECT
    public int getLastGeneralNum() {
        int num = -1;
        try {
            String lastNumSelectSql = "SELECT num FROM '" + TABLE_DATA_GENERAL + "' ORDER BY num DESC LIMIT 1;";
            PreparedStatement lastNumSelectPstmt  = conn.prepareStatement(lastNumSelectSql);
            ResultSet rs  = lastNumSelectPstmt.executeQuery();
            while (rs.next()) {
                num = rs.getInt("num");
            }
        } catch(SQLException e) {
        }
        return num;
    }

//    INSERT
    public boolean insertData(List<DataObject> dataObjectList) {
        String dataInsertSql = "INSERT INTO '"+ TABLE_DATA_GENERAL +
                "' (main, sub, startTime, endTime, amount, startLatitude, startLongitude, endLatitude, endLongitude, num)" +
                " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Map<String, BatchObject> additionalDataBatchObject = new HashMap<>();
        List<String> additionalData;
        String category;
        String element;
        try {
            BatchObject dataInsertBatch = new BatchObject(conn, dataInsertSql, 3000, "DATA");
            int num = getLastGeneralNum();
            if(num < 0)
                num = 0;

            for(DataObject dataObject : dataObjectList) {
                num++;
                additionalData = dataObject.getAdditionalData();
                category = dataObject.getMain().getValue();
                element = dataObject.getSub().getValue();
                dataInsertBatch.pstmt.setString(1, category);
                dataInsertBatch.pstmt.setString(2, element);
                dataInsertBatch.pstmt.setString(3, dataObject.getStartTime().getValue());
                dataInsertBatch.pstmt.setString(4, dataObject.getEndTime().getValue());
                dataInsertBatch.pstmt.setInt(5, dataObject.getAmount().getValue());
                dataInsertBatch.pstmt.setDouble(6, dataObject.getStartLatitude().getValue());
                dataInsertBatch.pstmt.setDouble(7, dataObject.getStartLongitude().getValue());
                dataInsertBatch.pstmt.setDouble(8, dataObject.getEndLatitude().getValue());
                dataInsertBatch.pstmt.setDouble(9, dataObject.getEndLongitude().getValue());
                dataInsertBatch.pstmt.setInt(10, num);
                if(dataInsertBatch.addBatch()){
                    conn.commit();
                }
                if(!additionalData.isEmpty()) {
                    insertAdditionalData(num, category, element, additionalData, additionalDataBatchObject);
                }
            }
            dataInsertBatch.executeBatch();
            for(String key : additionalDataBatchObject.keySet()) {
                additionalDataBatchObject.get(key).executeBatch();
            }
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.out.println("[SQLITE] : 데이터 등록 실패");
        }
        return false;
    }

    public List<String> getNearEnviroment(String targetCategory, String targetElement, double minRange, double maxRange, int dataAmount) {
        List<String> returnList = new ArrayList<>();
        String dataInsertSql = "SELECT " +
                "(((startLatitude+endLatitude)/2)+((ABS(RANDOM()) / 9223372036854775808 + "+minRange+") * "+maxRange+" + "+minRange+")) AS latitude, " +
                "(((startLongitude+endLongitude)/2)+((ABS(RANDOM()) / 9223372036854775808 + "+minRange+") * "+maxRange+" + "+minRange+")) AS longitude " +
                "FROM data_general WHERE main == '"+targetCategory+"' ";
        if(targetElement != null) {
            dataInsertSql+="AND sub =='"+targetElement+"' ";
        }
        dataInsertSql += "GROUP BY num ORDER BY random() LIMIT "+dataAmount+";";
        try {
            PreparedStatement lastNumSelectPstmt  = conn.prepareStatement(dataInsertSql);
            ResultSet rs  = lastNumSelectPstmt.executeQuery();
            while (rs.next()) {
                returnList.add(rs.getDouble("latitude") + Main.SPLIT_SYMBOL + rs.getDouble("longitude"));
            }
        } catch(SQLException e) {
        }
        return returnList;
    }

    public List<String> getTargetLoc(String targetCategory, String targetElement){
        List<String> returnList = new ArrayList<>();
        String dataInsertSql = "SELECT (startLatitude+endLatitude)/2 AS lati, (startLongitude+endLongitude)/2 AS longi FROM data_general " +
                "WHERE main == '"+targetCategory+"' AND sub == '" + targetElement + "' GROUP BY num;";
        try {
            PreparedStatement lastNumSelectPstmt  = conn.prepareStatement(dataInsertSql);
            ResultSet rs  = lastNumSelectPstmt.executeQuery();
            while (rs.next()) {
                returnList.add(rs.getDouble("lati") + Main.SPLIT_SYMBOL + rs.getDouble("longi"));
            }
        } catch(SQLException e) {
        }
        return returnList;
    }

    public List<String> getRemoveTarget(String targetCategory, String targetElement, double minRange, double maxRange, double targetLati, double targetLongi) {
        List<String> returnList = new ArrayList<>();
        String dataInsertSql = "SELECT num, main FROM data_general WHERE (" +
                "("+(targetLati+minRange)+" < ((startLatitude+endLatitude)/2) AND " +
                (targetLati+maxRange)+" > ((startLatitude+endLatitude)/2) AND " +
                (targetLongi-minRange)+" > ((startLongitude+endLongitude)/2) AND " +
                (targetLongi-maxRange)+" < ((startLongitude+endLongitude)/2)) OR " +
                "("+(targetLati+minRange)+" < ((startLatitude+endLatitude)/2) AND " +
                (targetLati+maxRange)+" > ((startLatitude+endLatitude)/2) AND " +
                (targetLongi+minRange)+" < ((startLongitude+endLongitude)/2) AND " +
                (targetLongi+maxRange)+" > ((startLongitude+endLongitude)/2)) OR " +
                "("+(targetLati-minRange)+" > ((startLatitude+endLatitude)/2) AND " +
                (targetLati-maxRange)+" < ((startLatitude+endLatitude)/2) AND " +
                (targetLongi-minRange)+" > ((startLongitude+endLongitude)/2) AND " +
                (targetLongi-maxRange)+" < ((startLongitude+endLongitude)/2)) OR " +
                "("+(targetLati-minRange)+" > ((startLatitude+endLatitude)/2) AND " +
                (targetLati-maxRange)+" < ((startLatitude+endLatitude)/2) AND " +
                (targetLongi+minRange)+" < ((startLongitude+endLongitude)/2) AND " +
                (targetLongi+maxRange)+" > ((startLongitude+endLongitude)/2)) " +
                ") AND main == '"+targetCategory+"' AND sub == '" + targetElement + "'";
        try {
            PreparedStatement lastNumSelectPstmt  = conn.prepareStatement(dataInsertSql);
            ResultSet rs  = lastNumSelectPstmt.executeQuery();
            while (rs.next()) {
                returnList.add(rs.getInt("num") + Main.SPLIT_SYMBOL + rs.getString("main"));
            }
        } catch(SQLException e) {
        }
        return returnList;
    }

    private void insertAdditionalData(int num, String category, String element, List<String> additionalData, Map<String, BatchObject> additionalDataBatchObject) {
        try {
            String[] splitted;
            StringBuilder additionalName = new StringBuilder();
            additionalName.append(category);
            for(String data : additionalData) {
                splitted = data.split(Main.SPLIT_SYMBOL);
                additionalName.append(splitted[1]);
            }
            String key = additionalName.toString();
            if( ! additionalDataBatchObject.containsKey(key)) {
                StringBuilder sb = new StringBuilder();
                sb.append("INSERT INTO '");
                sb.append(TABLE_DATA_ADDITIONAL);
                sb.append(category);
                sb.append("'\n");
                sb.append("(main, sub,");
                for(String data : additionalData) {
                    splitted = data.split(Main.SPLIT_SYMBOL);
                    sb.append("'"+ splitted[1] + "', ");
                }
                sb.append("target)\n");
                sb.append("VALUES (?, ?, ");
                for(int count = 0; count < additionalData.size(); count++) {
                    sb.append("?, ");
                }
                sb.append("?);");
                additionalDataBatchObject.put(key, new BatchObject(conn, sb.toString(), 3000, "AdditionalData"));
            }

            additionalDataBatchObject.get(key).pstmt.setString(1, category);
            additionalDataBatchObject.get(key).pstmt.setString(2, element);
            int start = 3;
            int valueIndex = 2;
            for(String data : additionalData) {
                splitted = data.split(Main.SPLIT_SYMBOL);
                if(splitted.length > 3)
                    valueIndex = 3;
                if(splitted[0].equals("TEXT")) {
                    if(splitted.length == 2)
                        additionalDataBatchObject.get(key).pstmt.setString(start, null);
                    else
                        additionalDataBatchObject.get(key).pstmt.setString(start, splitted[valueIndex]);
                }
                else if(splitted[0].equals("INTEGER") || splitted[0].equals("LONG")) {
                    if(splitted.length == 2)
                        additionalDataBatchObject.get(key).pstmt.setLong(start, 0l);
                    else
                        additionalDataBatchObject.get(key).pstmt.setLong(start, Long.parseLong(splitted[valueIndex]));
                }
                else if(splitted[0].equals("REAL")) {
                    if(splitted.length == 2)
                        additionalDataBatchObject.get(key).pstmt.setDouble(start, 0.0d);
                    else
                        additionalDataBatchObject.get(key).pstmt.setDouble(start, Double.parseDouble(splitted[valueIndex]));
                }
                else if(splitted[0].equals("BOOLEAN")) {
                    if(splitted.length == 2)
                        additionalDataBatchObject.get(key).pstmt.setBoolean(start, false);
                    else
                        additionalDataBatchObject.get(key).pstmt.setBoolean(start, Boolean.parseBoolean(splitted[valueIndex]));
                }
                start++;
            }
            additionalDataBatchObject.get(key).pstmt.setInt(start, num);
            if(additionalDataBatchObject.get(key).addBatch()){
                conn.commit();
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : " + category + " 속 " + num + " 번째 데이터의 부가정보 등록 실패");
        }
    }
}