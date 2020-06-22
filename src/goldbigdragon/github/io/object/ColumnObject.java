package goldbigdragon.github.io.object;

public class ColumnObject {
    public String name;
    public String type;
    public String defaultValue;
    public boolean notNull;
    public boolean primaryKey;

    public ColumnObject (String name, String type, String defaultValue, boolean notNull, boolean primaryKey) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.notNull = notNull;
        this.primaryKey = primaryKey;
    }
}