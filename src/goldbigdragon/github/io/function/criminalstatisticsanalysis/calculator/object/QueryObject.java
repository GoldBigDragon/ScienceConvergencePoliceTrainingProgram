package goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.DataType;
import goldbigdragon.github.io.enums.TimeEnum;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.object.VisualizationObject;

public class QueryObject {

    public DataType dataType;
    public String category;
    public String element;
    public String subQuery;
    public TimeEnum timeEnum;
    public int time;

    public SectorObject sectorObject = null;

    public QueryObject(DataType dataType, String category, String element, String subQuery, TimeEnum timeEnum, int time) {
        this.dataType = dataType;
        this.category = category;
        this.element = element;
        this.subQuery = subQuery;
        this.timeEnum = timeEnum;
        this.time = time;
    }

    public QueryObject(QueryObject original) {
        this.dataType = original.dataType;
        this.category = original.category;
        this.element = original.element;
        this.subQuery = original.subQuery;
        this.timeEnum = original.timeEnum;
        this.time = original.time;
        if(original.sectorObject != null) {
            this.sectorObject = new SectorObject(original.sectorObject);
        }
    }

    public QueryObject(VisualizationObject original, TimeEnum timeEnum, int time) {
        this.dataType = original.type;
        if(dataType == DataType.ELEMENT) {
            this.category = original.name.split(Main.SPLIT_SYMBOL)[0];
            this.element = original.name.split(Main.SPLIT_SYMBOL)[1];
        } else {
            this.category = original.name;
            this.element = null;
        }
        if(original.subQueryList == null || original.subQueryList.isEmpty()) {
            this.subQuery = null;
        } else {
            this.subQuery = original.subQueryToString();
        }
        this.timeEnum = timeEnum;
        this.time = time;
        this.sectorObject = null;
    }

    public String getKeyName(){
        if(sectorObject != null) {
            if(dataType == DataType.ELEMENT) {
                return sectorObject.sector + Main.SPLIT_SYMBOL + dataType.code + Main.SPLIT_SYMBOL + category + Main.SPLIT_SYMBOL + element;
            } else {
                return sectorObject.sector + Main.SPLIT_SYMBOL + dataType.code + Main.SPLIT_SYMBOL + category;
            }
        } else {
            if(dataType == DataType.ELEMENT) {
                return dataType.code + Main.SPLIT_SYMBOL + category + Main.SPLIT_SYMBOL + element;
            } else {
                return dataType.code + Main.SPLIT_SYMBOL + category;
            }
        }
    }
}