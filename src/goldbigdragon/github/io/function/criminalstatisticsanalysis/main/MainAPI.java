package goldbigdragon.github.io.function.criminalstatisticsanalysis.main;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.DataObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.object.ModelObject;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainAPI {
    public List<ModelObject> loadModel(Window window) {
        List<ModelObject> modelList = new ArrayList<>();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("모델 불러오기");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Model 파일", "*.SCPTP_model");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            try
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "MS949"));
                String line;
                try{
                    while((line=reader.readLine())!=null){
                            modelList.add(new ModelObject(line));
                    }
                } catch(Exception e){
                    new AlarmAPI().dataLoadError();
                }
                reader.close();
            }
            catch(IOException e1)
            {
                Thread.currentThread().interrupt();
            }
        }
        return modelList;
    }

    public void saveModel(Window window, String title, List<ModelObject> data) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("모델 저장");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Model 파일", "*.SCPTP_model");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("[" + title + "] [" + Main.mainVariables.utc + "].SCPTPmodel");
        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            try
            {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "MS949"));
                for(ModelObject mo : data) {
                    writer.write(mo.toString()+"\r\n");
                }
                writer.close();
            }
            catch(IOException e1)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

    public List<DataObject> loadData(Window window) {
        List<DataObject> dataList = new ArrayList<>();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("데이터 불러오기");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Data 파일", "*.SCPTP_data");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            try
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "MS949"));
                String line;
                try{
                    while((line=reader.readLine())!=null){
                        dataList.add(new DataObject(line));
                    }
                } catch(Exception e){
                    new AlarmAPI().dataLoadError();
                }
                reader.close();
            }
            catch(IOException e1)
            {
                Thread.currentThread().interrupt();
            }
        }
        return dataList;
    }

    public void saveData(Window window, String title, List<DataObject> data) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("데이터 저장");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Data 파일", "*.SCPTP_data");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("[" + title + "] [" + Main.mainVariables.utc + "].SCPTPdata");
        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            try
            {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "MS949"));
                for(DataObject mo : data) {
                    writer.write(mo.toString()+"\r\n");
                }
                writer.close();
            }
            catch(IOException e1)
            {
                Thread.currentThread().interrupt();
            }
        }
    }
}
