package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model;

import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.object.ModelObject;

import java.util.ArrayList;
import java.util.List;

public class ModelCreate extends ModelDBQuery {

    private List<String> pointTypeList;

    public ModelCreate(String databaseName, List<String> pointList, List<ModelObject> modelObject) {
        connectDb(databaseName);
        this.pointTypeList = getAvailablePointType(pointList, modelObject);
        createPointTable();
        insertPoint(pointTypeList);
        createCategoryTable(pointTypeList);
        insertCategory(modelObject, pointTypeList);
    }
    private List<String> getAvailablePointType(List<String> pointTypeList, List<ModelObject> categoryList){
        List<String> availablePointList = new ArrayList<>();
        for(int count = 0; count < categoryList.size(); count++) {
            for(String key : categoryList.get(count).getPointMap().keySet()) {
                if(pointTypeList.contains(key) && ! availablePointList.contains(key)) {
                    availablePointList.add(key);
                }
            }
        }
        return availablePointList;
    }
}
