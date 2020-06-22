package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.object;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class ModelObject {

    private String main;
    private String sub;
    private Map<String, Double> pointMap;

    public ModelObject(Map<String, Double> pointMap, String main, String sub) {
        this.main = main;
        this.sub = sub;
        this.pointMap = pointMap;
    }

    public ModelObject(String jsonString) {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        this.main = json.get("main").getAsString();
        this.sub = json.get("sub").getAsString();
        pointMap = new HashMap<>();
        JsonObject pointJson = json.get("point").getAsJsonObject();
        for(String key : pointJson.keySet()) {
            pointMap.put(key, pointJson.get(key).getAsDouble());
        }
    }

    @Override
    public String toString(){
        JsonObject json = new JsonObject();
        json.addProperty("main", main);
        json.addProperty("sub", sub);
        JsonObject pointJson = new JsonObject();
        for(String key : pointMap.keySet()) {
            pointJson.addProperty(key, pointMap.get(key));
        }
        json.add("point", pointJson);
        return json.toString();
    }

    public String getMain() {
        return main;
    }

    public String getSub() {
        return sub;
    }

    public Map<String, Double> getPointMap() { return pointMap;}
}
