package goldbigdragon.github.io.function.alarm.controller;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.Absolute;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.DataAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.DataFluctuationCreate;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.DataFluctuationObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.view.DataEditorView;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.ModelAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.ModelCreate;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.controller.ModelEditorController;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.object.ModelObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.view.MainView;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.query.DefaultCreate;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.view.VisualizationMainView;
import goldbigdragon.github.io.util.FileUtil;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriminalStatisticsAnalysisProgress {

    public void createDefaultModel() {
        DefaultCreate dc = new DefaultCreate();
        dc.createModelDb();
        dc.createDataDb();
    }

    public void overwriteModel() {
        List<ModelObject> modelObjects = new ArrayList<>();
        ModelObject mo;
        Map<String, Double> pointList;
        String pointKey;
        for(String category : ModelEditorController.model.keySet()) {
            for(String element : ModelEditorController.model.get(category)) {
                pointList = new HashMap<>();
                for(String point : ModelEditorController.point) {
                    pointKey = category + Main.SPLIT_SYMBOL + element + Main.SPLIT_SYMBOL + point;
                    if(ModelEditorController.defaultPoint.containsKey(pointKey)) {
                        pointList.put(point, ModelEditorController.defaultPoint.get(pointKey));
                    } else {
                        pointList.put(point, 0d);
                    }
                }
                mo = new ModelObject(pointList, category, element);
                modelObjects.add(mo);
            }
        }

        if(MainController.modelDb.getDatabaseName().equals(Main.dbDirectory + "/data/model/default.db")) {
            File d = new File(Main.dbDirectory + "/data/model");
            d.mkdirs();
            String database = Main.dbDirectory + "/data/model/instance_"+Main.mainVariables.utc;
            new ModelCreate(database, ModelEditorController.point, modelObjects).close();
            MainController.modelDb = new ModelAPI(database+".db");
        } else {
            String originalName = MainController.modelDb.getDatabaseName();
            File original = new File(originalName);
            MainController.modelDb.close();
            original.delete();
            new ModelCreate(originalName, ModelEditorController.point, modelObjects).close();
            MainController.modelDb = new ModelAPI(originalName);
        }
        ModelEditorController.clearAll();
    }

    public void loadData(String param){
        String modelString = param.split("\\*")[0];
        String dataString = param.split("\\*")[1];
        if(!modelString.equals("null")) {
            String modelDbPath = null;
            String[] modelPathArray = modelString.split(Main.SPLIT_SYMBOL);
            if(modelPathArray.length == 1) {
                String type = modelPathArray[0].substring(modelPathArray[0].lastIndexOf("."));
                if(type.equals(".db")) {
                    modelDbPath = modelPathArray[0];
                }
            }
            if(modelDbPath == null) {
                modelDbPath = Main.dbDirectory + "/data/model/instance_" + Main.mainVariables.utc;
                ModelAPI modelApi = new ModelAPI(modelDbPath);
                modelApi.modelMerge(modelPathArray);
                modelApi.close();
            }
            MainController.modelDb.close();
            MainController.modelDb = new ModelAPI(modelDbPath);
        }

        if(!dataString.equals("null")) {
            String dataDbPath = null;
            String[] dataPathArray = dataString.split(Main.SPLIT_SYMBOL);
            if(dataPathArray.length == 1) {
                String type = dataPathArray[0].substring(dataPathArray[0].lastIndexOf("."));
                if(type.equals(".db")) {
                    dataDbPath = dataPathArray[0];
                }
            }
            if(dataDbPath == null) {
                dataDbPath = Main.dbDirectory + "/data/data/instance_" + Main.mainVariables.utc;
                DataAPI dataApi = new DataAPI(dataDbPath);
                dataApi.dataMerge(dataPathArray);
                dataApi.close();
            }
            MainController.dataDb.close();
            MainController.dataDb = new DataAPI(dataDbPath);
        }
    }

    public void createFluctuatedData(String param) {
        FileUtil fu = new FileUtil();
        DataFluctuationObject dfo;
        File f = new File(param);
        DataFluctuationCreate dfc = new DataFluctuationCreate();
        try {
            List<String> data = fu.readFile(f, "UTF8");
            for(String string : data) {
                dfo = new DataFluctuationObject(string);
                dfc.createFluctuationData(dfo);
            }
        } catch(IOException e) {
        }
    }


    public void openCriminalStatisticsAnalysisMain(){
        Platform.runLater(()->{
            new MainView().view();
        });
    }

    public void openCriminalStatisticsAnalysisDataEditor(){
        Platform.runLater(()->{
            new DataEditorView().view();
        });
    }

    public void calculateCriminalStatisticsAnalysisVisualization(String param){
        String[] splitted = param.split(Main.SPLIT_SYMBOL);
        System.out.println(param);
        int result = new Absolute().calculate(splitted[0], Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), Double.parseDouble(splitted[3]), Double.parseDouble(splitted[4]), Double.parseDouble(splitted[5]), Double.parseDouble(splitted[6]));
        if(result == -1) {
            Platform.runLater(()->{
                new AlarmAPI().emptyVisualizationTarget();
                new MainView().view();
            });
        } else if(result == -2){
            Platform.runLater(()->{
                new AlarmAPI().emptyDatas();
                new MainView().view();
            });
        } else {
            Platform.runLater(()->{
                new VisualizationMainView().view();
                goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController.visualizationTarget = new ArrayList<>();
            });
        }
    }
}
