package goldbigdragon.github.io.enums;
public enum OperatorType {
    EQUALS("=="),
    EQUALS_OR_BIGGER(">="),
    EQUALS_OR_LOWER("<="),
    BIGGER(">"),
    LOWER("<"),
    DIFFERENT("!=");

    final public String symbol;

    OperatorType(String symbol) {
        this.symbol = symbol;
    }
}
