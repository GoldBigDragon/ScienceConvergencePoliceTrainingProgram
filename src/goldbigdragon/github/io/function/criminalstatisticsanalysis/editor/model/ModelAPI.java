package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object.QueryObject;
import goldbigdragon.github.io.object.BatchObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.object.ModelObject;
import goldbigdragon.github.io.util.DbHandler;
import goldbigdragon.github.io.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelAPI extends DbHandler {

    public ModelAPI(String databaseName){
        connectDb(databaseName);
    }

    public List<String> getPoint(){
        List<String> returnList = new ArrayList<>();
        String sql = "SELECT DISTINCT name from '" + ModelDBQuery.TABLE_MODEL_POINT + "' ORDER BY name DESC;";
        try {
            PreparedStatement pstmt = super.conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            String point;
            while (rs.next()) {
                point = rs.getString("name");
                returnList.add(point);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : '" + sql + "' 에러");
        }
        return returnList;
    }

    public List<String> getCategory(){
        List<String> returnList = new ArrayList<>();
        String sql = "SELECT DISTINCT main from '" + ModelDBQuery.TABLE_MODEL_CATEGORY + "' ORDER BY main DESC;";
        try {
            PreparedStatement pstmt = super.conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            String category;
            while (rs.next()) {
                category = rs.getString("main");
                returnList.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : '" + sql + "' 에러");
        }
        return returnList;
    }

    public Map<String, List<String>> getModel(){
        Map<String, List<String>> returnMap = new HashMap<>();
        String sql = "SELECT main, sub from '" + ModelDBQuery.TABLE_MODEL_CATEGORY + "' ORDER BY main, sub DESC;";
        try {
            PreparedStatement pstmt = super.conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            String main;
            String sub;
            List<String> subList;
            while (rs.next()) {
                main = rs.getString("main");
                sub = rs.getString("sub");
                if(returnMap.containsKey(main)) {
                    subList = returnMap.get(main);
                    if(!subList.contains(sub))
                        subList.add(sub);
                } else {
                    subList = new ArrayList<>();
                    subList.add(sub);
                }
                returnMap.put(main, subList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : '" + sql + "' 에러");
        }
        return returnMap;
    }

    public Map<String, Double> getDefaultPoint(List<String> pointList){
        Map<String, Double> returnMap = new HashMap<>();
        for(String pointName : pointList) {
            String sql = "SELECT main, sub, `" + pointName + "` as defaultPoint from '" + ModelDBQuery.TABLE_MODEL_CATEGORY + "' ORDER BY main, sub DESC;";
            try {
                PreparedStatement pstmt = super.conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                String main;
                String sub;
                double point;
                while (rs.next()) {
                    main = rs.getString("main");
                    sub = rs.getString("sub");
                    point = rs.getDouble("defaultPoint");
                    returnMap.put(main+ Main.SPLIT_SYMBOL + sub + Main.SPLIT_SYMBOL + pointName, point);
                }
            } catch (SQLException e) {
                System.out.println("[SQLITE] : '" + sql + "' 에러");
            }
        }
        return returnMap;
    }

    public List<ModelObject> getAll(List<String> pointNameList){
        List<ModelObject> returnList = new ArrayList<>();
        String sql = "SELECT * from '" + ModelDBQuery.TABLE_MODEL_CATEGORY + "' ORDER BY main, sub DESC;";
        try {
            PreparedStatement pstmt = super.conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            String main;
            String sub;
            double point;
            while (rs.next()) {
                main = rs.getString("main");
                sub = rs.getString("sub");
                Map<String, Double> pointMap = new HashMap<>();
                for(String pointName : pointNameList) {
                    pointMap.put(pointName, rs.getDouble(pointName));
                }
                returnList.add(new ModelObject(pointMap, main, sub));
            }
        } catch (SQLException e) {
            System.out.println("[SQLITE] : '" + sql + "' 에러");
        }
        return returnList;
    }

    public Map<String, Double> getHasPointElement(QueryObject qo){
        Map<String, Double> returnMap = new HashMap<>();
        String sql = "SELECT main, sub, `" + qo.category + "` AS point from '" + ModelDBQuery.TABLE_MODEL_CATEGORY + "' WHERE `" + qo.category + "` != 0 ORDER BY main, sub DESC;";
        try {
            PreparedStatement pstmt = super.conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            String main;
            String sub;
            double value;
            while (rs.next()) {
                main = rs.getString("main");
                sub = rs.getString("sub");
                value = rs.getDouble("point");
                returnMap.put(main + Main.SPLIT_SYMBOL + sub, value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[SQLITE] : '" + sql + "' 에러");
        }
        return returnMap;
    }

    public void addModel(Map<String, List<String>> modelData){
        List<String> pointList = getPoint();
        List<String> notExist = new ArrayList<>();

        Map<String, List<String>> existModel = getModel();
        for(String key : modelData.keySet()) {
            if(existModel.containsKey(key)) {
                for(String element : modelData.get(key)) {
                    if(!existModel.get(key).contains(element)) {
                        notExist.add(key+Main.SPLIT_SYMBOL+element);
                    }
                }
            } else {
                for(String element : modelData.get(key)) {
                    notExist.add(key+Main.SPLIT_SYMBOL+element);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO '" + ModelCreate.TABLE_MODEL_CATEGORY + "'\n");
        sb.append("(");
        for(String point : pointList) {
            sb.append("'"+point + "', ");
        }
        sb.append("main, sub)\n");
        sb.append("VALUES (");
        for(int count = 0; count < pointList.size(); count++) {
            sb.append("?, ");
        }
        sb.append("?, ?);");
        try {
            BatchObject bo = new BatchObject(conn, sb.toString(), 3000, "MODEL_ELEMENT");
            int start;
            String[] splitted;
            for(String model : notExist) {
                start = 1;
                for(; start < pointList.size()+1; start++) {
                    bo.pstmt.setDouble(start, 0.0);
                }
                splitted = model.split(Main.SPLIT_SYMBOL);
                bo.pstmt.setString(start, splitted[0]);
                start++;
                bo.pstmt.setString(start, splitted[1]);

                if(bo.addBatch()){
                    conn.commit();
                }
            }
            bo.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
        }
    }

    public void modelMerge(String[] dbPathArray){
        ModelAPI mapi;
        Map<String, List<String>> modelList = new HashMap<>();
        Map<String, Double> defaultPointList = new HashMap<>();

        List<String> tempPointList = null;
        Map<String, List<String>> tempModelList;
        List<String> existElements;
        List<String> tempElements;
        Map<String, Double> tempDefaultPointList;
        String type;
        String[] header;
        String[] record;

        File f;
        FileUtil fu = new FileUtil();

        List<String> publicPointList = new ArrayList<>();
        List<String> individualPointList;
        for(String path : dbPathArray) {
            individualPointList = new ArrayList<>();
            type = path.substring(path.lastIndexOf("."));
            if(type.equals(".db")) {
                mapi = new ModelAPI(path);
                tempPointList = mapi.getPoint();
                for(String point : tempPointList) {
                    if(!individualPointList.contains(point)) {
                        individualPointList.add(point);
                    }
                }
                tempModelList = mapi.getModel();
                for(String category : tempModelList.keySet()) {
                    if(!modelList.containsKey(category)) {
                        modelList.put(category, tempModelList.get(category));
                    } else {
                        existElements = modelList.get(category);
                        tempElements = tempModelList.get(category);
                        for(String element : tempElements) {
                            if(!existElements.contains(element)) {
                                existElements.add(element);
                            }
                        }
                        modelList.put(category, existElements);
                    }
                }
                tempDefaultPointList = mapi.getDefaultPoint(tempPointList);
                for(String model : tempDefaultPointList.keySet()) {
                    if(!defaultPointList.containsKey(model)) {
                        defaultPointList.put(model, tempDefaultPointList.get(model));
                    }
                }
                mapi.close();
            } else if(type.equals(".csv")) {
                f = new File(path);
                try{
                    tempPointList = fu.readFile(f, "MS949");
                } catch(IOException e){
                }
                if(!tempPointList.isEmpty()) {
                    header = tempPointList.get(0).split(",");
                    for(int count = 2; count < header.length; count++) {
                        if(!individualPointList.contains(header[count])) {
                            individualPointList.add(header[count]);
                        }
                    }
                }
                String category;
                String element;
                for(int count = 1; count < tempPointList.size(); count++) {
                    record = tempPointList.get(count).split(",");
                    category = record[0];
                    element = record[1];
                    if(!modelList.containsKey(category)) {
                        existElements = new ArrayList<>();
                        existElements.add(element);
                    } else {
                        existElements = modelList.get(category);
                        if(!existElements.contains(element)) {
                            existElements.add(element);
                        }
                    }
                    modelList.put(category, existElements);
                    for(int pointCount = 0; pointCount < individualPointList.size(); pointCount++) {
                        if(record.length > pointCount+2) {
                            defaultPointList.put(category + Main.SPLIT_SYMBOL + element + Main.SPLIT_SYMBOL + individualPointList.get(pointCount), Double.parseDouble(record[2+pointCount]));
                        }
                    }
                }
            }
            for(String pointName : individualPointList) {
                if(!publicPointList.contains(pointName)) {
                    publicPointList.add(pointName);
                }
            }
        }
        createModelDb(publicPointList, modelList, defaultPointList);
    }

    public void createModelDb(List<String> pointList, Map<String, List<String>> modelList, Map<String, Double> defaultPointList){
        List<ModelObject> moList = new ArrayList<>();
        Map<String, Double> pointMap;
        String pointName;
        for(String category : modelList.keySet()) {
            for(String element : modelList.get(category)) {
                pointMap = new HashMap<>();
                for(String pointElement : defaultPointList.keySet()) {
                    if(pointElement.indexOf(category + Main.SPLIT_SYMBOL + element) != -1) {
                        pointName = pointElement.split(Main.SPLIT_SYMBOL)[2];
                        pointMap.put(pointName, defaultPointList.get(pointElement));
                    }
                }
                moList.add(new ModelObject(pointMap, category, element));
            }
        }
        ModelCreate mc = new ModelCreate(getDatabaseName(), pointList, moList);
        mc.close();
    }

}