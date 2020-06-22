package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AdditionalDataColumnObject {
    public int cid;
    public String name;
    public String type;
    public int notnull;
    public String dflt_value;
    public int pk;

    public AdditionalDataColumnObject(int cid, String name, String type, int notnull, String dflt_value, int pk) {
        this.cid = cid;
        this.name = name;
        this.type = type;
        this.notnull = notnull;
        this.dflt_value = dflt_value;
        this.pk = pk;
    }

    public StringProperty getName() {
        return new SimpleStringProperty(name);
    }

    public StringProperty getType() {
        return new SimpleStringProperty(type);
    }

    public StringProperty getDefaultValue() {
        return new SimpleStringProperty(dflt_value);
    }
}
