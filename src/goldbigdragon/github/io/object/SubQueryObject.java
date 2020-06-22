package goldbigdragon.github.io.object;

import goldbigdragon.github.io.enums.LogicType;
import goldbigdragon.github.io.enums.OperatorType;
import goldbigdragon.github.io.enums.ValueType;

public class SubQueryObject {
    public ValueType type;
    public String name;
    public OperatorType operator;
    public String value;
    public LogicType logic;

    public SubQueryObject(ValueType type, String name, OperatorType operator, String value, LogicType logic) {
        this.type = type;
        this.name = name;
        this.operator = operator;
        this.value = value;
        this.logic = logic;
    }
}