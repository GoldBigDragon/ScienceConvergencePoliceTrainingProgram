package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.util.DateUtil;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class DataObject {

    private String main;
    private String sub;
    private String startTime;
    private String endTime;
    private int amount;
    private double startLatitude;
    private double endLatitude;
    private double startLongitude;
    private double endLongitude;
    private List<String> additionalData;

    public DataObject(String main, String sub, String startTime, String endTime, int amount, double startLatitude, double startLongitude, double endLatitude, double endLongitude, List<String> additionalData) {
        this.main = main;
        this.sub = sub;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.additionalData = additionalData;
    }

    public DataObject(String main, String sub, String startTime, String endTime, int amount, double startLatitude, double startLongitude, double endLatitude, double endLongitude, String[] additionalData) {
        this.main = main;
        this.sub = sub;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.additionalData = new ArrayList<>();
        for(String data : additionalData)
            this.additionalData.add(data);
    }

    public DataObject(String jsonString) {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        this.main = json.get("main").getAsString();
        this.sub = json.get("sub").getAsString();
        this.startTime = json.get("startTime").getAsString();
        this.endTime = json.get("endTime").getAsString();
        this.amount = json.get("amount").getAsInt();
        this.startLatitude = json.get("startLatitude").getAsDouble();
        this.startLongitude = json.get("startLongitude").getAsDouble();
        this.endLatitude = json.get("endLatitude").getAsDouble();
        this.endLongitude = json.get("endLongitude").getAsDouble();
        this.additionalData = new ArrayList<>();
        if(!json.get("additionalData").isJsonNull()) {
            JsonObject pointJson = json.get("additionalData").getAsJsonObject();
            String type;
            for(String key : pointJson.keySet()) {
                type = key.split(Main.SPLIT_SYMBOL)[0];
                if(type.equals("INTEGER"))
                    additionalData.add(key + Main.SPLIT_SYMBOL + pointJson.get(key).getAsInt());
                else if(type.equals("LONG"))
                    additionalData.add(key + Main.SPLIT_SYMBOL + pointJson.get(key).getAsLong());
                else if(type.equals("TEXT"))
                    additionalData.add(key + Main.SPLIT_SYMBOL + pointJson.get(key).getAsString());
                else if(type.equals("REAL"))
                    additionalData.add(key + Main.SPLIT_SYMBOL + pointJson.get(key).getAsDouble());
                else if(type.equals("BOOLEAN"))
                    additionalData.add(key + Main.SPLIT_SYMBOL + pointJson.get(key).getAsBoolean());
            }
        }
    }

    @Override
    public String toString(){
        JsonObject json = new JsonObject();
        json.addProperty("main", main);
        json.addProperty("sub", sub);
        json.addProperty("startTime", startTime);
        json.addProperty("endTime", endTime);
        json.addProperty("amount", amount);
        json.addProperty("startLatitude", startLatitude);
        json.addProperty("endLatitude", endLatitude);
        json.addProperty("startLongitude", startLongitude);
        json.addProperty("endLongitude", endLongitude);
        if(!additionalData.isEmpty()) {
            JsonObject additionalValueJson = new JsonObject();
            String[] splitted;
            String type;
            String name;
            String value;
            for(int count = 0; count < additionalData.size(); count++) {
                splitted = additionalData.get(count).split(Main.SPLIT_SYMBOL);
                type = splitted[0];
                name = splitted[1];
                value = splitted[2];
                if(type.equals("INTEGER"))
                    additionalValueJson.addProperty(type + Main.SPLIT_SYMBOL + name, Integer.parseInt(value));
                else if(type.equals("LONG"))
                    additionalValueJson.addProperty(type + Main.SPLIT_SYMBOL + name, Long.parseLong(value));
                else if(type.equals("TEXT"))
                    additionalValueJson.addProperty(type + Main.SPLIT_SYMBOL + name, value);
                else if(type.equals("REAL"))
                    additionalValueJson.addProperty(type + Main.SPLIT_SYMBOL + name, Double.parseDouble(value));
                else if(type.equals("BOOLEAN"))
                    additionalValueJson.addProperty(type + Main.SPLIT_SYMBOL + name, Boolean.parseBoolean(value));
            }
            json.add("additionalData", additionalValueJson);
        }
        return json.toString();
    }

    public StringProperty getMain() {
        return new SimpleStringProperty(main);
    }

    public StringProperty getSub() {
        return new SimpleStringProperty(sub);
    }

    public StringProperty getStartTime() {
        return new SimpleStringProperty(startTime);
    }

    public StringProperty getEndTime() {
        return new SimpleStringProperty(endTime);
    }

    public LongProperty getEpochStartTime() {
        return new SimpleLongProperty(new DateUtil().stringToEpoch(startTime));
    }

    public LongProperty getEpochEndTime() {
        return new SimpleLongProperty(new DateUtil().stringToEpoch(endTime));
    }

    public IntegerProperty getAmount() {
        return new SimpleIntegerProperty(amount);
    }

    public DoubleProperty getStartLatitude() { return new SimpleDoubleProperty(startLatitude);
    }

    public DoubleProperty getStartLongitude() { return new SimpleDoubleProperty(startLongitude);
    }

    public DoubleProperty getEndLatitude() { return new SimpleDoubleProperty(endLatitude);
    }

    public DoubleProperty getEndLongitude() { return new SimpleDoubleProperty(endLongitude);
    }

    public StringProperty getLatitude() { return new SimpleStringProperty(startLatitude + " ~ " + endLatitude);
    }

    public StringProperty getLongitude() { return new SimpleStringProperty(startLongitude + " ~ " + endLongitude);
    }

    public DoubleProperty getMiddleLatitude() {
        double temp = endLatitude - startLatitude;
        return new SimpleDoubleProperty(endLatitude - temp);
    }

    public DoubleProperty getMiddleLongitude() {
        double temp = endLongitude - startLongitude;
        return new SimpleDoubleProperty(endLongitude - temp);
    }

    public StringProperty getAdditionalData(int index) {return new SimpleStringProperty(additionalData.get(index));}

    public List<String> getAdditionalData() {return additionalData;}

    public void setLatitude(double start, double end) {
        startLatitude = start;
        endLatitude = end;
    }
    public void setLongitude(double start, double end) {
        startLongitude = start;
        endLongitude = end;
    }
}