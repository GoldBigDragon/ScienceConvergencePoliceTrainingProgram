package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model;

import goldbigdragon.github.io.object.BatchObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.object.ModelObject;
import goldbigdragon.github.io.util.DbHandler;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ModelDBQuery extends DbHandler {
    public static final String TABLE_MODEL_POINT = "model_point";
    public static final String TABLE_MODEL_CATEGORY = "model_category";

    public void createPointTable() {
        String sql = "CREATE TABLE IF NOT EXISTS '" + ModelDBQuery.TABLE_MODEL_POINT + "' (\n" +
                "num INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "name TEXT NOT NULL\n" +
                ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("[SQLITE] : " + ModelDBQuery.TABLE_MODEL_POINT + " 테이블 생성 실패");
        }
    }

    public void createCategoryTable(List<String> pointTypeList) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS '" + ModelDBQuery.TABLE_MODEL_CATEGORY + "' (\n");
        sb.append("num INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n");
        sb.append("main TEXT NOT NULL,\n");
        sb.append("sub TEXT NOT NULL,\n");
        for(String point : pointTypeList) {
            sb.append("'" + point + "' REAL NOT NULL DEFAULT 0,\n");
        }
        sb.append("description TEXT DEFAULT NULL\n");
        sb.append(");");
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sb.toString());
        } catch (SQLException e) {
            System.out.println("[SQLITE] : " + ModelDBQuery.TABLE_MODEL_CATEGORY + " 테이블 생성 실패");
        }
    }

    public void insertPoint(List<String> pointTypeList) {
        String sql = "INSERT INTO "+ ModelDBQuery.TABLE_MODEL_POINT +" (name) VALUES(?)";
        try {
            BatchObject bo = new BatchObject(conn, sql, 3000, "POINT MODEL");
            for(String point : pointTypeList) {
                bo.pstmt.setString(1, point);
                if(bo.addBatch()){
                    conn.commit();
                }
            }
            bo.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("[SQLITE] : POINT 기준 등록 실패");
        }
    }

    public void insertCategory(List<ModelObject> modelObject, List<String> pointTypeList) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO '" + ModelDBQuery.TABLE_MODEL_CATEGORY + "'\n");
        sb.append("(");
        for(String point : pointTypeList) {
            sb.append("'"+point + "', ");
        }
        sb.append("main, sub)\n");
        sb.append("VALUES (");
        for(int count = 0; count < pointTypeList.size(); count++) {
            sb.append("?, ");
        }
        sb.append("?, ?);");
        try {
            BatchObject bo = new BatchObject(conn, sb.toString(), 3000, "ELEMENT MODEL");
            int start;
            for(ModelObject mo : modelObject) {
                start = 1;
                for(String point : pointTypeList) {
                    if(mo.getPointMap().containsKey(point)) {
                        bo.pstmt.setDouble(start, mo.getPointMap().get(point));
                    } else {
                        bo.pstmt.setDouble(start, 0.0);
                    }
                    start++;
                }
                bo.pstmt.setString(start, mo.getMain());
                start++;
                bo.pstmt.setString(start, mo.getSub());
                if(bo.addBatch()){
                    conn.commit();
                }
            }
            bo.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("[SQLITE] : 카테고리 등록 실패");
        }
    }
}
