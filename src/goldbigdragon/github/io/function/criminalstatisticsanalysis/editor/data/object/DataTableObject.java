package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DataTableObject {
    private String num;
    private String category;
    private String element;
    private String startTime;
    private String endTime;
    private String amount;
    private String startLatitude;
    private String startLongitude;
    private String endLatitude;
    private String endLongitude;

    public DataTableObject(String num, String category, String element, String startTime, String endTime, String amount, String startLatitude, String startLongitude, String endLatitude, String endLongitude) {
        this.num = num;
        this.category = category;
        this.element = element;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
    }

    public StringProperty getNum() {
        return new SimpleStringProperty(num);
    }
    public StringProperty getCategory() {
        return new SimpleStringProperty(category);
    }
    public StringProperty getElement() {
        return new SimpleStringProperty(element);
    }
    public StringProperty getTime() {
        if(startTime.equals(endTime)) {
            return new SimpleStringProperty(startTime);
        }
        else {
            return new SimpleStringProperty(startTime + " ~ " + endTime);
        }
    }

    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return startTime;
    }

    public StringProperty getAmount() {
        return new SimpleStringProperty(amount);
    }
    public StringProperty getLatitude() {
        if(startLatitude == endLatitude) {
            return new SimpleStringProperty(startLatitude);
        } else {
            return new SimpleStringProperty(startLatitude + " ~ " + endLatitude);
        }
    }
    public StringProperty getLongitude() {
        if(startLongitude == endLongitude) {
            return new SimpleStringProperty(startLongitude);
        } else {
            return new SimpleStringProperty(startLongitude + " ~ " + endLongitude);
        }
    }

    public String getStartLatitude() {
        return startLatitude;
    }
    public String getEndLatitude() {
        return endLatitude;
    }
    public String getStartLongitude() {
        return startLongitude;
    }
    public String getEndLongitude() {
        return endLongitude;
    }
}
