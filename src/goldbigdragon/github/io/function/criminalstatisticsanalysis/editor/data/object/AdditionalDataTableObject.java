package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object;

import goldbigdragon.github.io.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AdditionalDataTableObject {
    private String name;
    private String type;
    private String value;

    public AdditionalDataTableObject(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public AdditionalDataTableObject(String jsonString) {
        String[] splitted = jsonString.split(Main.SPLIT_SYMBOL);
        this.type = splitted[0];
        this.name = splitted[1];
        if(splitted.length > 2) {
            this.value = splitted[2];
        } else {
            if(type.equals("INTEGER")||type.equals("LONG")||type.equals("REAL")) {
                this.value = "0";
            } else if(type.equals("BOOLEAN")) {
                this.value = "false";
            } else {
                this.value = "NULL";
            }
        }
    }

    public StringProperty getName() {
        return new SimpleStringProperty(name);
    }

    public StringProperty getType() {
        if(type.equals("INTEGER") || type.equals("LONG") || type.equals("정수"))
            return new SimpleStringProperty("정수");
        else if(type.equals("REAL") || type.equals("실수"))
            return new SimpleStringProperty("실수");
        else if(type.equals("BOOLEAN") || type.equals("이진수"))
            return new SimpleStringProperty("이진수");
        else if(type.equals("TIMESTAMP") || type.equals("시간"))
            return new SimpleStringProperty("시간");
        else
            return new SimpleStringProperty("문자열");
    }

    public StringProperty getValue() {
        return new SimpleStringProperty(value);
    }

    public void setValue(String value) { this.value = value; }

    @Override
    public String toString(){
        return type + Main.SPLIT_SYMBOL + name + Main.SPLIT_SYMBOL + value;
    }
}
