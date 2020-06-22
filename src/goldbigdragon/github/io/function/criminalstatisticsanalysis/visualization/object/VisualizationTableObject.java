package goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.object;

import goldbigdragon.github.io.enums.TimeEnum;

import java.util.HashMap;
import java.util.Map;

public class VisualizationTableObject {

    public String num;
    public String sector;
    public String category;
    public String element;
    public String subQuery;
    public TimeEnum timeType;
    public String timeValue;
    public String amount;

    public Map<String, Double> geoDataDifference = null;
    public Map<String, Double> dataDifference = null;
    public Map<String, Double> distanceBonus = null;

    public VisualizationTableObject(int num, int sector, String category, String element, String subQuery, TimeEnum timeType, String timeValue, double amount, boolean isAbsoluteCount) {
        this.num = Integer.toString(num);
        this.sector = Integer.toString(sector);
        this.category = category;
        this.element = element;
        this.subQuery = subQuery;
        this.timeType = timeType;
        this.timeValue = timeValue;
        if(isAbsoluteCount) {
            this.amount = Integer.toString((int)amount);
        } else {
            this.amount = String.format("%.3f", amount);
            geoDataDifference = new HashMap<>();
            dataDifference = new HashMap<>();
            distanceBonus = new HashMap<>();
        }
    }

    public VisualizationTableObject(int num, VisualizationTableObject cloneTarget) {
        this.num = Integer.toString(num);
        this.sector = cloneTarget.sector;
        this.category = cloneTarget.category;
        this.element = cloneTarget.element;
        this.subQuery = cloneTarget.subQuery;
        this.timeType = cloneTarget.timeType;
        this.timeValue = cloneTarget.timeValue;
        this.amount = cloneTarget.amount;
        geoDataDifference = cloneTarget.geoDataDifference;
        dataDifference = cloneTarget.dataDifference;
        distanceBonus = cloneTarget.distanceBonus;
    }

    public String getCategoryElement(){
        boolean cateNull = (category == null || category.length() < 1);
        boolean elementNull = (element == null || element.length() < 1);
        boolean subQueryNull = (subQuery == null || subQuery.length() < 1);

        if( ! cateNull && ! elementNull && ! subQueryNull) {
            return category + "::" + element + "::" + subQuery;
        } else if( ! cateNull && ! elementNull && subQueryNull) {
            return category + "::" + element;
        } else if( ! cateNull && elementNull && ! subQueryNull) {
            return category + "::" + subQuery;
        } else if( ! cateNull && elementNull && subQueryNull) {
            return category;
        } else if( cateNull && ! elementNull && subQueryNull) {
            return element;
        } else if( cateNull && ! elementNull && ! subQueryNull ) {
            return element + "::" + subQuery;
        }
        return null;
    }

    public String getElement(){
        if(subQuery != null && subQuery.length() > 0) {
            return element + "(" + subQuery + ")";
        } else {
            return element;
        }
    }

    public String getTime(){
        if(timeType == TimeEnum.YEAR) {
            if(timeValue == null || timeValue.length() < 1){
                return "전체기간";
            } else {
                return timeValue+"년";
            }
        } else if(timeType == TimeEnum.MONTH){
            return timeValue+"월";
        } else if(timeType == TimeEnum.WEEK){
            return timeValue+"요일";
        } else if(timeType == TimeEnum.HOUR){
            return timeValue+"시";
        }
        return null;
    }

    public Double getTotalBonus(){
        double returnDouble = Double.parseDouble(amount);
        for(double value : geoDataDifference.values()) {
            returnDouble += value;
        }
        for(double value : dataDifference.values()) {
            returnDouble += value;
        }
        for(double value : distanceBonus.values()) {
            returnDouble += value;
        }
        return returnDouble;
    }
}