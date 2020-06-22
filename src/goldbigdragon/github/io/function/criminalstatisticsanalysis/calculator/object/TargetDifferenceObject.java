package goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object;

import goldbigdragon.github.io.enums.TimeEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TargetDifferenceObject {

    public int sector = -1;
    public String dataType;
    public String category;
    public String element;
    public Map<TimeEnum, List<Integer>> timeMap = new HashMap<>();

    public TargetDifferenceObject(String dataType, String category, String element) {
        this.dataType = dataType;
        this.category = category;
        this.element = element;
    }
}