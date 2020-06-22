package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.TimeEnum;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.AdditionalDataColumnObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.AdditionalDataTableObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.DataObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.DataTableObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;
import goldbigdragon.github.io.object.ColumnObject;
import goldbigdragon.github.io.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DataAPI extends DataDBQuery {

    public DataAPI(String databaseName){
        connectDb(databaseName);
    }

    public void vaccum() {
        try {
            String sql = "VACUUM;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SQLITE] : vaccum() 에러");
        }
    }

    public List<Double> getDataRange(String category, String element){
        List<Double> returnList = new ArrayList<>();
        String[] target = {"startLatitude", "startLongitude", "endLatitude", "endLongitude"};
        String[] desc = {"", "", " DESC ", " DESC "};
        try {
            StringBuilder sql;
            for(int count = 0; count < target.length; count++) {
                sql = new StringBuilder();
                sql.append("SELECT ");
                sql.append(target[count]);
                sql.append(" FROM '");
                sql.append(DataDBQuery.TABLE_DATA_GENERAL);
                sql.append("' WHERE main == '");
                sql.append(category);
                sql.append("' ");
                if(element != null) {
                    sql.append("AND sub == '");
                    sql.append(element);
                    sql.append("' ");
                }
                sql.append("ORDER BY ");
                sql.append(target[count]);
                sql.append(desc[count]);
                sql.append(" LIMIT 1");
                PreparedStatement pstmt = conn.prepareStatement(sql.toString());
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    returnList.add(rs.getDouble(target[count]));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : 데이터 범위 구하기 실패!");
        }
        return returnList;
    }

    public List<String> getCategory(){
        List<String> returnList = new ArrayList<>();
        String startTimeSql = "SELECT DISTINCT main FROM '" + DataDBQuery.TABLE_DATA_GENERAL + "' ORDER BY main DESC;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(startTimeSql);
            ResultSet rs = pstmt.executeQuery();
            String category;
            while (rs.next()) {
                category = rs.getString("main");
                returnList.add(category);
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + startTimeSql + "' 에러");
        }
        return returnList;
    }

    public List<DataTableObject> getDatas(String category, String element, String sort, String desc, int count, int page) {
        List<DataTableObject> returnList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM '");
        sql.append(DataDBQuery.TABLE_DATA_GENERAL);
        sql.append("'");
        if( ! category.equals("모든 카테고리")) {
            sql.append(" WHERE main='");
            sql.append(category);
            sql.append("'");
            if(element != null && ! element.equals("모든 엘리먼트")) {
                sql.append(" AND sub='");
                sql.append(element);
                sql.append("'");
            }
        }
        sql.append(" ORDER BY ");
        if(sort.equals("카테고리"))
            sql.append("`main`");
        else if(sort.equals("엘리먼트"))
            sql.append("`sub`");
        else if(sort.equals("시작 시간"))
            sql.append("`startTime`");
        else if(sort.equals("종료 시간"))
            sql.append("`endTime`");
        else if(sort.equals("개수"))
            sql.append("`amount`");
        else if(sort.equals("시작 위도"))
            sql.append("`startLatitude`");
        else if(sort.equals("시작 경도"))
            sql.append("`startLongitude`");
        else if(sort.equals("종료 위도"))
            sql.append("`endLatitude`");
        else if(sort.equals("종료 경도"))
            sql.append("`endLongitude`");
        else
            sql.append("`num`");

        if(desc != null && desc.equals("내림차순"))
            sql.append(" DESC");
        sql.append(" LIMIT ");
        sql.append(page*count);
        sql.append(", " + count + ";");
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            int num;
            String main;
            String sub;
            String startTime;
            String endTime;
            int amount;
            double startLatitude;
            double startLongitude;
            double endLatitude;
            double endLongitude;
            while (rs.next()) {
                num = rs.getInt("num");
                main = rs.getString("main");
                sub = rs.getString("sub");
                startTime = rs.getString("startTime");
                endTime = rs.getString("endTime");
                amount = rs.getInt("amount");
                startLatitude = rs.getDouble("startLatitude");
                startLongitude = rs.getDouble("startLongitude");
                endLatitude = rs.getDouble("endLatitude");
                endLongitude = rs.getDouble("endLongitude");
                returnList.add(new DataTableObject(Integer.toString(num), main, sub, startTime, endTime, Integer.toString(amount), Double.toString(startLatitude), Double.toString(startLongitude), Double.toString(endLatitude), Double.toString(endLongitude)));
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + sql.toString() + "' 에러");
        }
        return returnList;
    }

    public List<DataObject> getAllData(int count, int page) {
        List<DataObject> returnData = new ArrayList<>();
        String sql = "SELECT * FROM '" + DataDBQuery.TABLE_DATA_GENERAL + "' LIMIT " + (page*count) + ", " + count + ";";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            int num;
            String main;
            String sub;
            String startTime;
            String endTime;
            int amount;
            double startLatitude;
            double startLongitude;
            double endLatitude;
            double endLongitude;
            List<AdditionalDataTableObject> additionalDataList;
            List<String> stringfyAdditionalData;
            while (rs.next()) {
                num = rs.getInt("num");
                main = rs.getString("main");
                sub = rs.getString("sub");
                startTime = rs.getString("startTime");
                endTime = rs.getString("endTime");
                amount = rs.getInt("amount");
                startLatitude = rs.getDouble("startLatitude");
                startLongitude = rs.getDouble("startLongitude");
                endLatitude = rs.getDouble("endLatitude");
                endLongitude = rs.getDouble("endLongitude");
                additionalDataList = getAdditionalData(num, main);
                stringfyAdditionalData = new ArrayList<>();
                for(AdditionalDataTableObject adto : additionalDataList) {
                    stringfyAdditionalData.add(adto.toString());
                }
                returnData.add(new DataObject(main, sub, startTime, endTime, amount, startLatitude, startLongitude, endLatitude, endLongitude, stringfyAdditionalData));
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + sql + "' 에러");
        }
        return returnData;
    }

    public int getMaxPage(String category, String element) {
        int maxPage = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) AS maxPage FROM '");
        sql.append(DataDBQuery.TABLE_DATA_GENERAL);
        sql.append("'");
        if( ! category.equals("모든 카테고리")) {
            sql.append(" WHERE main='");
            sql.append(category);
            sql.append("'");
            if( ! element.equals("모든 엘리먼트")) {
                sql.append(" AND sub='");
                sql.append(element);
                sql.append("'");
            }
        }

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                maxPage = rs.getInt("maxPage");
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + sql.toString() + "' 에러");
        }
        if(maxPage%20 == 0){
            return maxPage/20;
        }
        else{
            return (maxPage/20) + 1 ;
        }
    }

    public List<AdditionalDataTableObject> getAdditionalData(int num, String category) {
        List<AdditionalDataTableObject> returnList = new ArrayList<>();
        List<AdditionalDataColumnObject> columnList = getAdditionalDataColumns(category);
        if(!columnList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ");
            for(int count = 0; count < columnList.size()-1; count++) {
                sb.append("\"");
                sb.append(columnList.get(count).name);
                sb.append("\"");
                sb.append(", ");
            }
            sb.append("\"");
            sb.append(columnList.get(columnList.size()-1).name);
            sb.append("\" FROM \"");
            sb.append(DataDBQuery.TABLE_DATA_ADDITIONAL);
            sb.append(category);
            sb.append("\" WHERE target = ");
            sb.append(num);
            sb.append(";");
            try {
                PreparedStatement pstmt = conn.prepareStatement(sb.toString());
                ResultSet rs = pstmt.executeQuery();
                String name, type, value;
                while (rs.next()) {
                    for(AdditionalDataColumnObject co : columnList) {
                        name = co.name;
                        type = co.type;
                        if(co.type.equals("INTEGER"))
                            value = Integer.toString(rs.getInt(co.name));
                        else if(co.type.equals("LONG"))
                            value = Long.toString(rs.getLong(co.name));
                        else if(co.type.equals("REAL"))
                            value = Double.toString(rs.getDouble(co.name));
                        else if(co.type.equals("BOOLEAN"))
                            value = Boolean.toString(rs.getBoolean(co.name));
                        else {
                            value = rs.getString(co.name);
                        }
                        returnList.add(new AdditionalDataTableObject(name, type, value));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("[SQLITE] : '" + sb.toString() + "' 에러");
            }
        }
        for(int count = 0; count < returnList.size(); count++) {
            if(returnList.get(count).getValue().getValue() == null || returnList.get(count).getValue().getValue().trim().length() < 1) {
                String additionalDataName = returnList.get(count).getName().getValue();
                for(AdditionalDataColumnObject co : columnList) {
                    if(co.name.equals(additionalDataName)) {
                        returnList.get(count).setValue(co.dflt_value);
                        break;
                    }
                }
            }
        }
        if(returnList.isEmpty()) {
            for(AdditionalDataColumnObject co : columnList) {
                returnList.add(new AdditionalDataTableObject(co.name, co.type, co.dflt_value));
            }
        }

        return returnList;
    }

    public List<AdditionalDataColumnObject> getAdditionalDataColumns(String category){
        List<AdditionalDataColumnObject> returnList = new ArrayList<>();
        String sql = "PRAGMA table_info(\"" + DataDBQuery.TABLE_DATA_ADDITIONAL +category + "\");";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            int cid;
            String name;
            String type;
            int notnull;
            String dflt_value;
            int pk;
            while (rs.next()) {
                name = rs.getString("name");
                if( ! (name.equals("num")||name.equals("main")||name.equals("sub")||name.equals("target"))) {
                    cid = rs.getInt("cid");
                    type = rs.getString("type");
                    notnull = rs.getInt("notnull");
                    dflt_value = rs.getString("dflt_value");
                    pk = rs.getInt("pk");
                    returnList.add(new AdditionalDataColumnObject(cid, name, type, notnull, dflt_value, pk));
                }
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + sql + "' 에러");
        }
        return returnList;
    }

    public List<Integer> getTimes(TimeEnum timeEnum){
        List<Integer> returnList = new ArrayList<>();
        String startSql = "SELECT DISTINCT strftime('" + timeEnum.symbol + "', startTime) as `t` FROM data_general WHERE NOT startTime IS NULL AND NOT endTime IS NULL;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(startSql);
            ResultSet rs = pstmt.executeQuery();
            int y;
            while (rs.next()) {
                y = rs.getInt("t");
                returnList.add(y);
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + startSql + "' 에러");
        }
        String endSql = "SELECT DISTINCT strftime('" + timeEnum.symbol + "', endTime) as `t` FROM data_general WHERE NOT startTime IS NULL AND NOT endTime IS NULL;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(endSql);
            ResultSet rs = pstmt.executeQuery();
            int y;
            while (rs.next()) {
                y = rs.getInt("t");
                if(!returnList.contains(y)) {
                    returnList.add(y);
                }
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + endSql + "' 에러");
        }
        Collections.sort(returnList);
        return returnList;
    }

    public List<Double> getStartEndLatiLongi(TimeEnum timeEnum, String category, String element, String startTime, String endTime){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MIN(startLatitude) AS minLatitude, MIN(startLongitude) AS minLongitude, MAX(startLatitude) AS maxLatitude, MAX(startLongitude) AS maxLongitude ");
        sql.append("FROM '"+TABLE_DATA_GENERAL+"' WHERE main == '");
        sql.append(category);
        sql.append("' AND ");
        if(element != null) {
            sql.append("sub == '");
            sql.append(element);
            sql.append("' AND ");
        }
        sql.append("NOT startTime IS NULL AND NOT endTime IS NULL AND ");
        sql.append("(");
        // 시작 및 종료 일자가 일치 할 경우
        sql.append("(strftime('%Y', startTime) <= '"+endTime+"' AND strftime('%Y', endTime) >= '"+startTime+"') ");
        if(timeEnum != TimeEnum.YEAR) {
            sql.append("OR ");
            if(timeEnum == TimeEnum.MONTH) {
                // 시작 연도와 종료 연도가 1년 이상 차이 날 경우
                sql.append("((strftime('%Y', endTime) - strftime('%Y', startTime)) > 0)");
            } else if(timeEnum == TimeEnum.HOUR){
                // 시작 일자와 종료 일자가 1일 이상 차이 날 경우
                sql.append("((strftime('%s', endTime) - strftime('%s', startTime)) > 86400)");
            } else if(timeEnum == TimeEnum.WEEK) {
                // 시작 일자와 종료 일자가 1 주일 이상 차이 날 경우
                sql.append("((strftime('%s', endTime) - strftime('%s', startTime)) > 604800)");
            }
        }
        sql.append(");");
        return getStartEndLatiLongi(sql.toString());
    }

    public List<Double> getStartEndLatiLongi(){
        String sql = "SELECT MIN(startLatitude) AS minLatitude, MIN(startLongitude) AS minLongitude, MAX(startLatitude) AS maxLatitude, MAX(startLongitude) AS maxLongitude FROM data_general";
        return getStartEndLatiLongi(sql);
    }

    private List<Double> getStartEndLatiLongi(String sql){
        List<Double> returnList = new ArrayList<>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            double minLa = Double.MAX_VALUE;
            double minLo = Double.MAX_VALUE;
            double maxLa = Double.MIN_VALUE;
            double maxLo = Double.MIN_VALUE;
            while (rs.next()) {
                minLa = rs.getDouble("minLatitude");
                minLo = rs.getDouble("minLongitude");
                maxLa = rs.getDouble("maxLatitude");
                maxLo = rs.getDouble("maxLongitude");
            }
            if(minLa == 0 && minLo == 0 && maxLa == 0 && maxLo == 0) {
                return null;
            }
            returnList.add(minLa);
            returnList.add(minLo);
            returnList.add(maxLa);
            returnList.add(maxLo);
        } catch (SQLException e) {
            System.out.println("[SQLITE] : getDatasFromTime() 에러");
        }
        return returnList;
    }

//    INSERT
    public void deleteData(int num, String category) {
        try {
            String sql = "DELETE FROM \"" + DataDBQuery.TABLE_DATA_GENERAL + "\" WHERE num = ?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, num);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SQLITE] : 'DELETE FROM \"" + DataDBQuery.TABLE_DATA_GENERAL + "\" WHERE num = " + num + "' 에러");
        }

        boolean exists = false;
        try {
            String lastNumSelectSql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+DataDBQuery.TABLE_DATA_ADDITIONAL + category+"';";
            PreparedStatement lastNumSelectPstmt  = conn.prepareStatement(lastNumSelectSql);
            ResultSet rs  = lastNumSelectPstmt.executeQuery();
            while (rs.next()) {
                exists = true;
            }
        } catch(SQLException e) {
        }
        if(exists) {
            try {
                String sql = "DELETE FROM \"" + DataDBQuery.TABLE_DATA_ADDITIONAL + category + "\" WHERE target = ?;";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, num);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("[SQLITE] : 'DELETE FROM \"" + DataDBQuery.TABLE_DATA_ADDITIONAL +category+"\" WHERE target = " + num + "' 에러");
            }
        }
    }

    public void updateAdditionalDataName(String category, String element, int targetNum, String name, String type, String value) {
        String checkSql = "SELECT COUNT(*) AS c FROM \"" + DataDBQuery.TABLE_DATA_ADDITIONAL + category + "\" WHERE target = ?";
        int count = 0;
        try {
            PreparedStatement pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, targetNum);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt("c");
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + checkSql + "' 에러");
        }

        if(count < 1) {
            String sql = "INSERT INTO \"" + DataDBQuery.TABLE_DATA_ADDITIONAL + category + "\" (main, sub, \"" + name + "\", target) VALUES(?, ?, ?, ?)";
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, category);
                pstmt.setString(2, element);
                if(type.equals("INTEGER"))
                    pstmt.setInt(3, Integer.parseInt(value));
                else if(type.equals("REAL"))
                    pstmt.setDouble(3, Double.parseDouble(value));
                else if(type.equals("BOOLEAN"))
                    pstmt.setBoolean(3, Boolean.parseBoolean(value));
                else {
                    if(value.length() < 1 || value.equals("NULL"))
                        pstmt.setString(3, null);
                    else
                        pstmt.setString(3, value);
                }
                pstmt.setInt(4, targetNum);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("[SQLITE] : '" + sql + "' 에러");
            }
        } else {
            String sql = "UPDATE \"" + DataDBQuery.TABLE_DATA_ADDITIONAL + category + "\" SET \"" + name + "\" = ? WHERE target = ?;";
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                if(type.equals("INTEGER"))
                    pstmt.setInt(1, Integer.parseInt(value));
                else if(type.equals("REAL"))
                    pstmt.setDouble(1, Double.parseDouble(value));
                else if(type.equals("BOOLEAN"))
                    pstmt.setBoolean(1, Boolean.parseBoolean(value));
                else {
                    if(value.length() < 1 || value.equals("NULL"))
                        pstmt.setString(1, null);
                    else
                        pstmt.setString(1, value);
                }
                pstmt.setInt(2, targetNum);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("[SQLITE] : '" + sql + "' 에러");
            }
        }
    }

    public void deleteAdditionalData(String category, String name){
        additionalDataColumnUpdate(category, null, null, null, name);
    }

    public void createAdditionalDataTable(String category, String name, String type, String defaultValue){
        createBasicAdditionalDataTable(category);
        additionalDataColumnUpdate(category, name, type, defaultValue, null);
    }

//    UPDATE
    private void additionalDataColumnUpdate(String category, String addColumn, String addType, String addDefaultValue, String removeColumn){
        String query = null;
        List<ColumnObject> columns = getColumns(DataDBQuery.TABLE_DATA_ADDITIONAL + category);
        boolean exist = false;
        for(ColumnObject co : columns) {
            if(co.name.equals(removeColumn)) {
                exist = true;
            }
        }
        boolean addedNewColumn = false;

        if((removeColumn != null && exist) ||
            (addColumn != null && addType != null && ! columns.contains(addColumn))) {

            if(addColumn!=null && addType != null) {
                columns.add(new ColumnObject(addColumn, addType, addDefaultValue, false, false));
                addedNewColumn = true;
            }

            try {
                StringBuilder createBuilder = new StringBuilder();
                createBuilder.append("CREATE TABLE \"new_");
                createBuilder.append(DataDBQuery.TABLE_DATA_ADDITIONAL);
                createBuilder.append(category);
                createBuilder.append("\" (");

                ColumnObject co;
                for(int count = 0; count < columns.size(); count++) {
                    co = columns.get(count);
                    if( ! co.name.equals(removeColumn)) {
                        if(count != 0)
                            createBuilder.append(",");
                        createBuilder.append("\"");
                        createBuilder.append(co.name);
                        createBuilder.append("\"");
                        createBuilder.append(" ");
                        createBuilder.append(co.type);
                        if(co.primaryKey)
                            createBuilder.append(" PRIMARY KEY");
                        if(co.name.equals("num"))
                            createBuilder.append(" AUTOINCREMENT");
                        else if(co.notNull)
                            createBuilder.append(" NOT NULL");
                        if(co.defaultValue == null || co.defaultValue.equals("NULL")) {
                            if(co.type.equals("TEXT"))
                                createBuilder.append(" DEFAULT NULL");
                            else if(co.type.equals("INTEGER") || co.type.equals("REAL") || co.type.equals("LONG"))
                                createBuilder.append(" DEFAULT 0");
                        } else {
                            createBuilder.append(" DEFAULT ");
                            if(co.type.equals("TEXT")) {
                                createBuilder.append("\"");
                                createBuilder.append(co.defaultValue);
                                createBuilder.append("\"");
                            }
                            else
                                createBuilder.append(co.defaultValue);
                        }
                    }
                }
                createBuilder.append(");");
                query = createBuilder.toString();
                PreparedStatement createPstmt = conn.prepareStatement(createBuilder.toString());
                createPstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("[SQLITE] : '" + query + "' 에러");
                return;
            }

            try {
                StringBuilder insertBuilder = new StringBuilder();
                insertBuilder.append("INSERT INTO \"new_");
                insertBuilder.append(DataDBQuery.TABLE_DATA_ADDITIONAL);
                insertBuilder.append(category);
                insertBuilder.append("\" SELECT ");
                int size = columns.size();
                if(addedNewColumn) {
                    size--;
                }
                ColumnObject co;
                for(int count = 0; count < size; count++) {
                    co = columns.get(count);
                    if(!co.name.equals(removeColumn)) {
                        if(count != 0)
                            insertBuilder.append(",");
                        insertBuilder.append("`");
                        insertBuilder.append(co.name);
                        insertBuilder.append("`");
                    }
                }
                if(addedNewColumn) {
                    insertBuilder.append(",");
                    if(addDefaultValue == null || addDefaultValue.equals("NULL")) {
                        if(addType.equals("TEXT"))
                            insertBuilder.append("NULL");
                        else if(addType.equals("INTEGER") || addType.equals("REAL") || addType.equals("LONG"))
                            insertBuilder.append("0");
                    } else {
                        if(addType.equals("TEXT")) {
                            insertBuilder.append("\"");
                            insertBuilder.append(addDefaultValue);
                            insertBuilder.append("\"");
                        }
                        else
                            insertBuilder.append(addDefaultValue);
                    }
                }
                insertBuilder.append(" FROM \"");
                insertBuilder.append(DataDBQuery.TABLE_DATA_ADDITIONAL);
                insertBuilder.append(category);
                insertBuilder.append("\"");
                query = insertBuilder.toString();
                PreparedStatement insertPstmt = conn.prepareStatement(insertBuilder.toString());
                insertPstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("[SQLITE] : '" + query + "' 에러");
                return;
            }
            dropTable(DataDBQuery.TABLE_DATA_ADDITIONAL + category);
            renameTable("new_" + DataDBQuery.TABLE_DATA_ADDITIONAL + category, DataDBQuery.TABLE_DATA_ADDITIONAL + category);
        }
    }

    private Map<String, List<String>> getAdditionalDataMaps(List<DataObject> dataList){
        Map<String, List<String>> additionalDataMap = new HashMap<>();
        Map<String, String> defaultData = new HashMap<>();
        String main;
        List<String> additionalDatas;
        List<String> dataNames;
        List<String> addedList;
        String[] splitted;
        String typeAndName;
        for(DataObject dataObject : dataList){
            main = dataObject.getMain().getValue();
            additionalDatas = dataObject.getAdditionalData();
            dataNames = new ArrayList<>();
            for(String data : additionalDatas) {
                splitted = data.split(Main.SPLIT_SYMBOL);
                typeAndName = splitted[0] + Main.SPLIT_SYMBOL + splitted[1];
                if(!dataNames.contains(typeAndName))
                    dataNames.add(typeAndName);
                if(splitted.length > 3) {
                    if(!defaultData.containsKey(typeAndName)) {
                        defaultData.put(typeAndName, splitted[2]);
                    }
                }
            }
            if(additionalDataMap.containsKey(main)) {
                addedList = additionalDataMap.get(main);
                for(String willAdd : dataNames) {
                    if(!addedList.contains(willAdd))
                        addedList.add(willAdd);
                }
                additionalDataMap.remove(main);
                additionalDataMap.put(main, addedList);
            } else {
                additionalDataMap.put(main, dataNames);
            }
        }
        List<String> defaultAddedData;
        List<String> mainList = new ArrayList<>();
        mainList.addAll(additionalDataMap.keySet());
        for(String key : mainList) {
            defaultAddedData = new ArrayList<>();
            for(String additionalData : additionalDataMap.get(key)) {
                if(defaultData.containsKey(additionalData))
                    defaultAddedData.add(additionalData + Main.SPLIT_SYMBOL + defaultData.get(additionalData));
                else
                    defaultAddedData.add(additionalData);
            }
            additionalDataMap.remove(key);
            additionalDataMap.put(key, defaultAddedData);
        }
        return additionalDataMap;
    }


    public void dataMerge(String[] dbPathArray){
        createGeneralTable();
        String type = dbPathArray[0].substring(dbPathArray[0].lastIndexOf("."));
        if (type.equals(".db")) {
            mergeDb(dbPathArray);
        } else if (type.equals(".csv")) {
            mergeCsv(dbPathArray);
        }
    }

    public void mergeDb(String[] dbPathArray){
        DataAPI dapi;
        Map<String, List<String>> additionalDataInfoMap = new HashMap<>();
        List<String> categories;
        List<AdditionalDataColumnObject> adcoList;
        List<String> adcoStringfyList;
        String adcoStringfy;
        for(String path : dbPathArray) {
            dapi = new DataAPI(path);
            categories = dapi.getCategory();
            for(String category : categories) {
                adcoList = dapi.getAdditionalDataColumns(category);
                if(additionalDataInfoMap.containsKey(category)) {
                    adcoStringfyList = additionalDataInfoMap.get(category);
                } else {
                    adcoStringfyList = new ArrayList<>();
                }
                for(AdditionalDataColumnObject adco : adcoList) {
                    adcoStringfy = adco.type+ Main.SPLIT_SYMBOL+adco.name+Main.SPLIT_SYMBOL+adco.dflt_value;
                    if(!adcoStringfyList.contains(adcoStringfy)) {
                        adcoStringfyList.add(adcoStringfy);
                    }
                }
                additionalDataInfoMap.put(category, adcoStringfyList);
            }
            dapi.close();
        }
        for(String category : additionalDataInfoMap.keySet()) {
            createAdditionalDataTable(category, additionalDataInfoMap.get(category));
        }

        int page;
        List<DataObject> datas;
        for(String path : dbPathArray) {
            dapi = new DataAPI(path);
            page = 0;
            for(;;) {
                datas = dapi.getAllData(1000, page);
                if(datas.isEmpty()) {
                    break;
                } else {
                    insertData(datas);
                }
                page++;
            }
            dapi.close();
        }
    }

    private void mergeCsv(String[] csvPathArray){
        List<String> readedLine;
        List<String> additionalDataName;
        List<String> additionalDataType;
        List<String> additionalDataInfo;

        String category;
        String columnType;
        String columnName;
        String defaultData;
        int startLine;
        String[] splitted;

        String sub;
        String startTime;
        String endTime;
        int amount;
        double startLatitude;
        double startLongitude;
        double endLatitude;
        double endLongitude;
        List<String> additionalData;
        List<String> elementList;
        Map<String,List<String>> model = new HashMap<>();


        int latilongtiNull = 0;

        File f;
        String[] header;
        FileUtil fu = new FileUtil();

        DataObject dataObject;
        List<DataObject> dataObjectList;
        for(String path : csvPathArray) {
            category = path.substring(path.lastIndexOf(File.separator)+1, path.lastIndexOf("."));
            f = new File(path);
            additionalDataName = new ArrayList<>();
            additionalDataType = new ArrayList<>();
            additionalDataInfo = new ArrayList<>();
            try{
                header = fu.readFile(f, "MS949", 0, 1).get(0).split(",");
                for(int additionalCount = 8; additionalCount < header.length; additionalCount++) {
                    if(header[additionalCount].indexOf("::") == -1) {
                        columnType = "TEXT";
                        columnName = header[additionalCount];
                        defaultData = "NULL";
                        additionalDataType.add("TEXT");
                        additionalDataName.add(header[additionalCount]);
                        additionalDataInfo.add("TEXT"+Main.SPLIT_SYMBOL+header[additionalCount]+Main.SPLIT_SYMBOL+"NULL");
                    } else {
                        columnType = header[additionalCount].split("::")[0];
                        columnName = header[additionalCount].split("::")[1];
                        if(columnType.equalsIgnoreCase("INTEGER")||columnType.equalsIgnoreCase("REAL")
                                ||columnType.equalsIgnoreCase("LONG")) {
                            defaultData = "0";
                        } else if(columnType.equalsIgnoreCase("BOOLEAN")) {
                            defaultData = "FALSE";
                        } else {
                            defaultData = "NULL";
                        }
                    }
                    additionalDataType.add(columnType);
                    additionalDataName.add(columnName);
                    additionalDataInfo.add(columnType+Main.SPLIT_SYMBOL+columnName+Main.SPLIT_SYMBOL+defaultData);
                }
                createGeneralTable();
                createAdditionalDataTable(category, additionalDataInfo);
                startLine = 0;
                dataObjectList = new ArrayList<>();
                int additionalIndex;
                for(;;) {
                    readedLine = fu.readFile(f, "MS949", 1 + (startLine*2000),  (startLine*2000) + 2001);
                    startLine++;
                    if(readedLine.isEmpty()) {
                        break;
                    } else {
                        for(String line : readedLine) {
                            try {
                                splitted = line.split(",");
                                sub = splitted[0];
                                if(sub == null || sub.length() < 1) {
                                    sub = "기타";
                                }
                                startTime = splitted[1];
                                if(startTime == null || startTime.length() < 1) {
                                    continue;
                                }
                                endTime = splitted[2];
                                if(endTime == null || endTime.length() < 1) {
                                    continue;
                                }
                                if(splitted[3] == null || splitted[3].length() < 1) {
                                    amount = 1;
                                } else {
                                    amount = Integer.parseInt(splitted[3]);
                                }
                                if(splitted[4] == null || splitted[4].length() < 1) {
                                    latilongtiNull++;
                                    continue;
                                } else {
                                    startLatitude = Double.parseDouble(splitted[4]);
                                }
                                if(splitted[5] == null || splitted[5].length() < 1) {
                                    latilongtiNull++;
                                    continue;
                                } else {
                                    startLongitude = Double.parseDouble(splitted[5]);
                                }
                                if(splitted[6] == null || splitted[6].length() < 1) {
                                    latilongtiNull++;
                                    continue;
                                } else {
                                    endLatitude = Double.parseDouble(splitted[6]);
                                }
                                if(splitted[7] == null || splitted[7].length() < 1) {
                                    latilongtiNull++;
                                    continue;
                                } else {
                                    endLongitude = Double.parseDouble(splitted[7]);
                                }
                                additionalData = new ArrayList<>();

                                for(int additionalCount = 8; additionalCount < splitted.length; additionalCount++) {
                                    additionalIndex = additionalCount-8;
                                    if(additionalDataType.size() > additionalIndex && additionalDataName.size() > additionalIndex) {
                                        additionalData.add(additionalDataType.get(additionalIndex) + Main.SPLIT_SYMBOL + additionalDataName.get(additionalIndex) + Main.SPLIT_SYMBOL + splitted[additionalCount]);
                                    }
                                }
                                dataObject = new DataObject(category, sub, startTime, endTime, amount, startLatitude, startLongitude, endLatitude, endLongitude, additionalData);
                                if(model.containsKey(category)) {
                                    elementList = model.get(category);
                                    if(!elementList.contains(sub)) {
                                        elementList.add(sub);
                                        model.put(category, elementList);
                                    }
                                } else {
                                    elementList = new ArrayList<>();
                                    elementList.add(sub);
                                    model.put(category, elementList);
                                }
                                dataObjectList.add(dataObject);
                                if(dataObjectList.size() > 1000) {
                                    insertData(dataObjectList);
                                    dataObjectList.clear();
                                }
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                insertData(dataObjectList);
            } catch(IOException e){
            }
        }
        if(!model.isEmpty()) {
            MainController.modelDb.addModel(model);
        }
        if(latilongtiNull != 0) {
            System.out.println("[데이터 불러오기] : 위도, 경도 정보가 없는 " + latilongtiNull + "개의 데이터는 불러오지 못하였습니다!");
        }
    }
}