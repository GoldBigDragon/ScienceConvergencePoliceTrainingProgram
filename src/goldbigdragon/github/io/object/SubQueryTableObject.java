package goldbigdragon.github.io.object;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SubQueryTableObject {
    private String target;
    private String symbol;
    private String compareValue;
    private String logicalOperator;

    public SubQueryTableObject(String target, String symbol, String compareValue, String logicalOperator) {
        this.target = target;
        this.symbol = symbol;
        this.compareValue = compareValue;
        this.logicalOperator = logicalOperator;
    }

    public StringProperty getTarget() {
        return new SimpleStringProperty(target);
    }
    public StringProperty getSymbol() {
        return new SimpleStringProperty(symbol);
    }
    public StringProperty getCompareValue() {
        return new SimpleStringProperty(compareValue);
    }
    public StringProperty getLogicalOperator() {
        return new SimpleStringProperty(logicalOperator);
    }
}
