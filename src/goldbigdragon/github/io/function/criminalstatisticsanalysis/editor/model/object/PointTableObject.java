package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.object;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PointTableObject {
    private String name;
    private String value;

    public PointTableObject(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public StringProperty getName() {
        return new SimpleStringProperty(name);
    }

    public StringProperty getValue() {
        return new SimpleStringProperty(value);
    }

    public void setValue(String value) {
        this.value = value;
    }
}
