package goldbigdragon.github.io.util;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.DatabaseType;
import goldbigdragon.github.io.object.ColumnObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.DataDBQuery;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.ModelDBQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHandler {

    private String databaseName;
    protected Connection conn = null;

    public void connectDb(String databaseName){
        if(databaseName == null) {
            databaseName = Long.toString(Main.mainVariables.utc);
        }
        if(!(databaseName.indexOf(".") != -1 && databaseName.substring(databaseName.lastIndexOf(".")).equals(".db"))) {
            databaseName += ".db";
        }
        try {
            String url = "jdbc:sqlite:" + databaseName;
            this.conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : " + databaseName + " 연결 실패");
        }
        this.databaseName = databaseName;
    }

    public void close() {
        try {
            if (this.conn != null) {
                this.conn.close();
            }
        } catch (SQLException ex) {
            System.out.println("[SQLITE] : DB 연결 종료 실패");
        }
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public List<String> getTables() {
        List<String> tables = new ArrayList<>();
        String sql = "SELECT name FROM sqlite_master WHERE type IN ('table', 'view') AND name NOT LIKE 'sqlite_%' UNION ALL SELECT name FROM sqlite_temp_master WHERE type IN ('table', 'view') ORDER BY 1;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tables.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + sql + "' 에러");
        }
        return tables;
    }

    public List<ColumnObject> getColumns(String tableName){
        List<ColumnObject> returnList = new ArrayList<>();
        try {
            String getColumnsQuery = "SELECT * FROM pragma_table_info(\"" + tableName + "\");";
            PreparedStatement columnSelectPstmt = conn.prepareStatement(getColumnsQuery);
            ResultSet rs = columnSelectPstmt.executeQuery();
            String name;
            String type;
            String defaultValue = null;
            boolean notNull;
            boolean primaryKey;
            while (rs.next()) {
                name = rs.getString("name");
                type = rs.getString("type");
                defaultValue = rs.getString("dflt_value");
                if(defaultValue != null)
                    defaultValue = defaultValue.replace("\"", "");
                if(rs.getInt("notnull") == 0)
                    notNull = false;
                else
                    notNull = true;
                if(rs.getInt("pk") == 0)
                    primaryKey = false;
                else
                    primaryKey = true;
                returnList.add(new ColumnObject(name, type, defaultValue, notNull, primaryKey));
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + tableName + "'테이블 속 컬럼 정보 불러오기 실패!");
        }
        return returnList;
    }

    public void dropTable(String tableName){
        try {
            StringBuilder dropBuilder = new StringBuilder();
            dropBuilder.append("DROP TABLE IF EXISTS \"");
            dropBuilder.append(tableName);
            dropBuilder.append("\";");
            PreparedStatement dropPstmt = conn.prepareStatement(dropBuilder.toString());
            dropPstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + tableName + "' 테이블 제거 실패!");
            return;
        }
    }

    public void renameTable(String originalName, String newName) {
        try {
            StringBuilder renameBuilder = new StringBuilder();
            renameBuilder.append("ALTER TABLE \"");
            renameBuilder.append(originalName);
            renameBuilder.append("\" RENAME TO \"");
            renameBuilder.append(newName);
            renameBuilder.append("\";");
            PreparedStatement renamePstmt = conn.prepareStatement(renameBuilder.toString());
            renamePstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + originalName + "'에서 '" + newName + "'로 테이블 이름 변경 실패!");
            return;
        }
    }

    public DatabaseType getDbType(){
        List<String> tables = getTables();
        if(tables.contains(ModelDBQuery.TABLE_MODEL_POINT) && tables.contains(ModelDBQuery.TABLE_MODEL_CATEGORY)) {
            List<ColumnObject> pointColumns = getColumns(ModelDBQuery.TABLE_MODEL_POINT);
            List<ColumnObject> categoryColumns = getColumns(ModelDBQuery.TABLE_MODEL_CATEGORY);
            List<String> originalPointColumns = new ArrayList<>();
            originalPointColumns.add("name:TEXT");
            originalPointColumns.add("num:INTEGER");
            List<String> originalCategoryColumns = new ArrayList<>();
            originalCategoryColumns.add("num:INTEGER");
            originalCategoryColumns.add("main:TEXT");
            originalCategoryColumns.add("sub:TEXT");
            boolean rightPoint = checkColumnEquals(pointColumns, originalPointColumns);
            boolean rightCategory = checkColumnEquals(categoryColumns, originalCategoryColumns);

            if(rightCategory && rightPoint) {
                return DatabaseType.CRIMINAL_STATISTICS_ANALYSIS_MODEL;
            }
        } else if(tables.contains(DataDBQuery.TABLE_DATA_GENERAL)) {
            List<ColumnObject> generalColumns = getColumns(DataDBQuery.TABLE_DATA_GENERAL);
            List<String> originalGeneralColumns = new ArrayList<>();
            originalGeneralColumns.add("num:INTEGER");
            originalGeneralColumns.add("main:TEXT");
            originalGeneralColumns.add("sub:TEXT");
            originalGeneralColumns.add("startTime:TEXT");
            originalGeneralColumns.add("endTime:TEXT");
            originalGeneralColumns.add("amount:INTEGER");
            originalGeneralColumns.add("startLatitude:REAL");
            originalGeneralColumns.add("startLongitude:REAL");
            originalGeneralColumns.add("endLatitude:REAL");
            originalGeneralColumns.add("endLongitude:REAL");
            boolean rightGeneral = checkColumnEquals(generalColumns, originalGeneralColumns);

            if(rightGeneral) {
                return DatabaseType.CRIMINAL_STATISTICS_ANALYSIS_DATA;
            }
        }
        return DatabaseType.UNDEFINED;
    }

    private boolean checkColumnEquals(List<ColumnObject> columns, List<String> originalColumns){
        List<String> columnList = new ArrayList<>();
        for(ColumnObject co : columns) {
            columnList.add(co.name + ":" + co.type);
        }
        for(String ori : originalColumns) {
            if( ! columnList.contains(ori)) {
                return false;
            }
        }
        return true;
    }

}