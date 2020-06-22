package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.enums.AddEventType;
import goldbigdragon.github.io.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class DataFluctuationObject {

    private int minEventAmount;
    private int maxEventAmount;

    private String main;
    private String sub;
    private String minStartTime;
    private String maxStartTime;
    private String minEndTime;
    private String maxEndTime;
    private boolean shortTime = false;
    private int minAmount;
    private int maxAmount;
    private double minStartLatitude;
    private double maxStartLatitude;
    private double minStartLongitude;
    private double maxStartLongitude;
    private double minEndLatitude;
    private double maxEndLatitude;
    private double minEndLongitude;
    private double maxEndLongitude;
    private AddEventType addEventType;
    private String tempString;
    private List<String> additionalData;


    public DataFluctuationObject(int minEventAmount, int maxEventAmount, String main, String sub, String minStartTime, String maxStartTime, String minEndTime, String maxEndTime, boolean shortTime, int minAmount, int maxAmount, double minStartLatitude, double maxStartLatitude, double minStartLongitude, double maxStartLongitude, double minEndLatitude, double maxEndLatitude, double minEndLongitude, double maxEndLongitude, List<String> additionalData, AddEventType aet, String tempString) {
        dataInput(minEventAmount, maxEventAmount, main, sub, minStartTime, maxStartTime, minEndTime, maxEndTime, shortTime, minAmount, maxAmount, minStartLatitude, maxStartLatitude, minStartLongitude, maxStartLongitude, minEndLatitude, maxEndLatitude, minEndLongitude, maxEndLongitude, aet, tempString);
        this.additionalData = additionalData;
    }

    public DataFluctuationObject(int minEventAmount, int maxEventAmount, String main, String sub, String minStartTime, String maxStartTime, String minEndTime, String maxEndTime, boolean shortTime, int minAmount, int maxAmount, double minStartLatitude, double maxStartLatitude, double minStartLongitude, double maxStartLongitude, double minEndLatitude, double maxEndLatitude, double minEndLongitude, double maxEndLongitude, String[] additionalData, AddEventType aet, String tempString) {
        dataInput(minEventAmount, maxEventAmount, main, sub, minStartTime, maxStartTime, minEndTime, maxEndTime, shortTime, minAmount, maxAmount, minStartLatitude, maxStartLatitude, minStartLongitude, maxStartLongitude, minEndLatitude, maxEndLatitude, minEndLongitude, maxEndLongitude, aet, tempString);
        this.additionalData = new ArrayList<>();
        for(String data : additionalData)
            this.additionalData.add(data);
    }

    private void dataInput(int minEventAmount, int maxEventAmount, String main, String sub, String minStartTime, String maxStartTime, String minEndTime, String maxEndTime, boolean shortTime, int minAmount, int maxAmount, double minStartLatitude, double maxStartLatitude, double minStartLongitude, double maxStartLongitude, double minEndLatitude, double maxEndLatitude, double minEndLongitude, double maxEndLongitude, AddEventType aet, String tempString){
        this.minEventAmount = minEventAmount;
        this.maxEventAmount = maxEventAmount;
        this.main = main;
        this.sub = sub;
        this.minStartTime = minStartTime;
        this.maxStartTime = maxStartTime;
        this.minEndTime = minEndTime;
        this.maxEndTime = maxEndTime;
        this.shortTime = shortTime;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.minStartLatitude = minStartLatitude;
        this.maxStartLatitude = maxStartLatitude;
        this.minStartLongitude = minStartLongitude;
        this.maxStartLongitude = maxStartLongitude;
        this.minEndLatitude = minEndLatitude;
        this.maxEndLatitude = maxEndLatitude;
        this.minEndLongitude = minEndLongitude;
        this.maxEndLongitude = maxEndLongitude;
        this.addEventType = aet;
        this.tempString = tempString;
        this.additionalData = additionalData;
        sort(addEventType == AddEventType.AREA);
    }

    public DataFluctuationObject(String jsonString) {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        if(json.has("minEventAmount"))
            this.minEventAmount = json.get("minEventAmount").getAsInt();
        else
            this.minEventAmount = 500;
        if(json.has("maxEventAmount"))
            this.maxEventAmount = json.get("maxEventAmount").getAsInt();
        else
            this.maxEventAmount = 1000;
        if(json.has("main"))
            this.main = json.get("main").getAsString();
        else
            this.main = "기타";
        if(json.has("sub"))
            this.sub = json.get("sub").getAsString();
        else
            this.sub = "기타";

        if(json.has("minStartTime"))
            this.minStartTime = json.get("minStartTime").getAsString();
        else
            this.minStartTime = "2010-01-01 01:00:00";
        if(json.has("maxStartTime"))
            this.maxStartTime = json.get("maxStartTime").getAsString();
        else
            this.maxStartTime = "2010-01-01 01:00:00";

        if(json.has("minEndTime"))
            this.minEndTime = json.get("minEndTime").getAsString();
        else
            this.minEndTime = "2020-12-25 23:59:59";
        if(json.has("maxEndTime"))
            this.maxEndTime = json.get("maxEndTime").getAsString();
        else
            this.maxEndTime = "2020-12-25 23:59:59";

        if(json.has("shortTime"))
            this.shortTime = json.get("shortTime").getAsBoolean();
        else
            this.shortTime = false;

        if(json.has("minAmount"))
            this.minAmount = json.get("minAmount").getAsInt();
        else
            this.minAmount = 1;
        if(json.has("maxAmount"))
            this.maxAmount = json.get("maxAmount").getAsInt();
        else
            this.maxAmount = 100;

        if(json.has("minStartLatitude"))
            this.minStartLatitude = json.get("minStartLatitude").getAsDouble();
        else
            this.minStartLatitude = 124;
        if(json.has("maxStartLatitude"))
            this.maxStartLatitude = json.get("maxStartLatitude").getAsDouble();
        else
            this.maxStartLatitude = 124;
        if(json.has("minEndLatitude"))
            this.minEndLatitude = json.get("minEndLatitude").getAsDouble();
        else
            this.minEndLatitude = 132;
        if(json.has("maxEndLatitude"))
            this.maxEndLatitude = json.get("maxEndLatitude").getAsDouble();
        else
            this.maxEndLatitude = 132;

        if(json.has("minStartLongitude"))
            this.minStartLongitude = json.get("minStartLongitude").getAsDouble();
        else
            this.minStartLongitude = 33;
        if(json.has("maxStartLongitude"))
            this.maxStartLongitude = json.get("maxStartLongitude").getAsDouble();
        else
            this.maxStartLongitude = 33;
        if(json.has("minEndLongitude"))
            this.minEndLongitude = json.get("minEndLongitude").getAsDouble();
        else
            this.minEndLongitude = 42;
        if(json.has("maxEndLongitude"))
            this.maxEndLongitude = json.get("maxEndLongitude").getAsDouble();
        else
            this.maxEndLongitude = 42;
        if(json.has("addEventType"))
            this.addEventType = AddEventType.valueOf(json.get("addEventType").getAsString());
        else
            this.addEventType = AddEventType.AREA;
        if(json.has("tempString")) {
            if(json.get("tempString").isJsonNull()) {
                this.tempString = null;
            } else {
                this.tempString = json.get("tempString").getAsString();
            }
        }
        else
            this.tempString = null;
        this.additionalData = new ArrayList<>();
        if(json.has("additionalData") && !json.get("additionalData").isJsonNull()) {
            JsonObject additionalJson = json.get("additionalData").getAsJsonObject();
            String type;
            JsonObject range;
            for(String name : additionalJson.keySet()) {
                range = additionalJson.get(name).getAsJsonObject();
                type = range.get("type").getAsString();
                StringBuilder sb = new StringBuilder();
                sb.append(type);
                sb.append(Main.SPLIT_SYMBOL);
                sb.append(name);
                sb.append(Main.SPLIT_SYMBOL);
                if(type.equals("INTEGER")) {
                    sb.append(range.get("min").getAsInt());
                    sb.append(Main.SPLIT_SYMBOL);
                    sb.append(range.get("max").getAsInt());
                }
                else if(type.equals("LONG")) {
                    sb.append(range.get("min").getAsLong());
                    sb.append(Main.SPLIT_SYMBOL);
                    sb.append(range.get("max").getAsLong());
                }
                else if(type.equals("REAL")) {
                    sb.append(range.get("min").getAsDouble());
                    sb.append(Main.SPLIT_SYMBOL);
                    sb.append(range.get("max").getAsDouble());
                }
                else if(type.equals("TEXT")) {
                    int size = range.keySet().size();
                    int count = 0;
                    for(String index : range.keySet()) {
                        if(!index.equals("type")) {
                            sb.append(range.get(index).getAsString());
                            if(count < size)
                                sb.append(Main.SPLIT_SYMBOL);
                        }
                        count++;
                    }
                }
                else if(type.equals("BOOLEAN"))
                    additionalData.add(type + Main.SPLIT_SYMBOL + name + Main.SPLIT_SYMBOL + range.get("trueChance").getAsDouble());
                additionalData.add(sb.toString());
            }
        }
        sort(addEventType == AddEventType.AREA);
    }

    private void sort(boolean isArea){
        DateUtil du = new DateUtil();
        String tempString;
        if(du.stringToEpoch(minStartTime) > du.stringToEpoch(maxStartTime)) {
            tempString = maxStartTime;
            this.maxStartTime = minStartTime;
            this.minStartTime = tempString;
        }
        if(du.stringToEpoch(minEndTime) > du.stringToEpoch(maxEndTime)) {
            tempString = maxEndTime;
            this.maxEndTime = minEndTime;
            this.minEndTime = tempString;
        }
        if(du.stringToEpoch(minStartTime) > du.stringToEpoch(maxEndTime)) {
            tempString = maxEndTime;
            this.maxEndTime = minStartTime;
            this.minStartTime = tempString;
        }
        if(du.stringToEpoch(maxStartTime) > du.stringToEpoch(maxEndTime)) {
            tempString = maxEndTime;
            this.maxEndTime = maxStartTime;
            this.maxStartTime = tempString;
        }

        double tempDouble;
        if(isArea) {
            if(minStartLatitude > maxStartLatitude) {
                tempDouble = maxStartLatitude;
                this.maxStartLatitude = minStartLatitude;
                this.minStartLatitude = tempDouble;
            }
            if(minStartLongitude > maxStartLongitude) {
                tempDouble = maxStartLongitude;
                this.maxStartLongitude = minStartLongitude;
                this.minStartLongitude = tempDouble;
            }
            if(minEndLatitude > maxEndLatitude) {
                tempDouble = maxEndLatitude;
                this.maxEndLatitude = minEndLatitude;
                this.minEndLatitude = tempDouble;
            }
            if(minEndLongitude > maxEndLongitude) {
                tempDouble = maxEndLongitude;
                this.maxEndLongitude = minEndLongitude;
                this.minEndLongitude = tempDouble;
            }

            if(minStartLatitude > minEndLatitude) {
                tempDouble = minEndLatitude;
                this.minEndLatitude = minStartLatitude;
                this.minStartLatitude = tempDouble;
            }

            if(maxStartLatitude > maxEndLatitude) {
                tempDouble = maxEndLatitude;
                this.maxEndLatitude = maxStartLatitude;
                this.maxStartLatitude = tempDouble;
            }

            if(minStartLongitude > minEndLongitude) {
                tempDouble = minEndLongitude;
                this.minEndLongitude = minStartLongitude;
                this.minStartLongitude = tempDouble;
            }

            if(maxStartLongitude > maxEndLongitude) {
                tempDouble = maxEndLongitude;
                this.maxEndLongitude = maxStartLongitude;
                this.maxStartLongitude = tempDouble;
            }
        } else {
            if(minStartLatitude > minStartLongitude) {
                tempDouble = minStartLongitude;
                this.minStartLongitude = minStartLatitude;
                this.minStartLatitude = tempDouble;
            }
        }

    }

    @Override
    public String toString(){
        JsonObject json = new JsonObject();
        json.addProperty("minEventAmount", minEventAmount);
        json.addProperty("maxEventAmount", maxEventAmount);
        json.addProperty("main", main);
        json.addProperty("sub", sub);
        json.addProperty("minStartTime", minStartTime);
        json.addProperty("maxStartTime", maxStartTime);
        json.addProperty("minEndTime", minEndTime);
        json.addProperty("maxEndTime", maxEndTime);
        json.addProperty("shortTime", shortTime);
        json.addProperty("minAmount", minAmount);
        json.addProperty("maxAmount", maxAmount);
        json.addProperty("minStartLatitude", minStartLatitude);
        json.addProperty("maxStartLatitude", maxStartLatitude);
        json.addProperty("minStartLongitude", minStartLongitude);
        json.addProperty("maxStartLongitude", maxStartLongitude);
        json.addProperty("minEndLatitude", minEndLatitude);
        json.addProperty("maxEndLatitude", maxEndLatitude);
        json.addProperty("minEndLongitude", minEndLongitude);
        json.addProperty("maxEndLongitude", maxEndLongitude);

        json.addProperty("addEventType", addEventType.name());
        json.addProperty("tempString", tempString);

        if(!additionalData.isEmpty()) {
            JsonObject additionalValueJson = new JsonObject();
            String[] splitted;
            String type;
            String name;
            JsonObject rangeJson;
            for(int count = 0; count < additionalData.size(); count++) {
                splitted = additionalData.get(count).split(Main.SPLIT_SYMBOL);
                type = splitted[0];
                name = splitted[1];
                rangeJson = new JsonObject();
                rangeJson.addProperty("type", type);
                if(type.equals("INTEGER")) {
                    rangeJson.addProperty("min", Integer.parseInt(splitted[2]));
                    rangeJson.addProperty("max", Integer.parseInt(splitted[3]));
                }
                else if(type.equals("LONG")) {
                    rangeJson.addProperty("min", Long.parseLong(splitted[2]));
                    rangeJson.addProperty("max", Long.parseLong(splitted[3]));
                }
                else if(type.equals("REAL")) {
                    rangeJson.addProperty("min", Double.parseDouble(splitted[2]));
                    rangeJson.addProperty("max", Double.parseDouble(splitted[3]));
                }
                else if(type.equals("TEXT")) {
                    for(int count2 = 2; count2 < splitted.length; count2++) {
                        rangeJson.addProperty(Integer.toString(count2-2), splitted[count2]);
                    }
                }
                else if(type.equals("BOOLEAN")) {
                    rangeJson.addProperty("trueChance", Double.parseDouble(splitted[2]));
                }
                additionalValueJson.add(name, rangeJson);
            }
            json.add("additionalData", additionalValueJson);
        }
        return json.toString();
    }

    public int getMinEventAmount() {
        return minEventAmount;
    }

    public int getMaxEventAmount() {
        return maxEventAmount;
    }

    public String getMain() {
        return main;
    }

    public String getSub() {
        return sub;
    }

    public String getMinStartTime() {
        return minStartTime;
    }

    public String getMaxStartTime() {
        return maxStartTime;
    }

    public String getMinEndTime() {
        return minEndTime;
    }

    public String getMaxEndTime() {
        return maxEndTime;
    }

    public boolean getShortTime(){ return shortTime; }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public double getMinStartLatitude() { return minStartLatitude;
    }
    public double getMaxStartLatitude() { return maxStartLatitude;
    }
    public double getMinEndLatitude() { return minEndLatitude;
    }
    public double getMaxEndLatitude() { return maxEndLatitude;
    }
    public double getMinStartLongitude() { return minStartLongitude;
    }
    public double getMaxStartLongitude() { return maxStartLongitude;
    }
    public double getMinEndLongitude() { return minEndLongitude;
    }
    public double getMaxEndLongitude() { return maxEndLongitude;
    }
    public AddEventType getAddEventType() { return addEventType;
    }
    public String getTempString() { return tempString;
    }

    public String getAdditionalData(int index) {return additionalData.get(index);}

    public List<String> getAdditionalData() {return additionalData;}

    public void setAdditionalData(List<String> additionalData) {this.additionalData = additionalData;}

}