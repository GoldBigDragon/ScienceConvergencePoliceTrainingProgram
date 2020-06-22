package goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.object;

import goldbigdragon.github.io.enums.DataType;
import goldbigdragon.github.io.enums.LogicType;
import goldbigdragon.github.io.enums.OperatorType;
import goldbigdragon.github.io.enums.ValueType;
import goldbigdragon.github.io.object.SubQueryObject;

import java.util.ArrayList;
import java.util.List;

public class VisualizationObject {
    public DataType type;
    public String name;
    public List<SubQueryObject> subQueryList;

    public VisualizationObject(DataType type, String name) {
        this.type = type;
        this.name = name;
        this.subQueryList = new ArrayList<>();
    }

    public void addSubQuery(ValueType subQueryType, String subQueryName, OperatorType operator, String value, LogicType logic){
        subQueryList.add(new SubQueryObject(subQueryType, subQueryName, operator, value, logic));
    }

    public void removeSubQuery(String subQueryName, OperatorType operator, String value, LogicType logic){
        for(int count = 0; count < subQueryList.size(); count++){
            if(subQueryList.get(count).name.equals(subQueryName) && subQueryList.get(count).operator == operator && subQueryList.get(count).value.equals(value) && subQueryList.get(count).logic == logic) {
                subQueryList.remove(count);
                break;
            }
        }
    }

    public String subQueryToString(){
        if(subQueryList.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        SubQueryObject so;
        for(int count = 0; count < subQueryList.size(); count++) {
            so = subQueryList.get(count);
            sb.append("`");
            sb.append(so.name);
            sb.append("` ");
            sb.append(so.operator.symbol);
            sb.append(" ");
            if(so.type == ValueType.TEXT) {
                sb.append("'");
                sb.append(so.value);
                sb.append("'");
            } else {
                sb.append(so.value);
            }
            sb.append(" ");
            if(count < subQueryList.size()-1) {
                sb.append(subQueryList.get(count).logic);
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}