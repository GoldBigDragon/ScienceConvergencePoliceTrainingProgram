package goldbigdragon.github.io.enums;
public enum DataType {
    POINT("P"),
    CATEGORY("C"),
    ELEMENT("E");

    final public String code;

    DataType(String code) {
        this.code = code;
    }
}
