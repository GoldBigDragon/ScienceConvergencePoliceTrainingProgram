package goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.TimeEnum;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.enums.InsertType;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object.QueryObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object.TargetDifferenceObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.DataAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.object.VisualizationTableObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbsoluteDataAPI extends AbsoluteDataDBQuery {

    public AbsoluteDataAPI(String databaseName){
        connectDb(databaseName);
    }

    public List<String> getYears(){
        List<String> returnList = new ArrayList<>();
        String query = "SELECT targetTime FROM absolute_data_count WHERE timeType == 'YEAR' GROUP BY targetTime;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                returnList.add(rs.getString("targetTime"));
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : getYears() 에러");
        }
        return returnList;
    }

    public List<String> getVisualizationTarget(){
        List<String> returnList = new ArrayList<>();
        String query = "SELECT (category||'"+Main.SPLIT_SYMBOL+"'|| " +
                "CASE WHEN element IS NULL " +
                "THEN 'NULL' " +
                "ELSE element " +
                "END " +
                "||'"+Main.SPLIT_SYMBOL+"'|| " +
                "CASE WHEN subQuery IS NULL " +
                "THEN 'NULL' " +
                "ELSE subQuery " +
                "END " +
                ") AS element FROM "+ AbsoluteDataAPI.TABLE_ABSOLUTE_TARGET_DATA_COUNT + " " +
                "GROUP BY element, subQuery ORDER BY category, element, subQuery;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                returnList.add(rs.getString("element"));
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : getEnviromentVariable() 에러");
        }
        return returnList;
    }

    public List<String> getEnviromentVariable(){
        List<String> returnList = new ArrayList<>();
        String query = "SELECT (category||'" + Main.SPLIT_SYMBOL + "'||element) AS element FROM " + AbsoluteDataAPI.TABLE_ABSOLUTE_DATA_COUNT + " GROUP BY element ORDER BY category, element;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                returnList.add(rs.getString("element"));
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : getEnviromentVariable() 에러");
        }
        return returnList;
    }

    public boolean isDataCreatedWell(boolean isTarget){
        String startTimeSql = "SELECT COUNT(DISTINCT timeType) AS timeTypeAmount FROM ";
        if(isTarget) {
            startTimeSql += TABLE_ABSOLUTE_TARGET_DATA_COUNT + "; ";
        } else {
            startTimeSql += TABLE_ABSOLUTE_DATA_COUNT + "; ";
        }
        try {
            PreparedStatement pstmt = conn.prepareStatement(startTimeSql);
            ResultSet rs = pstmt.executeQuery();
            int timeTypeAmount = 0;
            while (rs.next()) {
                timeTypeAmount = rs.getInt("timeTypeAmount");
            }
            if(timeTypeAmount > 3) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + startTimeSql + "' 에러");
        }
        return false;
    }

    public List<Integer> getTimeGrade(TargetDifferenceObject tdo, TimeEnum te, boolean isTarget){
        List<Integer> returnList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT targetTime FROM ");
        if(tdo.sector != -1) {
            if(isTarget) {
                sql.append(TABLE_ABSOLUTE_TARGET_GEO_DATA_COUNT);
            } else {
                sql.append(TABLE_ABSOLUTE_GEO_DATA_COUNT);
            }
        } else {
            if(isTarget) {
                sql.append(TABLE_ABSOLUTE_TARGET_DATA_COUNT);
            } else {
                sql.append(TABLE_ABSOLUTE_DATA_COUNT);
            }
        }
        sql.append(" WHERE ");
        if(isTarget) {
            sql.append("dataType == '");
            sql.append(tdo.dataType);
            sql.append("' AND ");
        }
        if(tdo.category != null) {
            sql.append("category == '" + tdo.category + "' AND ");
        }
        if(tdo.element != null) {
            sql.append("element == '" + tdo.element + "' AND ");
        }
        if(tdo.sector != -1) {
            sql.append("sector == " + tdo.sector + " AND ");
        }
        sql.append("timeType == '" + te.name() + "' GROUP BY timeCount ORDER BY timeCount DESC LIMIT 7;");
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            ResultSet rs = pstmt.executeQuery();
            String targetTime;
            while (rs.next()) {
                targetTime = rs.getString("targetTime");
                returnList.add(Integer.parseInt(targetTime));
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + sql + "' 에러");
        }
        return returnList;
    }

    public List<String> getTargetAverageLoc(String category, String element, TimeEnum te, String timeString){
        List<String> returnList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT (startLatitude + endLatitude)/2 AS lati, (startLongitude + endLongitude)/2 AS longi FROM ");
        sb.append(DataAPI.TABLE_DATA_GENERAL);
        sb.append(" WHERE main == '" + category + "' AND ");
        if(element != null && ! element.equals("NULL")) {
            sb.append("sub == '" + element + "' AND ");
        }
        sb.append("startTime IS NOT NULL AND endTime IS NOT NULL AND (");
        sb.append("(strftime('"+te.symbol+"', startTime) <= '"+timeString+"' AND strftime('"+te.symbol+"', endTime) >= '"+timeString+"') ");
        if(te != TimeEnum.YEAR) {
            sb.append("OR ((strftime('%s', endTime) - strftime('%s', startTime)) > " + te.afterCount + ")");
        }
        sb.append(");");
        try {
            PreparedStatement pstmt = conn.prepareStatement(sb.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                returnList.add(rs.getDouble("lati") + Main.SPLIT_SYMBOL + rs.getDouble("longi"));
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : getTargetAverageLoc() 에러");
        }

        return returnList;
    }

    public int getEdgeTime(boolean isStartTime, QueryObject qo){
        StringBuilder sql = new StringBuilder();
        if(isStartTime){
            sql.append("SELECT MIN(STRFTIME('"+qo.timeEnum.symbol+"', startTime)");
        } else {
            sql.append("SELECT MAX(STRFTIME('"+qo.timeEnum.symbol+"', endTime)");
        }
        sql.append(") AS edgeTime ");
        sql.append("FROM 'data_general' ");
        sql.append("WHERE NOT startTime IS NULL AND NOT endTime IS NULL ");
        if(qo.category != null) {
            sql.append("AND main == '"+qo.category+"' ");
        }
        if(qo.element != null) {
            sql.append("AND sub == '"+qo.element+"' ");
        }
        if(qo.sectorObject != null) {
            sql.append("AND (startLatitude >= " + qo.sectorObject.startLati + " AND endLatitude <= "+qo.sectorObject.endLati+") ");
            sql.append("AND (startLongitude >= " + qo.sectorObject.startLongi + " AND endLongitude <= "+qo.sectorObject.endLongi+") ");
        }
        if(qo.subQuery != null) {
            sql.append("AND num IN (");
            sql.append("SELECT target FROM 'data_additional_" + qo.category + "' WHERE main == '" + qo.category+ "' AND ");
            if(qo.element != null) {
                sql.append("sub == '"+qo.element+"' AND ");
            }
            sql.append(qo.subQuery);
            sql.append(") ");
        }
        sql.append(";");
        try {
            ResultSet rs = conn.prepareStatement(sql.toString()).executeQuery();
            while (rs.next()) {
                return rs.getInt("edgeTime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(sql.toString());
            System.out.println("[SQLITE] : getEdgeTime() 에러");
        }
        return -1;
    }

    public int getAbsoluteTargetCount(QueryObject qo, String time, boolean returnValue){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) AS count ");
        sql.append("FROM 'data_general' ");
        sql.append("WHERE main == '"+qo.category+"' AND ");
        if(qo.element != null) {
            sql.append("sub == '"+qo.element+"' AND ");
        }
        if(qo.sectorObject != null) {
            sql.append("(startLatitude >= " + qo.sectorObject.startLati + " AND endLatitude <= "+qo.sectorObject.endLati+") AND ");
            sql.append("(startLongitude >= " + qo.sectorObject.startLongi + " AND endLongitude <= "+qo.sectorObject.endLongi+") AND ");
        }
        sql.append("NOT startTime IS NULL AND NOT endTime IS NULL AND (");
        sql.append("(strftime('"+qo.timeEnum.symbol+"', startTime) <= '"+time+"' AND strftime('"+qo.timeEnum.symbol+"', endTime) >= '"+time+"') ");
        if(qo.timeEnum != TimeEnum.YEAR) {
            sql.append("OR ((strftime('%s', endTime) - strftime('%s', startTime)) > " + qo.timeEnum.afterCount + ")");
        }
        sql.append(") ");
        if(qo.subQuery != null) {
            sql.append("AND num IN (");
            sql.append("SELECT target FROM 'data_additional_" + qo.category + "' WHERE main == '" + qo.category+ "' AND ");
            if(qo.element != null) {
                sql.append("sub == '"+qo.element+"' AND ");
            }
            sql.append(qo.subQuery);
            sql.append(")");
        }
        sql.append(";");
        try {
            ResultSet rs = conn.prepareStatement(sql.toString()).executeQuery();
            int count = 0;
            while (rs.next()) {
                count = rs.getInt("count");
            }
            if(count > 0) {
                if(returnValue) {
                    return count;
                } else {
                    if(staticBatchTarget == InsertType.ABSOLUTE_TARGET_COUNT) {
                        insertAbsoluteTargetCountData(qo, qo.timeEnum.name(), time, count);
                    } else if(staticBatchTarget == InsertType.ABSOLUTE_GEO_TARGET_COUNT) {
                        insertAbsoluteGeoTargetCountData(qo, qo.timeEnum.name(), time, count);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(sql.toString());
            System.out.println("[SQLITE] : getAbsoluteCount() 에러!");
        }
        return 0;
    }

    public boolean isVisualizationDb() {
        String selectSql = "SELECT name FROM sqlite_master WHERE type IN ('table', 'view') AND name NOT LIKE 'data_additional_%' UNION ALL SELECT name FROM sqlite_temp_master WHERE type IN ('table', 'view') ORDER BY 1;";
        List<String> defaultList = new ArrayList<>();
        defaultList.add(TABLE_SETTINGS);
        defaultList.add(TABLE_ABSOLUTE_TARGET_DATA_COUNT);
        defaultList.add(TABLE_ABSOLUTE_DATA_COUNT);
        defaultList.add(TABLE_ABSOLUTE_DATA_DIFFERENCE);
        defaultList.add(TABLE_ABSOLUTE_TARGET_GEO_DATA_COUNT);
        defaultList.add(TABLE_ABSOLUTE_GEO_DATA_COUNT);
        defaultList.add(TABLE_ABSOLUTE_GEO_DATA_DIFFERENCE);
        defaultList.add(TABLE_GEO_DATA_DISTANCE);
        List<String> getList = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(selectSql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                getList.add(rs.getString("name"));
            }
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : isVisualizationDb() 에러");
        }
        for(String table : defaultList) {
            if(!getList.contains(table)) {
                return false;
            }
        }
        return true;
    }


    public void dropDefaultTables(){
        String selectSql =
                "SELECT name FROM sqlite_master WHERE type IN ('table', 'view') AND name LIKE 'data_additional_%' UNION ALL SELECT name FROM sqlite_temp_master WHERE type IN ('table', 'view') ORDER BY 1;";
        List<String> defaultList = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(selectSql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                defaultList.add(rs.getString("name"));
            }
            ps.close();
            StringBuilder deleteSql = new StringBuilder();
            deleteSql.append("DROP TABLE IF EXISTS 'data_general'; ");
            for(String table : defaultList) {
                deleteSql.append("DROP TABLE IF EXISTS '" + table + "'; ");
            }
            deleteSql.append("DELETE FROM 'geo_data_distance' WHERE distance == 0; VACUUM;");
            Statement st = conn.createStatement();
            st.executeUpdate(deleteSql.toString());
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : dropDefaultTables() 에러");
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public int getSelectAbsoluteCountQuery(QueryObject qo, String timeString){
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) AS count ");
        query.append("FROM '"+ DataAPI.TABLE_DATA_GENERAL+"' ");
        query.append("WHERE main == '" + qo.category + "' AND ");
        query.append("sub == '" + qo.element + "' AND ");
        if(qo.sectorObject != null) {
            query.append("(startLatitude >= " + qo.sectorObject.startLati + " AND endLatitude <= " + qo.sectorObject.endLati + ") AND ");
            query.append("(startLongitude >= "+ qo.sectorObject.startLongi +" AND endLongitude <= " + qo.sectorObject.endLongi + ") AND ");
        }
        query.append("NOT startTime IS NULL AND NOT endTime IS NULL AND (");
        query.append("(strftime('"+qo.timeEnum.symbol+"', startTime) <= '" + timeString + "' AND strftime('" + qo.timeEnum.symbol + "', endTime) >= '" + timeString + "') ");
        if(qo.timeEnum != TimeEnum.YEAR) {
            query.append("OR ((strftime('%s', endTime) - strftime('%s', startTime)) > " + qo.timeEnum.afterCount + "));");
        }
        query.append(");");
        try {
            ResultSet rs = conn.prepareStatement(query.toString()).executeQuery();
            int count = 0;
            while (rs.next()) {
                count = rs.getInt("count");
            }
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getSelectAbsoluteCountQuery() 에러!");
        }
        return 0;
    }


    public Map<String, Double> getSettings(){
        Map<String, Double> returnMap = new HashMap<>();
        String sql = "SELECT * FROM setting;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                returnMap.put("rows", (double)rs.getInt("rows"));
                returnMap.put("columns", (double)rs.getInt("columns"));
                returnMap.put("startLatitude", rs.getDouble("startLatitude"));
                returnMap.put("startLongitude", rs.getDouble("startLongitude"));
                returnMap.put("endLatitude", rs.getDouble("endLatitude"));
                returnMap.put("endLongitude", rs.getDouble("endLongitude"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getSettings() 에러");
        }
        return returnMap;
    }

    public Map<String, Integer> getDataInfo(){
        Map<String, Integer> returnMap = new HashMap<>();
        String sql = "SELECT " +
                "(SELECT COUNT(DISTINCT element) FROM absolute_data_count) AS enviromentCount, " +
                "(SELECT COUNT(DISTINCT (CASE WHEN element IS NULL THEN '' ELSE element END)) FROM absolute_target_data_count GROUP BY category, element, subQuery) AS visualizationCount, " +
                "(SELECT (SELECT COUNT(*) FROM absolute_target_data_count)+(SELECT COUNT(*) FROM absolute_data_count)+(SELECT COUNT(*) FROM absolute_data_difference) " +
                "+(SELECT COUNT(*) FROM absolute_geo_data_count)+(SELECT COUNT(*) FROM absolute_geo_data_difference)+(SELECT COUNT(*) FROM absolute_target_geo_data_count) " +
                "+(SELECT COUNT(*) FROM geo_data_distance) " +
                ") AS totalAmount "+
                "LIMIT 1;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                returnMap.put("enviromentCount", rs.getInt("enviromentCount"));
                returnMap.put("visualizationCount", rs.getInt("visualizationCount"));
                returnMap.put("totalAmount", rs.getInt("totalAmount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getSettings() 에러");
        }
        return returnMap;
    }


    public Map<String, String> getVisualizationInfo(String targetCategory, String targetElement){
        Map<String, String> returnMap = new HashMap<>();
        String sameCategory = "";
        if(targetElement != null && !targetElement.equals("NULL")) {
            sameCategory = "AND category != targetCategory AND element != targetElement ";
        }
        String sql = "SELECT " +
                "(SELECT ele||' ('||disAvg||'m)' FROM (SELECT (category||CASE WHEN element IS NULL THEN '' ELSE '::'||element END) AS ele, ROUND(AVG(distance), 3) AS disAvg FROM geo_data_distance WHERE targetCategory == '"+targetCategory+"' AND targetElement == '"+targetElement+"' "+sameCategory+"GROUP BY ele ORDER BY disAvg LIMIT 1)) AS shortestDistance ";
        sql += ",(SELECT ele||' ('||disAvg||'m)' FROM (SELECT (category||CASE WHEN element IS NULL THEN '' ELSE '::'||element END) AS ele, ROUND(AVG(distance), 3) AS disAvg FROM geo_data_distance WHERE targetCategory == '"+targetCategory+"' AND targetElement == '"+targetElement+"' "+sameCategory+"GROUP BY ele ORDER BY disAvg DESC LIMIT 1)) AS longestDistance ";
        sql += ",(SELECT (category||CASE WHEN element IS NULL THEN '' ELSE '::'||element END||' ('||ROUND(yearCount, 3)||')') AS ele FROM absolute_data_difference WHERE targetCategory == '"+targetCategory+"' AND targetElement =='"+targetElement+"' "+sameCategory+"GROUP BY ele ORDER BY yearCount DESC LIMIT 1) AS mostTimeDifference ";
        sql += ",(SELECT (category||CASE WHEN element IS NULL THEN '' ELSE '::'||element END||' ('||ROUND(yearCount, 3)||')') AS ele FROM absolute_data_difference WHERE targetCategory == '"+targetCategory+"' AND targetElement == '"+targetElement+"' "+sameCategory+"GROUP BY ele ORDER BY yearCount LIMIT 1) AS leastTimeDifference " +
                ";";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                returnMap.put("shortestDistance", rs.getString("shortestDistance"));
                returnMap.put("longestDistance", rs.getString("longestDistance"));
                returnMap.put("mostTimeDifference", rs.getString("mostTimeDifference"));
                returnMap.put("leastTimeDifference", rs.getString("leastTimeDifference"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getVisualizationInfo() 에러");
        }
        return returnMap;
    }

    // Visualization - Map
    public Map<Integer, Map<String, Double>> getAbsoluteAmountOfEnviroment(TimeEnum te, String startTime, String endTime, List<String> enviromentTargets){
        Map<Integer, Map<String, Double>> returnMap = new HashMap<>();
        String enviromentQuery = "SELECT sector, (category||'"+Main.SPLIT_SYMBOL+"'||element) as element, SUM(timeCount) AS amount FROM 'absolute_geo_data_count' WHERE ";
        if(te == TimeEnum.ALL) {
            enviromentQuery += "timeType == 'YEAR' AND (";
        } else {
            enviromentQuery += "timeType == '"+te.name()+"' AND ";
            if(startTime != null) {
                enviromentQuery += "targetTime >= '"+startTime+"' AND ";
            }
            if(endTime != null) {
                enviromentQuery += "targetTime <= '"+endTime+"' AND ";
            }
            enviromentQuery += "(";
        }
        String[] splitted;
        for(String target : enviromentTargets) {
            splitted = target.split(Main.SPLIT_SYMBOL);
            enviromentQuery += "(category == '" + splitted[0] + "' AND element == '" + splitted[1] + "') OR ";
        }
        enviromentQuery += " 1 != 1 ) GROUP BY sector, category, element ORDER BY sector;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(enviromentQuery);
            ResultSet rs = pstmt.executeQuery();
            int sector;
            String element;
            double value;
            Map<String, Double> tempAmount;
            while (rs.next()) {
                sector = rs.getInt("sector");
                element = rs.getString("element");
                value = rs.getDouble("amount");
                if(returnMap.containsKey(sector)) {
                    tempAmount = returnMap.get(sector);
                } else {
                    tempAmount = new HashMap<>();
                }
                tempAmount.put(element, value);
                returnMap.put(sector, tempAmount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getAbsoluteAmountOfEnviroment() 에러");
        }
        return returnMap;
    }

    public Map<Integer, Map<String, Double>> getAbsoluteAmountOfVisualizationTarget(TimeEnum te, String startTime, String endTime, List<String> visualizationTargets){
        Map<Integer, Map<String, Double>> returnMap = new HashMap<>();
        String enviromentQuery = "SELECT sector, (category || '"+Main.SPLIT_SYMBOL+"' || " +
                "CASE WHEN element IS NULL THEN 'NULL' ELSE element END || '" +Main.SPLIT_SYMBOL+"' || "+
                "CASE WHEN subQuery IS NULL THEN 'NULL' ELSE subQuery END " +
                ") as element, SUM(timeCount) AS amount FROM 'absolute_target_geo_data_count' WHERE ";
        if(te == TimeEnum.ALL) {
            enviromentQuery += "timeType == 'YEAR' AND (";
        } else {
            enviromentQuery += "timeType == '"+te.name()+"' AND ";
            if(startTime != null) {
                enviromentQuery += "targetTime >= '"+startTime+"' AND ";
            }
            if(endTime != null) {
                enviromentQuery += "targetTime <= '"+endTime+"' AND ";
            }
            enviromentQuery += "(";
        }
        String[] splitted;
        for(String target : visualizationTargets) {
            splitted = target.split(Main.SPLIT_SYMBOL);
            enviromentQuery += "(category == '" + splitted[0] + "' ";
            if(!splitted[1].equals("NULL")) {
                enviromentQuery += "AND element == '" + splitted[1] + "' ";
            }
            if(!splitted[2].equals("NULL")) {
                enviromentQuery += "AND subQuery == \"" + splitted[2] + "\" ";
            }
            enviromentQuery += ") OR ";
        }
        enviromentQuery += " 1 != 1 ) GROUP BY sector, category, element, subQuery ORDER BY sector;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(enviromentQuery);
            ResultSet rs = pstmt.executeQuery();
            int sector;
            String element;
            double value;
            Map<String, Double> tempAmount;
            while (rs.next()) {
                sector = rs.getInt("sector");
                element = rs.getString("element");
                value = rs.getDouble("amount");
                if(returnMap.containsKey(sector)) {
                    tempAmount = returnMap.get(sector);
                } else {
                    tempAmount = new HashMap<>();
                }
                tempAmount.put(element, value);
                returnMap.put(sector, tempAmount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getAbsoluteAmountOfVisualizationTarget() 에러");
        }
        return returnMap;
    }

    public Map<Integer, Map<String, Double>> getGeoAbsoluteDifference(TimeEnum te, double point, String targetCategory, String targetElement, List<String> enviromentVariables){
        Map<Integer, Map<String, Double>> returnMap = new HashMap<>();
        String differenceQuery = "SELECT sector, (category || '"+Main.SPLIT_SYMBOL+"' || " +
                "CASE WHEN element IS NULL THEN 'NULL' ELSE element END" +
                ") as element, ("+point+"/";
        if(te == TimeEnum.ALL) {
            differenceQuery += "totalAverage";
        } else if(te==TimeEnum.YEAR){
            differenceQuery += "yearCount";
        } else if(te==TimeEnum.MONTH){
            differenceQuery += "monthCount";
        } else if(te==TimeEnum.WEEK){
            differenceQuery += "weekCount";
        } else if(te==TimeEnum.HOUR){
            differenceQuery += "hourCount";
        }
        differenceQuery += ") AS difference FROM 'absolute_geo_data_difference' WHERE (";
        String[] splitted;
        for(String env : enviromentVariables) {
            splitted = env.split(Main.SPLIT_SYMBOL);
            differenceQuery += "(category == '" + splitted[0] + "' ";
            if(!splitted[1].equals("NULL")) {
                differenceQuery += "AND element == '" + splitted[1] + "' ";
            }
            differenceQuery += ") OR ";
        }
        differenceQuery += " 1 != 1 ) AND targetCategory == '" + targetCategory+"' AND ";
        if(targetElement != null && ! targetElement.equals("NULL")) {
            differenceQuery += "targetElement == '" + targetElement+"' AND ";
        }
        if(te == TimeEnum.ALL) {
            differenceQuery += "totalAverage ";
        } else if(te==TimeEnum.YEAR){
            differenceQuery += "yearCount ";
        } else if(te==TimeEnum.MONTH){
            differenceQuery += "monthCount ";
        } else if(te==TimeEnum.WEEK){
            differenceQuery += "weekCount ";
        } else if(te==TimeEnum.HOUR){
            differenceQuery += "hourCount ";
        }
        differenceQuery += "!= 0 GROUP BY sector, category, element ORDER BY sector;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(differenceQuery);
            ResultSet rs = pstmt.executeQuery();
            int sector;
            String element;
            double value;
            Map<String, Double> tempAmount;
            while (rs.next()) {
                sector = rs.getInt("sector");
                element = rs.getString("element");
                value = rs.getDouble("difference");
                if(returnMap.containsKey(sector)) {
                    tempAmount = returnMap.get(sector);
                } else {
                    tempAmount = new HashMap<>();
                }
                tempAmount.put(element, value);
                returnMap.put(sector, tempAmount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getGeoAbsoluteDifference() 에러");
        }
        return returnMap;
    }

    public Map<String, Double> getAbsoluteDifference(TimeEnum te, double point, String targetCategory, String targetElement, List<String> enviromentVariables){
        Map<String, Double> returnMap = new HashMap<>();
        String differenceQuery = "SELECT (category || '"+Main.SPLIT_SYMBOL+"' || " +
                "CASE WHEN element IS NULL THEN 'NULL' ELSE element END" +
                ") as element, ("+point+"/";
        if(te == TimeEnum.ALL) {
            differenceQuery += "totalAverage";
        } else if(te==TimeEnum.YEAR){
            differenceQuery += "yearCount";
        } else if(te==TimeEnum.MONTH){
            differenceQuery += "monthCount";
        } else if(te==TimeEnum.WEEK){
            differenceQuery += "weekCount";
        } else if(te==TimeEnum.HOUR){
            differenceQuery += "hourCount";
        }
        differenceQuery += ") AS difference FROM 'absolute_data_difference' WHERE (";
        String[] splitted;
        for(String env : enviromentVariables) {
            splitted = env.split(Main.SPLIT_SYMBOL);
            differenceQuery += "(category == '" + splitted[0] + "' ";
            if(!splitted[1].equals("NULL")) {
                differenceQuery += "AND element == '" + splitted[1] + "' ";
            }
            differenceQuery += ") OR ";
        }
        differenceQuery += " 1 != 1 ) AND targetCategory == '" + targetCategory+"' AND ";
        if(targetElement != null && ! targetElement.equals("NULL")) {
            differenceQuery += "targetElement == '" + targetElement+"' AND ";
        }
        if(te == TimeEnum.ALL) {
            differenceQuery += "totalAverage ";
        } else if(te==TimeEnum.YEAR){
            differenceQuery += "yearCount ";
        } else if(te==TimeEnum.MONTH){
            differenceQuery += "monthCount ";
        } else if(te==TimeEnum.WEEK){
            differenceQuery += "weekCount ";
        } else if(te==TimeEnum.HOUR){
            differenceQuery += "hourCount ";
        }
        differenceQuery += "!= 0 GROUP BY category, element ORDER BY category;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(differenceQuery);
            ResultSet rs = pstmt.executeQuery();
            String element;
            double value;
            Map<String, Double> tempAmount;
            while (rs.next()) {
                element = rs.getString("element");
                value = rs.getDouble("difference");
                returnMap.put(element, value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getAbsoluteDifference() 에러");
        }
        return returnMap;
    }

    public Map<Integer, Map<String, Double>> getGeoDistance(TimeEnum te, String timeString, double point, String targetCategory, String targetElement, List<String> enviromentVariables){
        Map<Integer, Map<String, Double>> returnMap = new HashMap<>();
        String distanceQuery = "SELECT sector, (category || '"+Main.SPLIT_SYMBOL+"' || " +
                "CASE WHEN element IS NULL THEN 'NULL' ELSE element END" +
                ") as element, ("+point+"/(AVG(distance))) AS distance FROM 'geo_data_distance' WHERE (";
        String[] splitted;
        for(String env : enviromentVariables) {
            splitted = env.split(Main.SPLIT_SYMBOL);
            distanceQuery += "(category == '" + splitted[0] + "' ";
            if(!splitted[1].equals("NULL")) {
                distanceQuery += "AND element == '" + splitted[1] + "' ";
            }
            distanceQuery += ") OR ";
        }
        distanceQuery += " 1 != 1 ) AND targetCategory == '" + targetCategory+"' ";
        if(targetElement != null && ! targetElement.equals("NULL")) {
            distanceQuery += "AND targetElement == '" + targetElement+"' ";
        }
        if(te != TimeEnum.ALL) {
            distanceQuery += "AND timeType == '"+te.name()+"' ";
            if(timeString != null) {
                distanceQuery += "AND timeString == '" + timeString + "' ";
            }
        }
        distanceQuery += "GROUP BY sector, category, element ORDER BY sector;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(distanceQuery);
            ResultSet rs = pstmt.executeQuery();
            int sector;
            String element;
            double value;
            Map<String, Double> tempAmount;
            while (rs.next()) {
                sector = rs.getInt("sector");
                element = rs.getString("element");
                value = rs.getDouble("distance");
                if(returnMap.containsKey(sector)) {
                    tempAmount = returnMap.get(sector);
                } else {
                    tempAmount = new HashMap<>();
                }
                tempAmount.put(element, value);
                returnMap.put(sector, tempAmount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getGeoDistance() 에러");
        }
        return returnMap;
    }


    // Visualization - LineChart
    public Map<String, Map<String, Double>> getAbsoluteAmountOfEnviroment(TimeEnum te, List<String> enviromentTargets, String startTime, String endTime, int sector, boolean desc){
        // element <time, amount>
        Map<String, Map<String, Double>> returnMap = new HashMap<>();
        String enviromentQuery = "SELECT (category||" +
                "CASE WHEN element IS NULL " +
                "THEN '' " +
                "ELSE ('::'||element) " +
                "END " +
                ") AS element, targetTime, SUM(timeCount) as total FROM ";
        if(sector == -1) {
            enviromentQuery += "'absolute_data_count' WHERE ";
        } else {
            enviromentQuery += "'absolute_geo_data_count' WHERE sector == " + sector + " AND ";
        }
        enviromentQuery += "timeType == '"+te.name()+"' AND targetTime >= '"+startTime+"' AND targetTime <= '"+endTime+"' AND (";
        String[] splitted;
        for(String target : enviromentTargets) {
            splitted = target.split(Main.SPLIT_SYMBOL);
            enviromentQuery += "(category == '" + splitted[0] + "' AND element == '" + splitted[1] + "') OR ";
        }
        enviromentQuery += " 1 != 1 ) GROUP BY category, element, timeType, targetTime ORDER BY targetTime";
        if(desc) {
            enviromentQuery += " DESC";
        }
        enviromentQuery += ";";
        try {
            PreparedStatement pstmt = conn.prepareStatement(enviromentQuery);
            ResultSet rs = pstmt.executeQuery();
            // element <time, amount>
            String element;
            String time;
            int amount;
            Map<String, Double> tempMap;
            while (rs.next()) {
                element = rs.getString("element");
                if(returnMap.containsKey(element)){
                    tempMap = returnMap.get(element);
                } else {
                    tempMap = new HashMap<>();
                }
                time = rs.getString("targetTime");
                amount = rs.getInt("total");
                tempMap.put(time, (double)amount);
                returnMap.put(element, tempMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getAbsoluteAmountOfEnviroment() 에러");
        }
        return returnMap;
    }

    public Map<String, Map<String, Double>> getAbsoluteAmountOfVisualizationTarget(TimeEnum te, List<String> visualizationTargets, String startTime, String endTime, int sector, boolean desc){
        // element <time, amount>
        Map<String, Map<String, Double>> returnMap = new HashMap<>();
        String enviromentQuery = "SELECT (category || " +
                "CASE WHEN element IS NULL " +
                "THEN '' " +
                "ELSE ('::'||element) " +
                "END || " +
                "CASE WHEN subQuery IS NULL " +
                "THEN '' " +
                "ELSE ('::'||subQuery) " +
                "END " +
                ") AS element, timeType, targetTime, SUM(timeCount) as total  FROM ";
        if(sector == -1) {
            enviromentQuery += "'absolute_target_data_count' WHERE ";
        } else {
            enviromentQuery += "'absolute_target_geo_data_count' WHERE sector == " + sector + " AND ";
        }
        enviromentQuery += "timeType == '"+te.name()+"' AND targetTime >= '"+startTime+"' AND targetTime <= '"+endTime+"' AND (";
        String[] splitted;
        for(String target : visualizationTargets) {
            splitted = target.split(Main.SPLIT_SYMBOL);
            enviromentQuery += "(category == '" + splitted[0] + "' ";
            if(!splitted[1].equals("NULL")) {
                enviromentQuery += "AND element == '" + splitted[1] + "' ";
            }
            if(!splitted[2].equals("NULL")) {
                enviromentQuery += "AND subQuery == '" + splitted[2] + "' ";
            }
            enviromentQuery += ") OR ";
        }
        enviromentQuery += " 1 != 1 ) GROUP BY category, element, timeType, targetTime ORDER BY targetTime";
        if(desc) {
            enviromentQuery += " DESC";
        }
        enviromentQuery += ";";
        try {
            PreparedStatement pstmt = conn.prepareStatement(enviromentQuery);
            ResultSet rs = pstmt.executeQuery();
            String element;
            String time;
            int amount;
            Map<String, Double> tempMap;
            while (rs.next()) {
                element = rs.getString("element");
                if(returnMap.containsKey(element)){
                    tempMap = returnMap.get(element);
                } else {
                    tempMap = new HashMap<>();
                }
                time = rs.getString("targetTime");
                amount = rs.getInt("total");
                tempMap.put(time, (double)amount);
                returnMap.put(element, tempMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getAbsoluteAmountOfVisualizationTarget() 에러");
        }
        return returnMap;
    }


    // Visualization - Other
    public List<VisualizationTableObject> getAbsoluteAmountOfEnviroment(TimeEnum te, String startTime, String endTime, List<String> enviromentTargets, int targetSector, boolean desc){
        List<VisualizationTableObject> returnList = new ArrayList<>();
        String enviromentQuery = "SELECT sector, category, "+
                "CASE WHEN element IS NULL THEN '' ELSE element END AS element, "+
                "timeType, targetTime, SUM(timeCount) AS amount FROM 'absolute_geo_data_count' WHERE ";
        if(targetSector != -1) {
            enviromentQuery += "sector == "+targetSector+" AND ";
        }
        if(te == TimeEnum.ALL) {
            enviromentQuery += "timeType == 'YEAR' AND (";
        } else if(startTime != null) {
            enviromentQuery += "timeType == '"+te.name()+"' AND targetTime >= '"+startTime+"' AND targetTime <= '"+endTime+"' AND (";
        }
        String[] splitted;
        for(String target : enviromentTargets) {
            splitted = target.split(Main.SPLIT_SYMBOL);
            enviromentQuery += "(category == '" + splitted[0] + "' AND element == '" + splitted[1] + "') OR ";
        }
        enviromentQuery += " 1 != 1 ) GROUP BY sector, category, element, timeType, targetTime ORDER BY amount";
        if(desc)
            enviromentQuery += " DESC";
        enviromentQuery += ";";
        try {
            PreparedStatement pstmt = conn.prepareStatement(enviromentQuery);
            ResultSet rs = pstmt.executeQuery();
            VisualizationTableObject vto;
            while (rs.next()) {
                vto = new VisualizationTableObject(0, (rs.getInt("sector")+1), rs.getString("category"), rs.getString("element"), null, TimeEnum.valueOf(rs.getString("timeType")), rs.getString("targetTime"), rs.getDouble("amount"), true);
                returnList.add(vto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getAbsoluteAmountOfEnviroment() 에러");
        }
        return returnList;
    }

    public List<VisualizationTableObject> getAbsoluteAmountOfVisualizationTarget(TimeEnum te, String startTime, String endTime, List<String> visualizationTargets, int targetSector, boolean desc){
        List<VisualizationTableObject> returnList = new ArrayList<>();
        String enviromentQuery = "SELECT sector, category, "+
                "CASE WHEN element IS NULL THEN '' ELSE element END AS element, "+
                "CASE WHEN subQuery IS NULL THEN '' ELSE subQuery END AS subQuery, timeType, targetTime, SUM(timeCount) AS amount FROM 'absolute_target_geo_data_count' WHERE ";
        if(targetSector != -1) {
            enviromentQuery += "sector == "+targetSector+" AND ";
        }
        if(te == TimeEnum.ALL) {
            enviromentQuery += "timeType == 'YEAR' AND (";
        } else if(startTime != null) {
            enviromentQuery += "timeType == '"+te.name()+"' AND targetTime >= '"+startTime+"' AND targetTime <= '"+endTime+"' AND (";
        }
        String[] splitted;
        for(String target : visualizationTargets) {
            splitted = target.split(Main.SPLIT_SYMBOL);
            enviromentQuery += "(category == '" + splitted[0] + "' ";
            if(!splitted[1].equals("NULL")) {
                enviromentQuery += "AND element == '" + splitted[1] + "' ";
            }
            if(!splitted[2].equals("NULL")) {
                enviromentQuery += "AND subQuery == \"" + splitted[2] + "\" ";
            }
            enviromentQuery += ") OR ";
        }
        enviromentQuery += " 1 != 1 ) GROUP BY sector, category, element, subQuery, timeType, targetTime ORDER BY amount";
        if(desc)
            enviromentQuery += " DESC";
        enviromentQuery += ";";
        try {
            PreparedStatement pstmt = conn.prepareStatement(enviromentQuery);
            ResultSet rs = pstmt.executeQuery();
            VisualizationTableObject vto;
            while (rs.next()) {
                vto = new VisualizationTableObject(0, (rs.getInt("sector")+1), rs.getString("category"), rs.getString("element"), rs.getString("subQuery"), TimeEnum.valueOf(rs.getString("timeType")), rs.getString("targetTime"), rs.getDouble("amount"), true);
                returnList.add(vto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getAbsoluteAmountOfEnviroment() 에러");
        }
        return returnList;
    }


    public Map<String, Double> getEqualizer(){
        Map<String, Double> returnMap = new HashMap<>();
        String enviromentQuery = "SELECT (category||'"+Main.SPLIT_SYMBOL+"'||element) AS key, (1.0/SUM(timeCount)) AS bonus " +
                "FROM absolute_data_count WHERE timeType == 'YEAR' GROUP BY element;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(enviromentQuery);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                returnMap.put(rs.getString("key"), rs.getDouble("bonus"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : getEqualizer() 에러");
        }
        return returnMap;
    }
}