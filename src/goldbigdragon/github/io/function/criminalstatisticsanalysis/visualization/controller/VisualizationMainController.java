package goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.controller;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.*;
import goldbigdragon.github.io.function.BaseController;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.Absolute;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.AbsoluteDataAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.enums.VisualizationType;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.view.MainView;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.object.VisualizationTableObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.view.VisualizationMainView;
import goldbigdragon.github.io.node.NumberField;
import goldbigdragon.github.io.node.RealField;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class VisualizationMainController extends BaseController {

    @FXML Label loadedDataLabel;
    @FXML Label loadedEnviromentDataLabel;
    @FXML Label loadedVisualizationDataLabel;
    @FXML Label rowsLabel;
    @FXML Label columnsLabel;
    @FXML Label startLatiLabel;
    @FXML Label startLongiLabel;
    @FXML Label endLatiLabel;
    @FXML Label endLongiLabel;

    @FXML Accordion accordion;
    @FXML Button calcButton;

    @FXML WebView map;
    @FXML TableView<VisualizationTableObject> table;
    @FXML PieChart pieChart;
    @FXML LineChart lineChart;

    @FXML GridPane pieChartGridPane;
    @FXML GridPane lineChartGridPane;
    @FXML VBox briefingPane;
    @FXML VBox briefingVisualizationData;

    @FXML TitledPane sectorSelectPane;
    @FXML TitledPane orderByPane;

    @FXML RadioButton allSectorRadio;
    @FXML RadioButton ascendingRadio;
    @FXML RadioButton timeYearRadio;
    @FXML RadioButton timeMonthRadio;
    @FXML RadioButton timeWeekRadio;
    @FXML RadioButton timeHourRadio;
    @FXML RadioButton timeAllRadio;
    @FXML RadioButton absoluteCalcRadio;

    @FXML MenuButton timeMinYear;
    @FXML MenuButton timeMaxYear;
    @FXML Label timeYearSlash;
    @FXML MenuButton timeMinMonth;
    @FXML MenuButton timeMaxMonth;
    @FXML Label timeMonthSlash;
    @FXML MenuButton timeMinWeek;
    @FXML MenuButton timeMaxWeek;
    @FXML Label timeWeekSlash;
    @FXML MenuButton timeMinHour;
    @FXML MenuButton timeMaxHour;
    @FXML Label timeHourSlash;

    @FXML VBox visualizationTargetVbox;
    @FXML VBox environmentVariableVbox;
    @FXML VBox appearBonusVbox;
    @FXML HBox limitHbox;
    @FXML NumberField sectorNumberField;
    @FXML NumberField limitField;
    @FXML RealField timeAvgBonus;
    @FXML RealField distanceAvgBonus;
    @FXML RealField sectorTimeAvgBonus;
    @FXML RealField adjacentSectorBonus;
    @FXML CheckBox equalizerCheckBox;
    @FXML Label mapInfoLabel;
    @FXML Label tableInfoLabel;
    @FXML Label pieInfoLabel;
    @FXML Label pieChartLabel;
    @FXML Label lineInfoLabel;
    @FXML Label lineChartLabel;

    List<String> selectedVisualizationTarget = new ArrayList<>();
    List<String> selectedEnviromentTarget = new ArrayList<>();

    private VisualizationType vt;
    private boolean calculating = false;

    @Override
    @FXML public void close(ActionEvent event){
        Absolute.absoluteDB.close();
        Absolute.absoluteDB = null;
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.DISABLE);
        stage.close();
        new MainView().view();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vt = VisualizationType.MAP;
        Platform.runLater(() -> {
            Map<String, Double> settingMap = Absolute.absoluteDB.getSettings();
            int rows = settingMap.get("rows").intValue();
            int columns = settingMap.get("columns").intValue();
            sectorNumberField.setMinMax(1, rows*columns, 1, rows*columns);
            limitField.setMinMax(1, 100, 1, 100);

            pieChart.setLabelLineLength(20);
            pieChart.setLegendVisible(false);
            pieChart.setLabelsVisible(true);

            lineChart.setLegendVisible(true);

            List<String> yearList = Absolute.absoluteDB.getYears();
            MenuItem min;
            MenuItem max;
            for(String year : yearList) {
                min = new MenuItem(year);
                min.setOnAction(event -> timeMinYearSelect(event));
                timeMinYear.getItems().add(min);
                max = new MenuItem(year);
                max.setOnAction(event -> timeMaxYearSelect(event));
                timeMaxYear.getItems().add(max);
            }
            timeMinYear.setText(yearList.get(0));
            timeMaxYear.setText(yearList.get(yearList.size()-1));
            List<String> visualizationTargetList = Absolute.absoluteDB.getVisualizationTarget();
            CheckBox cb;
            for(String visualizationTarget : visualizationTargetList) {
                cb = new CheckBox(visualizationTarget.replace(Main.SPLIT_SYMBOL+"NULL", "").replace(Main.SPLIT_SYMBOL, "::"));
                cb.setSelected(true);
                selectedVisualizationTarget.add(visualizationTarget);
                cb.setOnAction(event -> {
                    if(!((CheckBox)event.getSource()).isSelected()) {
                        selectedVisualizationTarget.remove(visualizationTarget);
                    } else {
                        selectedVisualizationTarget.add(visualizationTarget);
                    }
                });
                visualizationTargetVbox.getChildren().add(cb);
            }
            List<String> enviromentList = Absolute.absoluteDB.getEnviromentVariable();
            for(String environmentVariable : enviromentList) {
                cb = new CheckBox(environmentVariable.replace(Main.SPLIT_SYMBOL, "::"));
                cb.setSelected(true);
                selectedEnviromentTarget.add(environmentVariable);
                cb.setOnAction(event -> {
                    if(!((CheckBox)event.getSource()).isSelected()) {
                        selectedEnviromentTarget.remove(environmentVariable);
                    } else {
                        selectedEnviromentTarget.add(environmentVariable);
                    }
                });
                environmentVariableVbox.getChildren().add(cb);
            }
            StringBuilder sb = new StringBuilder("http://localhost:1120/visualizationMap?");
            String url = sb.toString().substring(0, sb.length()-1);
            map.getEngine().setJavaScriptEnabled(true);
            map.getEngine().load(url);

            table.setEditable(false);
            table.setPlaceholder(new Label("조건에 부합하는 데이터가 없습니다!"));

            Map<String, Integer> dataMap = Absolute.absoluteDB.getDataInfo();
            loadedDataLabel.setText(dataMap.get("totalAmount").toString());
            loadedEnviromentDataLabel.setText(dataMap.get("enviromentCount").toString());
            loadedVisualizationDataLabel.setText(dataMap.get("visualizationCount").toString());
            rowsLabel.setText(Integer.toString(rows));
            columnsLabel.setText(Integer.toString(columns));
            startLatiLabel.setText(settingMap.get("startLatitude").toString());
            startLongiLabel.setText(settingMap.get("startLongitude").toString());
            endLatiLabel.setText(settingMap.get("endLatitude").toString());
            endLongiLabel.setText(settingMap.get("endLongitude").toString());

            VBox vvbox;
            Label space;
            Label titleLabel;
            VBox infoVBox;
            Label shortestDistanceLabel;
            Label longestDistanceLabel;
            Label mostTimeDifferenceLabel;
            Label leastTimeDifferenceLabel;
            Map<String, String> visualizationInfo;
            String[] splitted;
            for(String visualizationTarget : visualizationTargetList) {
                splitted = visualizationTarget.split(Main.SPLIT_SYMBOL);
                visualizationInfo = Absolute.absoluteDB.getVisualizationInfo(splitted[0], splitted[1]);
                vvbox = new VBox();
                space = new Label(" ");
                infoVBox = new VBox();
                infoVBox.setPadding(new Insets(0, 0, 0, 20));
                titleLabel = new Label(visualizationTarget.replace(Main.SPLIT_SYMBOL+"NULL", "").replace(Main.SPLIT_SYMBOL, "::"));
                shortestDistanceLabel = new Label("평균 거리가 가장 가까운 환경변수 : " + visualizationInfo.get("shortestDistance"));
                longestDistanceLabel = new Label("평균 거리가 가장 먼 환경변수 : " + visualizationInfo.get("longestDistance"));
                mostTimeDifferenceLabel = new Label("평균 발생 시간이 가장 차이나는 환경변수 : " + visualizationInfo.get("mostTimeDifference"));
                leastTimeDifferenceLabel = new Label("평균 발생 시간이 가장 근접한 환경변수 : " + visualizationInfo.get("leastTimeDifference"));
                infoVBox.getChildren().addAll(shortestDistanceLabel, longestDistanceLabel, mostTimeDifferenceLabel, leastTimeDifferenceLabel);
                vvbox.getChildren().addAll(space, titleLabel, infoVBox);
                briefingVisualizationData.getChildren().add(vvbox);
            }
            space = new Label(" ");
            briefingVisualizationData.getChildren().add(space);

//            briefingPane (VBox임) 속에
//
//            이후 시각화 대상에 대한 for 루프로
//                    하나의 HBox를 만들고 그 속에
//              해당 시각화 대상과의 평균 거리가 가장 가까운 것과
//              해당 시각화 대상과의 평균 거리가 가장 먼 것,
//            해당 시각화 대상과의 시간 차가 가장 적은 것과
//            해당 시각화 대상과의 시간 차가 가장 먼 것,
//                    따라서
//            해당 시각화 발생 가능성을 높이는 가장 큰 원인과
//            해당 시각화 발생 가능성을 내리는 가장 큰 원인,
//            해당 시각화 발생 가능성이 가장 높은 섹터와
//            해당 시각화 발생 가능성이 가장 낮은 섹터,
//            해당 시각화 발생 가능성이 가장 높았던 년도와
//            해당 시각화 발생 가능성이 가장 낮았던 년도
//            해당 시각화 발생 가능성이 가장 높았던 월과
//            해당 시각화 발생 가능성이 가장 낮았던 월,
//            해당 시각화 발생 가능성이 가장 높았던 요일과
//            해당 시각화 발생 가능성이 가장 낮았던 요일,
//            해당 시각화 발생 가능성이 가장 높았던 시간과
//            해당 시각화 발생 가능성이 가장 낮았던 시간도 짚어주기.
//
//                다 하고 나면 임의 범죄 데이터 넣어서 돌려보기
        });
    }

    @FXML private void save(){
        SoundUtil.playSound(SoundType.ENABLE);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("시각화 DB 다른 이름으로 저장");
        FileChooser.ExtensionFilter dbFilter = new FileChooser.ExtensionFilter("SQLite 데이터베이스 파일", "*.db");
        fileChooser.getExtensionFilters().addAll(dbFilter);
        File file = fileChooser.showSaveDialog(mainPane.getScene().getWindow());
        if (file != null) {
            FileInputStream dataFis;
            FileOutputStream dataFos;
            try
            {
                File openedDataDb = new File(Absolute.absoluteDB.getDatabaseName());
                String path = file.getAbsolutePath();
                String type = path.substring(path.lastIndexOf("."));
                path = path.substring(0, path.lastIndexOf(type));
                File newDataFile = new File(path + ".visualization.data.db");
                if(!newDataFile.exists())
                    newDataFile.createNewFile();
                byte[] b = new byte[4096];
                dataFis = new FileInputStream(openedDataDb);
                dataFos = new FileOutputStream(newDataFile);
                int cnt = 0;
                while((cnt=dataFis.read(b)) != -1){
                    dataFos.write(b, 0, cnt);
                }
                new AlarmAPI().fileExportSuccess();
                dataFis.close();
                dataFos.close();
            }
            catch(IOException e1) {
                new AlarmAPI().fileExportError();
            }
        }
    }

    @FXML private void load(){
        SoundUtil.playSound(SoundType.ENABLE);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("시각화 DB 불러오기");
        FileChooser.ExtensionFilter dbFilter = new FileChooser.ExtensionFilter("SQLite 데이터베이스 파일", "*.db");
        fileChooser.getExtensionFilters().addAll(dbFilter);
        File selectedFiles = fileChooser.showOpenDialog(mainPane.getScene().getWindow());

        if (selectedFiles != null) {
            AbsoluteDataAPI adAPI = new AbsoluteDataAPI(selectedFiles.getPath());
            if(adAPI.isVisualizationDb()) {
                Absolute.absoluteDB.close();
                Absolute.absoluteDB = new AbsoluteDataAPI(selectedFiles.getPath());
                new VisualizationMainView().view();
                Stage stage = (Stage) mainPane.getScene().getWindow();
                SoundUtil.playSound(SoundType.DISABLE);
                stage.close();
            } else {
                new AlarmAPI().notVisualizationDbFile();
            }
            adAPI.close();
        }
    }

    @FXML private void selectAll(){
        allSelect(true);
    }
    @FXML private void unselectAll(){
        allSelect(false);
    }
    private void allSelect(boolean isSelect) {
        CheckBox cb;
        for(Node n : visualizationTargetVbox.getChildren()) {
            cb = (CheckBox) n;
            cb.setSelected(isSelect);
        }
        for(Node n : environmentVariableVbox.getChildren()) {
            cb = (CheckBox) n;
            cb.setSelected(isSelect);
        }
        if(!isSelect) {
            selectedEnviromentTarget.clear();
            selectedVisualizationTarget.clear();
        } else {
            selectedEnviromentTarget = Absolute.absoluteDB.getEnviromentVariable();
            selectedVisualizationTarget = Absolute.absoluteDB.getVisualizationTarget();
        }
    }

    @FXML private void showMap(){
        show(true, false, false, false, false);
    }
    @FXML private void showTable(){
        show(false, true, false, false, false);
    }
    @FXML private void showPieChart(){
        show(false, false, true, false, false);
    }
    @FXML private void showLineGraph(){
        show(false, false, false, true, false);
    }
    @FXML private void showBrief(){
        show(false, false, false, false, true);
    }
    private void show(boolean isMap, boolean isTable, boolean isPie, boolean isLineGraph, boolean isBrief){
        SoundUtil.playSound(SoundType.CHECK);
        if(map != null) {
            map.setVisible(isMap);
            mapInfoLabel.setVisible(isMap);
        }
        if(table != null) {
            table.setVisible(isTable);
            tableInfoLabel.setVisible(isTable);
        }
        if(pieChart != null) {
            pieChartGridPane.setVisible(isPie);
            pieInfoLabel.setVisible(isPie);
        }
        if(lineChartGridPane != null) {
            lineChartGridPane.setVisible(isLineGraph);
            lineInfoLabel.setVisible(isLineGraph);
        }
        if(briefingPane != null) {
            briefingPane.setVisible(isBrief);
        }
        timeAllRadio.setVisible(!isLineGraph);
        if(isMap) {
            vt = VisualizationType.MAP;
            sectorSelectPane.setExpanded(false);
            orderByPane.setExpanded(false);
            sectorSelectPane.setDisable(true);
            orderByPane.setDisable(true);
        } else {
            sectorSelectPane.setDisable(false);
            orderByPane.setDisable(false);
        }
        if(isTable) {
            vt = VisualizationType.TABLE;
        }
        if(isPie) {
            vt = VisualizationType.PIE_CHART;
        }
        if(isLineGraph) {
            vt = VisualizationType.LINE_CHART;
            if(timeAllRadio.isSelected()) {
                timeYearRadio.setSelected(true);
            }
        }
        accordion.setDisable(isBrief);
        calcButton.setDisable(isBrief);
        maxTimeVisible(absoluteCalcRadio.isSelected());
    }

    @FXML private void showTimeDetail(){
        maxTimeVisible(true);
    }
    @FXML private void hideTimeDetail(){
        maxTimeVisible(false);
    }
    private void maxTimeVisible(boolean visible) {
        timeMaxYear.setVisible(visible);
        timeYearSlash.setVisible(visible);
        timeMaxMonth.setVisible(visible);
        timeMonthSlash.setVisible(visible);
        timeMaxWeek.setVisible(visible);
        timeWeekSlash.setVisible(visible);
        timeMaxHour.setVisible(visible);
        timeHourSlash.setVisible(visible);
        limitHbox.setVisible(true);
        if(vt == VisualizationType.LINE_CHART) {
            limitHbox.setVisible(false);
            timeMaxYear.setVisible(true);
            timeYearSlash.setVisible(true);
            timeMaxMonth.setVisible(true);
            timeMonthSlash.setVisible(true);
            timeMaxWeek.setVisible(true);
            timeWeekSlash.setVisible(true);
            timeMaxHour.setVisible(true);
            timeHourSlash.setVisible(true);
        }
        appearBonusVbox.setVisible(!visible);
    }

    @FXML private void sectorUnSelected(ActionEvent e){
        sectorNumberField.setDisable(true);
    }
    @FXML private void sectorSelected(ActionEvent e){
        sectorNumberField.setDisable(false);
    }

    @FXML private void timeMinYearSelect(ActionEvent e){
        String selectedTime = ((MenuItem) e.getSource()).getText();
        String otherTime = timeMaxYear.getText();
        if(Integer.parseInt(timeMaxYear.getText()) < Integer.parseInt(selectedTime)) {
            timeMinYear.setText(otherTime);
            timeMaxYear.setText(selectedTime);
        } else {
            timeMinYear.setText(selectedTime);
        }
    }
    @FXML private void timeMaxYearSelect(ActionEvent e){
        String selectedTime = ((MenuItem) e.getSource()).getText();
        String otherTime = timeMinYear.getText();
        if(Integer.parseInt(timeMinYear.getText()) > Integer.parseInt(selectedTime)) {
            timeMinYear.setText(selectedTime);
            timeMaxYear.setText(otherTime);
        } else {
            timeMaxYear.setText(selectedTime);
        }
    }

    @FXML private void timeMinMonthSelect(ActionEvent e){
        String selectedTime = ((MenuItem) e.getSource()).getText();
        String otherTime = timeMaxMonth.getText();
        if(Integer.parseInt(timeMaxMonth.getText()) < Integer.parseInt(selectedTime)) {
            timeMinMonth.setText(otherTime);
            timeMaxMonth.setText(selectedTime);
        } else {
            timeMinMonth.setText(selectedTime);
        }
    }
    @FXML private void timeMaxMonthSelect(ActionEvent e){
        String selectedTime = ((MenuItem) e.getSource()).getText();
        String otherTime = timeMinMonth.getText();
        if(Integer.parseInt(timeMinMonth.getText()) > Integer.parseInt(selectedTime)) {
            timeMinMonth.setText(selectedTime);
            timeMaxMonth.setText(otherTime);
        } else {
            timeMaxMonth.setText(selectedTime);
        }
    }

    @FXML private void timeMinWeekSelect(ActionEvent e){
        String selectedTime = ((MenuItem) e.getSource()).getText();
        String otherTime = timeMaxWeek.getText();
        if(getWeekToInteger(timeMaxWeek.getText()) < getWeekToInteger(selectedTime)) {
            timeMinWeek.setText(otherTime);
            timeMaxWeek.setText(selectedTime);
        } else {
            timeMinWeek.setText(selectedTime);
        }
    }
    @FXML private void timeMaxWeekSelect(ActionEvent e){
        String selectedTime = ((MenuItem) e.getSource()).getText();
        String otherTime = timeMinWeek.getText();
        if(getWeekToInteger(timeMinWeek.getText()) > getWeekToInteger(selectedTime)) {
            timeMinWeek.setText(selectedTime);
            timeMaxWeek.setText(otherTime);
        } else {
            timeMaxWeek.setText(selectedTime);
        }
    }

    @FXML private void timeMinHourSelect(ActionEvent e){
        String selectedTime = ((MenuItem) e.getSource()).getText();
        String otherTime = timeMaxHour.getText();
        if(Integer.parseInt(timeMaxHour.getText()) < Integer.parseInt(selectedTime)) {
            timeMinHour.setText(otherTime);
            timeMaxHour.setText(selectedTime);
        } else {
            timeMinHour.setText(selectedTime);
        }
    }
    @FXML private void timeMaxHourSelect(ActionEvent e){
        String selectedTime = ((MenuItem) e.getSource()).getText();
        String otherTime = timeMinHour.getText();
        if(Integer.parseInt(timeMinHour.getText()) > Integer.parseInt(selectedTime)) {
            timeMinHour.setText(selectedTime);
            timeMaxHour.setText(otherTime);
        } else {
            timeMaxHour.setText(selectedTime);
        }
    }

    @FXML private void calculate(){
        if(! calculating) {
            calculating = true;
            calcButton.setDisable(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(()->{
                        TimeEnum selectedTime;
                        String startTargetTime = null;
                        String endTargetTime = null;
                        boolean isAbsoluteCalc = absoluteCalcRadio.isSelected();
                        if(timeYearRadio.isSelected()){
                            selectedTime = TimeEnum.YEAR;
                            startTargetTime = timeMinYear.getText();
                            endTargetTime = timeMaxYear.getText();
                        } else if(timeMonthRadio.isSelected()) {
                            selectedTime = TimeEnum.MONTH;
                            startTargetTime = timeMinMonth.getText();
                            endTargetTime = timeMaxMonth.getText();
                        } else if(timeWeekRadio.isSelected()) {
                            selectedTime = TimeEnum.WEEK;
                            startTargetTime = Integer.toString(getWeekToInteger(timeMinWeek.getText()));
                            endTargetTime = Integer.toString(getWeekToInteger(timeMaxWeek.getText()));
                        } else if(timeHourRadio.isSelected()) {
                            selectedTime = TimeEnum.HOUR;
                            startTargetTime = timeMinHour.getText();
                            endTargetTime = timeMaxHour.getText();
                        } else {
                            selectedTime = TimeEnum.ALL;
                        }
                        boolean equalizer = equalizerCheckBox.isSelected();
                        if(vt == VisualizationType.MAP) {
                            int maxLevel = 8;
                            Map<String, Double> settingMap = Absolute.absoluteDB.getSettings();
                            int rows = settingMap.get("rows").intValue();
                            int columns = settingMap.get("columns").intValue();
                            int maxSector = rows * columns;
                            double startLatitude = settingMap.get("startLatitude");
                            double startLongitude = settingMap.get("startLongitude");
                            double endLatitude = settingMap.get("endLatitude");
                            double endLongitude = settingMap.get("endLongitude");
                            double latiSize = (endLatitude - startLatitude) / rows;
                            double longiSize = (endLongitude - startLongitude) / columns;

                            if(isAbsoluteCalc) {
                                mapAbsoluteCalculate(selectedTime, startTargetTime, endTargetTime, rows, columns, maxSector, startLatitude, startLongitude, endLatitude, endLongitude, latiSize, longiSize, maxLevel);
                            } else {
                                mapOccurrenceCalculate(selectedTime, startTargetTime, endTargetTime, rows, columns, maxSector, startLatitude, startLongitude, endLatitude, endLongitude, latiSize, longiSize, maxLevel, equalizer);
                            }
                        } else {
                            int selectedSector;
                            if(allSectorRadio.isSelected()) {
                                selectedSector = -1;
                            } else {
                                selectedSector = sectorNumberField.getNumber()-1;
                            }
                            boolean desc = ! ascendingRadio.isSelected();
                            int limit = limitField.getNumber();
                            if(vt == VisualizationType.LINE_CHART) {
                                if(isAbsoluteCalc) {
                                    if(selectedVisualizationTarget.size() + selectedEnviromentTarget.size() > 10) {
                                        new AlarmAPI().tooMuchData();
                                    } else {
                                        Map<String, Map<String, Double>> sorted = lineChartAbsoluteCalculate(selectedTime, startTargetTime, endTargetTime, selectedSector, desc, lineInfoLabel);
                                        drawLineChart(selectedTime, isAbsoluteCalc, selectedSector, sorted);
                                    }
                                } else {
                                    if(selectedVisualizationTarget.isEmpty()) {
                                        new AlarmAPI().notSelectedVisualizationTarget();
                                    } else {
                                        Map<String, Double> settingMap = Absolute.absoluteDB.getSettings();
                                        int rows = settingMap.get("rows").intValue();
                                        int columns = settingMap.get("columns").intValue();
                                        int maxSector = rows * columns;
                                        Map<String, Double> sorted = lineChartOccurrenceCalculate(selectedTime, startTargetTime, endTargetTime, selectedSector, maxSector, columns, desc, lineInfoLabel, equalizer);
                                        drawLineChartOccurrence(selectedTime, isAbsoluteCalc, selectedSector, sorted);
                                    }
                                }
                            } else {
                                List<VisualizationTableObject> sorted = new ArrayList<>();
                                if(isAbsoluteCalc) {
                                    if(vt == VisualizationType.TABLE) {
                                        sorted = tableAbsoluteCalculate(selectedTime, startTargetTime, endTargetTime, selectedSector, desc, limit, tableInfoLabel);
                                    } else if(vt == VisualizationType.PIE_CHART) {
                                        sorted = tableAbsoluteCalculate(selectedTime, startTargetTime, endTargetTime, selectedSector, desc, limit, pieInfoLabel);
                                    }
                                } else {
                                    Map<String, Double> settingMap = Absolute.absoluteDB.getSettings();
                                    int rows = settingMap.get("rows").intValue();
                                    int columns = settingMap.get("columns").intValue();
                                    int maxSector = rows * columns;
                                    if(vt == VisualizationType.TABLE) {
                                        sorted = tableOccurrenceCalculate(selectedTime, startTargetTime, columns, maxSector, desc, limit, tableInfoLabel, selectedSector, equalizer);
                                    } else if(vt == VisualizationType.PIE_CHART) {
                                        sorted = tableOccurrenceCalculate(selectedTime, startTargetTime, columns, maxSector, desc, limit, pieInfoLabel, selectedSector, equalizer);
                                    }
                                }
                                if(vt == VisualizationType.TABLE) {
                                    drawTable(isAbsoluteCalc, selectedSector, sorted);
                                } else if(vt == VisualizationType.PIE_CHART) {
                                    drawPieChart(isAbsoluteCalc, selectedSector, sorted);
                                }
                            }
                        }
                        calculating = false;
                        calcButton.setDisable(false);
                    });
                }
            }).start();
        }
    }

    private void mapAbsoluteCalculate(TimeEnum selectedTime, String startTargetTime, String endTargetTime, int rows, int columns, int maxSector, double startLatitude, double startLongitude, double endLatitude, double endLongitude, double latiSize, double longiSize, int maxLevel){
        Map<Integer, Map<String, Double>> enviromentMap = Absolute.absoluteDB.getAbsoluteAmountOfEnviroment(selectedTime, startTargetTime, startTargetTime, selectedEnviromentTarget);
        Map<Integer, Map<String, Double>> visualizationTargetMap = Absolute.absoluteDB.getAbsoluteAmountOfVisualizationTarget(selectedTime, startTargetTime, startTargetTime, selectedVisualizationTarget);
        long minAmount = Long.MAX_VALUE;
        long maxAmount = Long.MIN_VALUE;
        double amount;

        for(int sector = 0; sector < maxSector; sector++) {
            amount = 0;
            if(enviromentMap.containsKey(sector)) {
                for(String element : enviromentMap.get(sector).keySet()) {
                    amount += enviromentMap.get(sector).get(element);
                }
            }
            if(visualizationTargetMap.containsKey(sector)) {
                for(String element : visualizationTargetMap.get(sector).keySet()) {
                    amount += visualizationTargetMap.get(sector).get(element);
                }
            }
            if(minAmount > amount) {
                minAmount = (long)amount;
            }
            if(maxAmount < amount) {
                maxAmount = (long)amount;
            }
        }
        double layer = (maxAmount - minAmount) / maxLevel;
        map.getEngine().executeScript("clear()");
        for(int count = 0; count < maxSector; count++) {
            drawMapAbsoluteResult(rows, count, latiSize, longiSize, startLongitude, endLatitude, minAmount, layer, enviromentMap.get(count), visualizationTargetMap.get(count));
        }
        mapInfoLabel.setText("선택된 대상의 절대건수가 높은 지역");
    }

    private void mapOccurrenceCalculate(TimeEnum selectedTime, String startTargetTime, String endTargetTime, int rows, int columns, int maxSector, double startLatitude, double startLongitude, double endLatitude, double endLongitude, double latiSize, double longiSize, int maxLevel, boolean equalizer){
        if(selectedVisualizationTarget.isEmpty()) {
            new AlarmAPI().notSelectedVisualizationTarget();
            return;
        } else {
            Map<Integer, Map<String, Double>> enviromentMap = Absolute.absoluteDB.getAbsoluteAmountOfEnviroment(selectedTime, startTargetTime, startTargetTime, selectedEnviromentTarget);
            Map<Integer, Map<String, Double>> visualizationTargetMap = Absolute.absoluteDB.getAbsoluteAmountOfVisualizationTarget(selectedTime, startTargetTime, startTargetTime, selectedVisualizationTarget);
            Map<Integer, Map<String, Double>> geoDataDifferenceMap;
            Map<String, Double> dataDifferenceMap;
            Map<Integer, Map<String, Double>> geoDistanceMap;
            String[] splitted;
            Map<Integer, Double> totalValueMap = new HashMap<>();
            Map<Integer, Double> adjacentSectorBonusMap = new HashMap<>();
            Map<Integer, String> enviromentLoreMap = new HashMap<>();
            Map<Integer, String> targetLoreMap = new HashMap<>();
            Map<Integer, String> adjacentLoreMap = new HashMap<>();
            Map<String, Double> equalizerMap = new HashMap<>();
            if(equalizer) {
                equalizerMap = Absolute.absoluteDB.getEqualizer();
            }
            String loreLine;
            double totalValue;
            double targetValue;
            double sectorTimeAvgPower = sectorTimeAvgBonus.getReal();
            double timeAvgPower = timeAvgBonus.getReal();
            double distanceAvgPower = distanceAvgBonus.getReal();
            for(String target : selectedVisualizationTarget) {
                splitted = target.split(Main.SPLIT_SYMBOL);
                geoDataDifferenceMap = Absolute.absoluteDB.getGeoAbsoluteDifference(selectedTime, sectorTimeAvgPower, splitted[0], splitted[1], selectedEnviromentTarget);
                dataDifferenceMap = Absolute.absoluteDB.getAbsoluteDifference(selectedTime, timeAvgPower, splitted[0], splitted[1], selectedEnviromentTarget);
                geoDistanceMap = Absolute.absoluteDB.getGeoDistance(selectedTime, startTargetTime, distanceAvgPower, splitted[0], splitted[1], selectedEnviromentTarget);
                if(equalizer && ! equalizerMap.isEmpty()) {
                    for(String ele : equalizerMap.keySet()) {
                        for(int sector : geoDataDifferenceMap.keySet()) {
                            if(geoDataDifferenceMap.get(sector).containsKey(ele)) {
                                geoDataDifferenceMap.get(sector).put(ele, geoDataDifferenceMap.get(sector).get(ele) * equalizerMap.get(ele));
                            }
                        }
                        for(int sector : geoDistanceMap.keySet()) {
                            if(geoDistanceMap.get(sector).containsKey(ele)) {
                                geoDistanceMap.get(sector).put(ele, geoDistanceMap.get(sector).get(ele) * equalizerMap.get(ele));
                            }
                        }
                        if(dataDifferenceMap.containsKey(ele)) {
                            dataDifferenceMap.put(ele, dataDifferenceMap.get(ele) * equalizerMap.get(ele));
                        }
                    }
                }

                for(int count = 0; count <maxSector; count++) {
                    if(totalValueMap.containsKey(count)) {
                        totalValue = totalValueMap.get(count);
                    } else {
                        totalValue = 0;
                    }
                    totalValue += getMultipleValue(enviromentMap.get(count), geoDataDifferenceMap.get(count), enviromentLoreMap, count, "시각화 대상 및 지역간 발생 시간차");
                    totalValue += getMultipleValue(enviromentMap.get(count), dataDifferenceMap, enviromentLoreMap, count, "시각화 대상간 발생 시간차");
                    totalValue += getMultipleValue(enviromentMap.get(count), geoDistanceMap.get(count), enviromentLoreMap, count, "시각화 대상간 거리");
                    if(visualizationTargetMap.containsKey(count) && visualizationTargetMap.get(count).containsKey(target)) {
                        targetValue = visualizationTargetMap.get(count).get(target);
                        totalValue += targetValue;
                        if(targetLoreMap.containsKey(count)) {
                            loreLine = targetLoreMap.get(count) + "<br>";
                        } else {
                            loreLine = "";
                        }
                        loreLine += "&nbsp;&nbsp;" + target.replace(Main.SPLIT_SYMBOL+"NULL", "").replace(Main.SPLIT_SYMBOL, "::") + " : " + targetValue;
                        targetLoreMap.put(count, loreLine);
                    }
                    totalValueMap.put(count, totalValue);
                }
            }

            double adjacentBonusTemp;
            double adjacentSectorPower = adjacentSectorBonus.getReal();
            int[] affectedSectorArray = new int[8];
            for(int sector : totalValueMap.keySet()) {
                affectedSectorArray[0] = sector+1;
                affectedSectorArray[1] = sector-1;
                affectedSectorArray[2] = sector-columns;
                affectedSectorArray[3] = sector-columns+1;
                affectedSectorArray[4] = sector-columns-1;
                affectedSectorArray[5] = sector+columns;
                affectedSectorArray[6] = sector+columns+1;
                affectedSectorArray[7] = sector+columns-1;
                for(int affectedSector : affectedSectorArray) {
                    if(affectedSector > -1 && affectedSector < maxSector && totalValueMap.get(sector) != 0) {
                        if(adjacentSectorBonusMap.containsKey(affectedSector)) {
                            adjacentBonusTemp = adjacentSectorBonusMap.get(affectedSector);
                        } else {
                            adjacentBonusTemp = 0;
                        }
                        adjacentBonusTemp += adjacentSectorPower * totalValueMap.get(sector);
                        adjacentSectorBonusMap.put(affectedSector, adjacentBonusTemp);
                    }
                }
            }
            for(int sector : adjacentSectorBonusMap.keySet()) {
                adjacentBonusTemp = adjacentSectorBonusMap.get(sector);
                if(adjacentBonusTemp != 0) {
                    totalValueMap.put(sector, totalValueMap.get(sector) + adjacentBonusTemp);
                    adjacentLoreMap.put(sector, "&nbsp;&nbsp;인접지역 보너스 : " + String.format("%.2f", adjacentBonusTemp));
                }
            }


            long minAmount = Long.MAX_VALUE;
            long maxAmount = Long.MIN_VALUE;
            double amount = 0;

            for(int sector = 0; sector < maxSector; sector++) {
                if(totalValueMap.containsKey(sector)) {
                    amount = totalValueMap.get(sector);
                }
                if(minAmount > amount) {
                    minAmount = (long)amount;
                }
                if(maxAmount < amount) {
                    maxAmount = (long)amount;
                }
            }
            double layer = (maxAmount - minAmount) / maxLevel;
            map.getEngine().executeScript("clear()");
            for(int count = 0; count < maxSector; count++) {
                drawMapOccurrenceResult(rows, count, latiSize, longiSize, startLongitude, endLatitude, minAmount, layer, totalValueMap.get(count), targetLoreMap.get(count), enviromentLoreMap.get(count), adjacentLoreMap.get(count));
            }
            String targetString = getTargetString();
            boolean isSelectedSameElement = Boolean.parseBoolean(targetString.substring(0, targetString.indexOf("§")));
            targetString = targetString.substring(targetString.indexOf("§")+1);
            StringBuilder infoLore = new StringBuilder();
            infoLore.append(targetString.substring(0, targetString.length()-2));
            if(selectedTime == TimeEnum.YEAR) {
                infoLore.append("의 " + startTargetTime+"년도 발생 가능성이 높았던 지역");
            } else if(selectedTime == TimeEnum.MONTH) {
                infoLore.append("의 "+ startTargetTime+"월 발생 가능성이 높은 지역");
            } else if(selectedTime == TimeEnum.WEEK) {
                infoLore.append("의 "+ getIntegerToWeek(Integer.parseInt(startTargetTime))+"요일 발생 가능성이 높은 지역");
            } else if(selectedTime == TimeEnum.HOUR) {
                infoLore.append("의 "+ startTargetTime+"시 발생 가능성이 높은 지역");
            } else if(selectedTime == TimeEnum.ALL) {
                infoLore.append("의 향후 발생 가능성이 높은 지역");
            }
            if(isSelectedSameElement) {
                infoLore.append("\n(시각화 대상과 동일한 카테고리 속 환경변수를 선택하여 정확도가 떨어졌을 가능성이 있습니다.)");
            }
            mapInfoLabel.setText(infoLore.toString());
        }
    }

    private double getMultipleValue(Map<String, Double> amount, Map<String, Double> multiple, Map<Integer, String> lore, int sector, String category){
        double returnDouble = 0;
        if(amount != null && multiple != null) {
            double multipled;
            String loreLine;
            if(lore != null) {
                if(lore.containsKey(sector)) {
                    loreLine = lore.get(sector);
                    loreLine += "<br><br>&nbsp;&nbsp;["+category+"]";
                } else {
                    loreLine = "&nbsp;&nbsp;["+category+"]";
                }
                for(String key : amount.keySet()) {
                    if(multiple.containsKey(key)) {
                        multipled = (amount.get(key) * multiple.get(key));
                        returnDouble += multipled;
                        loreLine += "<br>&nbsp;&nbsp;&nbsp;&nbsp;" + key.replace(Main.SPLIT_SYMBOL+"NULL", "").replace(Main.SPLIT_SYMBOL, "::") + " : " + String.format("%.2f", multipled);
                        lore.put(sector, loreLine);
                    }
                }
            } else {
                for(String key : amount.keySet()) {
                    if(multiple.containsKey(key)) {
                        returnDouble += (amount.get(key) * multiple.get(key));
                    }
                }
            }
        }
        return returnDouble;
    }

    private List<VisualizationTableObject> tableAbsoluteCalculate(TimeEnum selectedTime, String startTargetTime, String endTargetTime, int selectedSector, boolean desc, int limit, Label infoLabel){
        List<VisualizationTableObject> sortedList = new ArrayList<>();
        List<VisualizationTableObject> enviromentList = Absolute.absoluteDB.getAbsoluteAmountOfEnviroment(selectedTime, startTargetTime, endTargetTime, selectedEnviromentTarget, selectedSector, desc);
        List<VisualizationTableObject> visualizationTargetList = Absolute.absoluteDB.getAbsoluteAmountOfVisualizationTarget(selectedTime, startTargetTime, endTargetTime, selectedVisualizationTarget, selectedSector, desc);

        int enviromentIndex = 0;
        int visualizationTargetIndex = 0;

        double enviromentValue;
        double visualizationValue;
        for(int count = 0; count < limit; count++) {
            if(enviromentIndex < enviromentList.size() || visualizationTargetIndex < visualizationTargetList.size()) {
                if(desc) {
                    if(enviromentIndex >= enviromentList.size()) {
                        enviromentValue = Double.MIN_VALUE;
                    } else {
                        enviromentValue = Double.parseDouble(enviromentList.get(enviromentIndex).amount);
                    }
                    if(visualizationTargetIndex >= visualizationTargetList.size()) {
                        visualizationValue = Double.MIN_VALUE;
                    } else {
                        visualizationValue = Double.parseDouble(visualizationTargetList.get(visualizationTargetIndex).amount);
                    }
                    if(enviromentValue > visualizationValue) {
                        sortedList.add(new VisualizationTableObject((count+1), enviromentList.get(enviromentIndex)));
                        enviromentIndex++;
                    } else {
                        sortedList.add(new VisualizationTableObject((count+1),visualizationTargetList.get(visualizationTargetIndex)));
                        visualizationTargetIndex++;
                    }
                } else {
                    if(enviromentIndex >= enviromentList.size()) {
                        enviromentValue = Double.MAX_VALUE;
                    } else {
                        enviromentValue = Double.parseDouble(enviromentList.get(enviromentIndex).amount);
                    }
                    if(visualizationTargetIndex >= visualizationTargetList.size()) {
                        visualizationValue = Double.MAX_VALUE;
                    } else {
                        visualizationValue = Double.parseDouble(visualizationTargetList.get(visualizationTargetIndex).amount);
                    }
                    if(enviromentValue < visualizationValue) {
                        sortedList.add(new VisualizationTableObject((count+1),enviromentList.get(enviromentIndex)));
                        enviromentIndex++;
                    } else {
                        sortedList.add(new VisualizationTableObject((count+1),visualizationTargetList.get(visualizationTargetIndex)));
                        visualizationTargetIndex++;
                    }
                }
            } else {
                break;
            }
        }
        if(vt != VisualizationType.LINE_CHART) {
            infoLabel.setText("선택된 대상의 절대건수가 높은 통계 결과");
        } else {
            StringBuilder infoLore = new StringBuilder();
            if(selectedSector == -1) {
                if(selectedTime == TimeEnum.YEAR) {
                    infoLore.append("전체지역 대상 연도별 발생횟수");
                } else if(selectedTime == TimeEnum.MONTH) {
                    infoLore.append("전체지역 대상 월별 발생횟수");
                } else if(selectedTime == TimeEnum.WEEK) {
                    infoLore.append("전체지역 대상 요일별 발생횟수");
                } else if(selectedTime == TimeEnum.HOUR) {
                    infoLore.append("전체지역 대상 시간별 발생횟수");
                } else if(selectedTime == TimeEnum.ALL) {
                    infoLore.append("전체지역 대상 통합 발생횟수");
                }
            } else {
                infoLore.append((selectedSector+1) + "번 섹터 대상 ");
                if(selectedTime == TimeEnum.YEAR) {
                    infoLore.append("연도별 발생횟수");
                } else if(selectedTime == TimeEnum.MONTH) {
                    infoLore.append("월별 발생횟수");
                } else if(selectedTime == TimeEnum.WEEK) {
                    infoLore.append("요일별 발생횟수");
                } else if(selectedTime == TimeEnum.HOUR) {
                    infoLore.append("시간별 발생횟수");
                } else if(selectedTime == TimeEnum.ALL) {
                    infoLore.append("통합 발생횟수");
                }
            }
            infoLabel.setText(infoLore.toString());
        }
        return sortedList;
    }

    private List<VisualizationTableObject> tableOccurrenceCalculate(TimeEnum selectedTime, String startTargetTime, int columns, int maxSector, boolean desc, int limit, Label infoLabel, int targetSector, boolean equalizer){
        List<VisualizationTableObject> returnList = new ArrayList<>();
        if(selectedVisualizationTarget.isEmpty()) {
            new AlarmAPI().notSelectedVisualizationTarget();
        } else {
            returnList = getTotalOccurrenceBonus(selectedTime, startTargetTime, columns, maxSector, desc, limit, targetSector, equalizer);
            String targetString = getTargetString();
            boolean isSelectedSameElement = Boolean.parseBoolean(targetString.substring(0, targetString.indexOf("§")));
            targetString = targetString.substring(targetString.indexOf("§")+1);
            StringBuilder infoLore = new StringBuilder();
            if(targetSector == -1) {
                infoLore.append(targetString.substring(0, targetString.length()-2));
                if(selectedTime == TimeEnum.YEAR) {
                    infoLore.append("의 " + startTargetTime+"년도 발생 가능성이 높은 섹터");
                } else if(selectedTime == TimeEnum.MONTH) {
                    infoLore.append("의 "+ startTargetTime+"월 발생 가능성이 높은 섹터");
                } else if(selectedTime == TimeEnum.WEEK) {
                    infoLore.append("의 "+ getIntegerToWeek(Integer.parseInt(startTargetTime))+"요일 발생 가능성이 높은 섹터");
                } else if(selectedTime == TimeEnum.HOUR) {
                    infoLore.append("의 "+ startTargetTime+"시 발생 가능성이 높은 섹터");
                } else if(selectedTime == TimeEnum.ALL) {
                    infoLore.append("의 향후 발생 가능성이 높은 섹터");
                }
            } else {
                infoLore.append((targetSector+1) + "번 섹터 속 ");
                infoLore.append(targetString.substring(0, targetString.length()-2));
                if(selectedTime == TimeEnum.YEAR) {
                    infoLore.append("의 " + startTargetTime+"년도 발생 가능성을 향상시키는 변수");
                } else if(selectedTime == TimeEnum.MONTH) {
                    infoLore.append("의 "+ startTargetTime+"월 발생 가능성을 향상시키는 변수");
                } else if(selectedTime == TimeEnum.WEEK) {
                    infoLore.append("의 "+ getIntegerToWeek(Integer.parseInt(startTargetTime))+"요일 발생 가능성을 향상시키는 변수");
                } else if(selectedTime == TimeEnum.HOUR) {
                    infoLore.append("의 "+ startTargetTime+"시 발생 가능성을 향상시키는 변수");
                } else if(selectedTime == TimeEnum.ALL) {
                    infoLore.append("의 향후 발생 가능성을 향상시키는 변수");
                }
            }
            if(isSelectedSameElement) {
                infoLore.append("\n(시각화 대상과 동일한 카테고리 속 환경변수를 선택하여 정확도가 떨어졌을 가능성이 있습니다.)");
            }
            infoLabel.setText(infoLore.toString());
        }
        return returnList;
    }

    private List<VisualizationTableObject> getTotalOccurrenceBonus(TimeEnum selectedTime, String startTargetTime, int columns, int maxSector, boolean desc, int limit, int targetSector, boolean equalizer){
        List<VisualizationTableObject> returnList = new ArrayList<>();
        Map<Integer, Map<String, Double>> enviromentMap = Absolute.absoluteDB.getAbsoluteAmountOfEnviroment(selectedTime, startTargetTime, startTargetTime, selectedEnviromentTarget);
        Map<Integer, Map<String, Double>> visualizationTargetMap = Absolute.absoluteDB.getAbsoluteAmountOfVisualizationTarget(selectedTime, startTargetTime, startTargetTime, selectedVisualizationTarget);

        Map<Integer, Map<String, Double>> geoDataDifferenceMap;
        Map<String, Double> dataDifferenceMap;
        Map<Integer, Map<String, Double>> geoDistanceMap;
        Map<String, Double> equalizerMap = new HashMap<>();
        if(equalizer) {
            equalizerMap = Absolute.absoluteDB.getEqualizer();
        }
        Map<Integer, Double> adjacentSectorBonusMap = new HashMap<>();
        String[] splitted;
        Map<Integer, List<VisualizationTableObject>> vtoMap = new HashMap<>();
        double sectorTimeAvgPower = sectorTimeAvgBonus.getReal();
        double timeAvgPower = timeAvgBonus.getReal();
        double distanceAvgPower = distanceAvgBonus.getReal();
        List<VisualizationTableObject> vtoList;
        VisualizationTableObject vto;
        TimeEnum vtoTimeEnum = selectedTime;
        String vtoStartTime = startTargetTime;
        if(selectedTime==TimeEnum.WEEK) {
            vtoStartTime = getIntegerToWeek(Integer.parseInt(startTargetTime));
        }
        if(vtoTimeEnum == TimeEnum.ALL) {
            vtoTimeEnum = TimeEnum.YEAR;
        }
        for(String target : selectedVisualizationTarget) {
            splitted = target.split(Main.SPLIT_SYMBOL);
            geoDataDifferenceMap = Absolute.absoluteDB.getGeoAbsoluteDifference(selectedTime, sectorTimeAvgPower, splitted[0], splitted[1], selectedEnviromentTarget);
            dataDifferenceMap = Absolute.absoluteDB.getAbsoluteDifference(selectedTime, timeAvgPower, splitted[0], splitted[1], selectedEnviromentTarget);
            geoDistanceMap = Absolute.absoluteDB.getGeoDistance(selectedTime, startTargetTime, distanceAvgPower, splitted[0], splitted[1], selectedEnviromentTarget);
            if(equalizer && ! equalizerMap.isEmpty()) {
                for(String ele : equalizerMap.keySet()) {
                    for(int sector : geoDataDifferenceMap.keySet()) {
                        if(geoDataDifferenceMap.get(sector).containsKey(ele)) {
                            geoDataDifferenceMap.get(sector).put(ele, geoDataDifferenceMap.get(sector).get(ele) * equalizerMap.get(ele));
                        }
                    }
                    for(int sector : geoDistanceMap.keySet()) {
                        if(geoDistanceMap.get(sector).containsKey(ele)) {
                            geoDistanceMap.get(sector).put(ele, geoDistanceMap.get(sector).get(ele) * equalizerMap.get(ele));
                        }
                    }
                    if(dataDifferenceMap.containsKey(ele)) {
                        dataDifferenceMap.put(ele, dataDifferenceMap.get(ele) * equalizerMap.get(ele));
                    }
                }
            }
            for(int sector = 0; sector < maxSector; sector++) {
                if(vtoMap.containsKey(sector)) {
                    vtoList = vtoMap.get(sector);
                } else {
                    vtoList = new ArrayList<>();
                }
                vto = new VisualizationTableObject(0, (sector+1), splitted[0], splitted[1], null, vtoTimeEnum, vtoStartTime, 0, false);
                getMultipleValueTable(enviromentMap.get(sector), geoDataDifferenceMap.get(sector), vto.geoDataDifference);
                getMultipleValueTable(enviromentMap.get(sector), dataDifferenceMap, vto.dataDifference);
                getMultipleValueTable(enviromentMap.get(sector), geoDistanceMap.get(sector), vto.distanceBonus);
                if(visualizationTargetMap.containsKey(sector) && visualizationTargetMap.get(sector).containsKey(target)) {
                    vto.amount = Double.toString(visualizationTargetMap.get(sector).get(target));
                } else {
                    vto.amount = "0";
                }
                vtoList.add(vto);
                vtoMap.put(sector, vtoList);
            }
        }
        double adjacentBonusTemp;
        double adjacentSectorPower = adjacentSectorBonus.getReal();
        int[] affectedSectorArray = new int[8];
        for(int sector : vtoMap.keySet()) {
            affectedSectorArray[0] = sector+1;
            affectedSectorArray[1] = sector-1;
            affectedSectorArray[2] = sector-columns;
            affectedSectorArray[3] = sector-columns+1;
            affectedSectorArray[4] = sector-columns-1;
            affectedSectorArray[5] = sector+columns;
            affectedSectorArray[6] = sector+columns+1;
            affectedSectorArray[7] = sector+columns-1;
            for(int affectedSector : affectedSectorArray) {
                if(affectedSector > -1 && affectedSector < maxSector && vtoMap.get(sector).size() != 0) {
                    if(adjacentSectorBonusMap.containsKey(affectedSector)) {
                        adjacentBonusTemp = adjacentSectorBonusMap.get(affectedSector);
                    } else {
                        adjacentBonusTemp = 0;
                    }
                    for(VisualizationTableObject vtoObj : vtoMap.get(sector)) {
                        adjacentBonusTemp += adjacentSectorPower * vtoObj.getTotalBonus();
                    }
                    adjacentSectorBonusMap.put(affectedSector, adjacentBonusTemp);
                }
            }
        }

        double totalAmount;
        if(targetSector == -1) {
            Map<Integer, Double> totalBonusMap = new HashMap<>();
            for(int sector = 0; sector < maxSector; sector++) {
                totalAmount = 0;
                if(vtoMap.containsKey(sector)) {
                    for(VisualizationTableObject vtoObj : vtoMap.get(sector)) {
                        totalAmount += vtoObj.getTotalBonus();
                    }
                } else {
                    totalAmount = 0;
                }
                if(adjacentSectorBonusMap.containsKey(sector)) {
                    totalAmount += adjacentSectorBonusMap.get(sector);
                }
                totalBonusMap.put(sector, totalAmount);
            }
            List<Integer> sortedKeyset = sortByValue(totalBonusMap, desc);
            int grade = 1;
            for(Integer key : sortedKeyset) {
                if(grade < limit+1) {
                    returnList.add(new VisualizationTableObject(grade, (key+1), null, null, null, vtoTimeEnum, vtoStartTime, totalBonusMap.get(key), false));
                    grade++;
                } else {
                    break;
                }
            }
        } else {
            Map<String, Double> totalBonusMap = new HashMap<>();
            for(VisualizationTableObject vtoObj : vtoMap.get(targetSector)) {
                for(String key : vtoObj.geoDataDifference.keySet()) {
                    totalBonusMap.put("GEO_DIFFERENCE"+Main.SPLIT_SYMBOL+key, vtoObj.geoDataDifference.get(key));
                }
                for(String key : vtoObj.dataDifference.keySet()) {
                    totalBonusMap.put("DIFFERENCE"+Main.SPLIT_SYMBOL+key, vtoObj.dataDifference.get(key));
                }
                for(String key : vtoObj.distanceBonus.keySet()) {
                    totalBonusMap.put("DISTANCE"+Main.SPLIT_SYMBOL+key, vtoObj.distanceBonus.get(key));
                }
            }
            totalBonusMap.put("ADJACENT_BONUS", adjacentSectorBonusMap.get(targetSector));
            List<String> sortedKeyset = sortByValue(totalBonusMap, desc);
            int grade = 1;
            String bonusTarget;
            for(String key : sortedKeyset) {
                if(grade < limit+1) {
                    if(key.equals("ADJACENT_BONUS")) {
                        returnList.add(new VisualizationTableObject(grade, targetSector+1, null, "전체", "인접지역 보너스", vtoTimeEnum, vtoStartTime, totalBonusMap.get(key), false));
                    } else {
                        splitted = key.split(Main.SPLIT_SYMBOL);
                        if(splitted[0].equals("DISTANCE")) {
                            bonusTarget = "시각화 대상간 거리";
                        } else if(splitted[0].equals("DIFFERENCE")) {
                            bonusTarget = "시각화 대상간 발생 시간차";
                        } else {
                            bonusTarget = "시각화 대상 및 지역간 발생 시간차";
                        }
                        returnList.add(new VisualizationTableObject(grade, targetSector+1, null, splitted[1], bonusTarget, vtoTimeEnum, vtoStartTime, totalBonusMap.get(key), false));
                    }
                    grade++;
                } else {
                    break;
                }
            }
        }
        return returnList;
    }

    private String getTargetString(){
        boolean isSelectedSameElement = false;
        StringBuilder targetString = new StringBuilder();
        String[] splitted;
        String[] splitted2;
        for(String target : selectedVisualizationTarget){
            splitted = target.split(Main.SPLIT_SYMBOL);
            targetString.append("'");
            targetString.append(target.replace(Main.SPLIT_SYMBOL+"NULL", "").replace(Main.SPLIT_SYMBOL, "::"));
            targetString.append("', ");
            if( ! isSelectedSameElement) {
                for(String enviromentTarget : selectedEnviromentTarget) {
                    splitted2 = enviromentTarget.split(Main.SPLIT_SYMBOL);
                    if((splitted[1].equals("NULL") && splitted[0].equals(splitted2[0]))||
                            (splitted2[0].equals(splitted[0]) && splitted2[1].equals(splitted[1]))) {
                        isSelectedSameElement = true;
                        break;
                    }
                }
            }
        }
        return isSelectedSameElement + "§" + targetString.toString();
    }

    private Map<String, Map<String, Double>> lineChartAbsoluteCalculate(TimeEnum selectedTime, String startTargetTime, String endTargetTime, int selectedSector, boolean desc, Label infoLabel){
        Map<String, Map<String, Double>> returnMap = new HashMap<>();

        Map<String, Map<String, Double>> enviromentMap = Absolute.absoluteDB.getAbsoluteAmountOfEnviroment(selectedTime, selectedEnviromentTarget, startTargetTime, endTargetTime, selectedSector, desc);
        Map<String, Map<String, Double>> visualizationTargetMap = Absolute.absoluteDB.getAbsoluteAmountOfVisualizationTarget(selectedTime, selectedVisualizationTarget, startTargetTime, endTargetTime, selectedSector, desc);
        returnMap.putAll(enviromentMap);
        returnMap.putAll(visualizationTargetMap);

        int min = Integer.parseInt(startTargetTime);
        int max = Integer.parseInt(endTargetTime);
        String tempTimeString;
        for(String elementKey : returnMap.keySet()){
            for(int count = min; count <= max; count++) {
                if(selectedTime != TimeEnum.WEEK && count < 10) {
                    tempTimeString = "0"+count;
                } else {
                    tempTimeString = Integer.toString(count);
                }
                if(!returnMap.get(elementKey).containsKey(tempTimeString)) {
                    returnMap.get(elementKey).put(tempTimeString, 0d);
                }
            }
        }
        if(vt == VisualizationType.LINE_CHART) {
            StringBuilder infoLore = new StringBuilder();
            if(selectedSector == -1) {
                if(selectedTime == TimeEnum.YEAR) {
                    infoLore.append("전체지역 대상 연도별 발생횟수");
                } else if(selectedTime == TimeEnum.MONTH) {
                    infoLore.append("전체지역 대상 월별 발생횟수");
                } else if(selectedTime == TimeEnum.WEEK) {
                    infoLore.append("전체지역 대상 요일별 발생횟수");
                } else if(selectedTime == TimeEnum.HOUR) {
                    infoLore.append("전체지역 대상 시간별 발생횟수");
                } else if(selectedTime == TimeEnum.ALL) {
                    infoLore.append("전체지역 대상 통합 발생횟수");
                }
            } else {
                infoLore.append((selectedSector+1) + "번 섹터 대상 ");
                if(selectedTime == TimeEnum.YEAR) {
                    infoLore.append("연도별 발생횟수");
                } else if(selectedTime == TimeEnum.MONTH) {
                    infoLore.append("월별 발생횟수");
                } else if(selectedTime == TimeEnum.WEEK) {
                    infoLore.append("요일별 발생횟수");
                } else if(selectedTime == TimeEnum.HOUR) {
                    infoLore.append("시간별 발생횟수");
                } else if(selectedTime == TimeEnum.ALL) {
                    infoLore.append("통합 발생횟수");
                }
            }
            infoLabel.setText(infoLore.toString());
        }
        return returnMap;
    }

    private Map<String, Double> lineChartOccurrenceCalculate(TimeEnum selectedTime, String startTargetTime, String endTargetTime, int targetSector, int maxSector, int columns, boolean desc, Label infoLabel, boolean equalizer){
        Map<String, Double> returnMap = new HashMap<>();

        int min = Integer.parseInt(startTargetTime);
        int max = Integer.parseInt(endTargetTime);

        Map<Integer, Map<String, Double>> enviromentMap;
        Map<Integer, Map<String, Double>> visualizationTargetMap;
        Map<Integer, Map<String, Double>> geoDataDifferenceMap;
        Map<String, Double> dataDifferenceMap;
        Map<Integer, Map<String, Double>> geoDistanceMap;
        Map<Integer, Double> adjacentSectorBonusMap;
        Map<Integer, List<VisualizationTableObject>> vtoMap;
        List<VisualizationTableObject> vtoList;
        VisualizationTableObject vto;
        Map<String, Double> equalizerMap = new HashMap<>();
        if(equalizer) {
            equalizerMap = Absolute.absoluteDB.getEqualizer();
        }

        String[] splitted;
        double sectorTimeAvgPower = sectorTimeAvgBonus.getReal();
        double timeAvgPower = timeAvgBonus.getReal();
        double distanceAvgPower = distanceAvgBonus.getReal();
        double adjacentBonusTemp;
        double adjacentSectorPower;
        int[] affectedSectorArray;
        double totalAmount;
        TimeEnum vtoTimeEnum = selectedTime;
        String vtoStartTime;
        String time;

        for(int count = min; count <= max; count++) {
            if(selectedTime != TimeEnum.WEEK && count < 10) {
                time = "0"+count;
            } else {
                time = Integer.toString(count);
            }
            vtoStartTime = time;
            if(selectedTime==TimeEnum.WEEK) {
                vtoStartTime = getIntegerToWeek(Integer.parseInt(time));
            }
            if(vtoTimeEnum == TimeEnum.ALL) {
                vtoTimeEnum = TimeEnum.YEAR;
            }
            enviromentMap = Absolute.absoluteDB.getAbsoluteAmountOfEnviroment(selectedTime, time, time, selectedEnviromentTarget);
            visualizationTargetMap = Absolute.absoluteDB.getAbsoluteAmountOfVisualizationTarget(selectedTime, time, time, selectedVisualizationTarget);
            adjacentSectorBonusMap = new HashMap<>();
            vtoMap = new HashMap<>();
            for(String target : selectedVisualizationTarget) {
                splitted = target.split(Main.SPLIT_SYMBOL);
                geoDataDifferenceMap = Absolute.absoluteDB.getGeoAbsoluteDifference(selectedTime, sectorTimeAvgPower, splitted[0], splitted[1], selectedEnviromentTarget);
                dataDifferenceMap = Absolute.absoluteDB.getAbsoluteDifference(selectedTime, timeAvgPower, splitted[0], splitted[1], selectedEnviromentTarget);
                geoDistanceMap = Absolute.absoluteDB.getGeoDistance(selectedTime, time, distanceAvgPower, splitted[0], splitted[1], selectedEnviromentTarget);
                if(equalizer && ! equalizerMap.isEmpty()) {
                    for(String ele : equalizerMap.keySet()) {
                        for(int sector : geoDataDifferenceMap.keySet()) {
                            if(geoDataDifferenceMap.get(sector).containsKey(ele)) {
                                geoDataDifferenceMap.get(sector).put(ele, geoDataDifferenceMap.get(sector).get(ele) * equalizerMap.get(ele));
                            }
                        }
                        for(int sector : geoDistanceMap.keySet()) {
                            if(geoDistanceMap.get(sector).containsKey(ele)) {
                                geoDistanceMap.get(sector).put(ele, geoDistanceMap.get(sector).get(ele) * equalizerMap.get(ele));
                            }
                        }
                        if(dataDifferenceMap.containsKey(ele)) {
                            dataDifferenceMap.put(ele, dataDifferenceMap.get(ele) * equalizerMap.get(ele));
                        }
                    }
                }
                for(int sector = 0; sector < maxSector; sector++) {
                    if(vtoMap.containsKey(sector)) {
                        vtoList = vtoMap.get(sector);
                    } else {
                        vtoList = new ArrayList<>();
                    }
                    vto = new VisualizationTableObject(0, (sector+1), splitted[0], splitted[1], null, vtoTimeEnum, vtoStartTime, 0, false);
                    getMultipleValueTable(enviromentMap.get(sector), geoDataDifferenceMap.get(sector), vto.geoDataDifference);
                    getMultipleValueTable(enviromentMap.get(sector), dataDifferenceMap, vto.dataDifference);
                    getMultipleValueTable(enviromentMap.get(sector), geoDistanceMap.get(sector), vto.distanceBonus);
                    if(visualizationTargetMap.containsKey(sector) && visualizationTargetMap.get(sector).containsKey(target)) {
                        vto.amount = Double.toString(visualizationTargetMap.get(sector).get(target));
                    } else {
                        vto.amount = "0";
                    }
                    vtoList.add(vto);
                    vtoMap.put(sector, vtoList);
                }
            }
            adjacentSectorPower = adjacentSectorBonus.getReal();
            affectedSectorArray = new int[8];
            for(int sector : vtoMap.keySet()) {
                affectedSectorArray[0] = sector+1;
                affectedSectorArray[1] = sector-1;
                affectedSectorArray[2] = sector-columns;
                affectedSectorArray[3] = sector-columns+1;
                affectedSectorArray[4] = sector-columns-1;
                affectedSectorArray[5] = sector+columns;
                affectedSectorArray[6] = sector+columns+1;
                affectedSectorArray[7] = sector+columns-1;
                for(int affectedSector : affectedSectorArray) {
                    if(affectedSector > -1 && affectedSector < maxSector && vtoMap.get(sector).size() != 0) {
                        if(adjacentSectorBonusMap.containsKey(affectedSector)) {
                            adjacentBonusTemp = adjacentSectorBonusMap.get(affectedSector);
                        } else {
                            adjacentBonusTemp = 0;
                        }
                        for(VisualizationTableObject vtoObj : vtoMap.get(sector)) {
                            adjacentBonusTemp += adjacentSectorPower * vtoObj.getTotalBonus();
                        }
                        adjacentSectorBonusMap.put(affectedSector, adjacentBonusTemp);
                    }
                }
            }

            if(targetSector == -1) {
                totalAmount = 0;
                for(int sector = 0; sector < maxSector; sector++) {
                    if(vtoMap.containsKey(sector)) {
                        for(VisualizationTableObject vtoObj : vtoMap.get(sector)) {
                            totalAmount += vtoObj.getTotalBonus();
                        }
                    } else {
                        totalAmount = 0;
                    }
                    if(adjacentSectorBonusMap.containsKey(sector)) {
                        totalAmount += adjacentSectorBonusMap.get(sector);
                    }
                }
                returnMap.put(time, totalAmount);
            } else {
                totalAmount = 0;
                for(VisualizationTableObject vtoObj : vtoMap.get(targetSector)) {
                    totalAmount += vtoObj.getTotalBonus();
                }
                returnMap.put(time, totalAmount);
            }

            if(!returnMap.containsKey(time)) {
                returnMap.put(time, 0d);
            }
        }

        String targetString = getTargetString();
        boolean isSelectedSameElement = Boolean.parseBoolean(targetString.substring(0, targetString.indexOf("§")));
        targetString = targetString.substring(targetString.indexOf("§")+1);
        StringBuilder infoLore = new StringBuilder();
        if(targetSector == -1) {
            infoLore.append(targetString.substring(0, targetString.length()-2));
            if(selectedTime == TimeEnum.YEAR) {
                infoLore.append("의 전체지역 대상 연도별 발생 가능성");
            } else if(selectedTime == TimeEnum.MONTH) {
                infoLore.append("의 전체지역 대상 월별 발생 가능성");
            } else if(selectedTime == TimeEnum.WEEK) {
                infoLore.append("의 전체지역 대상 요일별 발생 가능성");
            } else if(selectedTime == TimeEnum.HOUR) {
                infoLore.append("의 전체지역 대상 시간별 발생 가능성");
            } else if(selectedTime == TimeEnum.ALL) {
                infoLore.append("의 전체지역 대상 통합 발생 가능성");
            }
        } else {
            infoLore.append((targetSector+1) + "번 섹터 속 ");
            infoLore.append(targetString.substring(0, targetString.length()-2));
            if(selectedTime == TimeEnum.YEAR) {
                infoLore.append("의 연도별 발생 가능성");
            } else if(selectedTime == TimeEnum.MONTH) {
                infoLore.append("의 월별 발생 가능성");
            } else if(selectedTime == TimeEnum.WEEK) {
                infoLore.append("의 요일별 발생 가능성");
            } else if(selectedTime == TimeEnum.HOUR) {
                infoLore.append("의 시간별 발생 가능성");
            } else if(selectedTime == TimeEnum.ALL) {
                infoLore.append("의 통합 발생 가능성");
            }
        }
        String[] splitted2;
        for(String target : selectedVisualizationTarget){
            splitted = target.split(Main.SPLIT_SYMBOL);
            if( ! isSelectedSameElement) {
                for(String enviromentTarget : selectedEnviromentTarget) {
                    splitted2 = enviromentTarget.split(Main.SPLIT_SYMBOL);
                    if((splitted[1].equals("NULL") && splitted[0].equals(splitted2[0]))||
                            (splitted2[0].equals(splitted[0]) && splitted2[1].equals(splitted[1]))) {
                        isSelectedSameElement = true;
                        break;
                    }
                }
            }
        }
        if(isSelectedSameElement) {
            infoLore.append("\n(시각화 대상과 동일한 카테고리 속 환경변수를 선택하여 정확도가 떨어졌을 가능성이 있습니다.)");
        }
        infoLabel.setText(infoLore.toString());
        return returnMap;
    }


    private List sortByValue(final Map map, boolean desc) {
        List<String> list = new ArrayList();
        list.addAll(map.keySet());
        Collections.sort(list,new Comparator() {
            public int compare(Object o1,Object o2) {
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);
                return ((Comparable) v2).compareTo(v1);
            }
        });
        if(!desc) {
            Collections.reverse(list);
        }
        return list;
    }

    private void getMultipleValueTable(Map<String, Double> amount, Map<String, Double> multiple, Map<String, Double> targetMap){
        if(amount != null && multiple != null) {
            double multipled;
            for(String key : amount.keySet()) {
                if(multiple.containsKey(key)) {
                    multipled = (amount.get(key) * multiple.get(key));
                    targetMap.put(key.replace(Main.SPLIT_SYMBOL+"NULL", "").replace(Main.SPLIT_SYMBOL, "::"), multipled);
                }
            }
        }
    }


    private void drawMapAbsoluteResult(int rows, int sector, double latiSize, double longiSize, double startLongitude, double endLatitude, long minAmount, double layer, Map<String, Double> enviromentMap, Map<String, Double> visualizationTargetMap){
        int rowJump = sector/rows;
        int columnJump = sector - rowJump*rows;
        double sectorStartLati = endLatitude - (latiSize * (rowJump+1));
        double sectorEndLati = endLatitude - (latiSize * (rowJump));
        double sectorStartLongi = startLongitude + (longiSize * (columnJump));
        double sectorEndLongi = startLongitude + (longiSize * (columnJump+1));
        double totalValue = 0;
        double amount;
        StringBuilder lore = new StringBuilder();
        lore.append("&nbsp;<br>");
        lore.append("[시각화 대상]<br>");
        if(visualizationTargetMap != null && !visualizationTargetMap.isEmpty()) {
            for(String key : visualizationTargetMap.keySet()){
                amount = visualizationTargetMap.get(key);
                totalValue += amount;
                lore.append("&nbsp;&nbsp;"+key.replace(Main.SPLIT_SYMBOL+"NULL", "").replace(Main.SPLIT_SYMBOL,"::") + " : " + amount+"<br>");
            }
        }
        lore.append("&nbsp;<br>[환경 변수]<br>");
        if(enviromentMap != null && !enviromentMap.isEmpty()) {
            for(String key : enviromentMap.keySet()){
                amount = enviromentMap.get(key);
                totalValue += amount;
                lore.append("&nbsp;&nbsp;"+key.replace(Main.SPLIT_SYMBOL, "::") + " : " + amount+"<br>");
            }
        }
        int level = (int)((totalValue - minAmount) / layer);
        String color;
        if(level == 1) {
            color = "'#FFBFBF'";
        } else if(level == 2) {
            color = "'#FF7F7F'";
        } else if(level == 3) {
            color = "'#FF3F3F'";
        } else if(level == 4) {
            color = "'#FF0000'";
        } else if(level == 5) {
            color = "'#BF0000'";
        } else if(level == 6) {
            color = "'#7F0000'";
        } else if(level >= 7) {
            color = "'#3F0000'";
        } else {
            color = "'#FFFFFF'";
        }
        lore.append("<br>&nbsp;&nbsp;총합 : " + totalValue+"<br>");
        lore.append("&nbsp;&nbsp;레벨 : " + level + "<br>");
        lore.append("&nbsp;");
        map.getEngine().executeScript("drawPolygon(" + sectorStartLati + ", " + sectorStartLongi + ", " + sectorEndLati + ", " + sectorEndLongi + ", "+color+", " + (sector+1) + ", " + totalValue + ",\""+lore.toString()+"\")");
    }

    private void drawMapOccurrenceResult(int rows, int sector, double latiSize, double longiSize, double startLongitude, double endLatitude, long minAmount, double layer, double totalValue, String targetLore, String enviromentLore, String adjacentLore){
        int rowJump = sector/rows;
        int columnJump = sector - rowJump*rows;
        double sectorStartLati = endLatitude - (latiSize * (rowJump+1));
        double sectorEndLati = endLatitude - (latiSize * (rowJump));
        double sectorStartLongi = startLongitude + (longiSize * (columnJump));
        double sectorEndLongi = startLongitude + (longiSize * (columnJump+1));
        StringBuilder lore = new StringBuilder();
        lore.append("&nbsp;<br>");
        if(targetLore != null && targetLore.length() > 1) {
            lore.append("[시각화 대상 절대 건수]<br>" + targetLore + "&nbsp;<br>&nbsp;<br>");
        }
        if(enviromentLore != null && enviromentLore.length() > 1) {
            lore.append("[환경 보너스]<br>"+enviromentLore + "&nbsp;<br>&nbsp;<br>");
        }
        if(adjacentLore != null && adjacentLore.length() > 1) {
            lore.append("[인접지역 보너스]<br>"+adjacentLore);
        }
        int level = (int)((totalValue - minAmount) / layer);
        String color;
        if(level == 1) {
            color = "'#FFBFBF'";
        } else if(level == 2) {
            color = "'#FF7F7F'";
        } else if(level == 3) {
            color = "'#FF3F3F'";
        } else if(level == 4) {
            color = "'#FF0000'";
        } else if(level == 5) {
            color = "'#BF0000'";
        } else if(level == 6) {
            color = "'#7F0000'";
        } else if(level >= 7) {
            color = "'#3F0000'";
        } else {
            color = "'#FFFFFF'";
        }
        lore.append("<br><br>&nbsp;&nbsp;총합 : " + String.format("%.2f", totalValue) +"<br>");
        lore.append("&nbsp;&nbsp;레벨 : " + level + "<br>");
        lore.append("&nbsp;");
        try{
            map.getEngine().executeScript("drawPolygon(" + sectorStartLati + ", " + sectorStartLongi + ", " + sectorEndLati + ", " + sectorEndLongi + ", "+color+", " + (sector+1) + ", " + String.format("%.2f", totalValue) + ",\""+lore.toString()+"\")");
        } catch(JSException e){
            e.printStackTrace();
            System.out.println(lore.toString());
        }
    }

    private void drawTable(boolean isAbsoluteCalc, int selectedSector, List<VisualizationTableObject> result){
        table.getColumns().clear();
        table.getItems().clear();
        if(isAbsoluteCalc) {
            TableColumn<VisualizationTableObject,String> numColumn = new TableColumn("순위");
            TableColumn<VisualizationTableObject,String> sectorColumn = new TableColumn("섹터");
            TableColumn<VisualizationTableObject,String> categoryColumn = new TableColumn("카테고리");
            TableColumn<VisualizationTableObject,String> elementColumn = new TableColumn("엘리먼트");
            TableColumn<VisualizationTableObject,String> timeColumn = new TableColumn("시간");
            TableColumn<VisualizationTableObject,String> amountColumn = new TableColumn("절대건수");
            numColumn.setStyle("-fx-alignment: CENTER;");
            sectorColumn.setStyle("-fx-alignment: CENTER;");
            categoryColumn.setStyle("-fx-alignment: CENTER;");
            elementColumn.setStyle("-fx-alignment: CENTER;");
            timeColumn.setStyle("-fx-alignment: CENTER;");
            amountColumn.setStyle("-fx-alignment: CENTER;");
            numColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().num));
            sectorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().sector));
            categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().category));
            elementColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getElement()));
            timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
            amountColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().amount));
            table.getColumns().addAll(numColumn, sectorColumn, categoryColumn, elementColumn, timeColumn, amountColumn);
        } else {
            if(selectedSector == -1) {
                TableColumn<VisualizationTableObject,String> numColumn = new TableColumn("순위");
                TableColumn<VisualizationTableObject,String> sectorColumn = new TableColumn("섹터");
                TableColumn<VisualizationTableObject,String> timeColumn = new TableColumn("시간");
                TableColumn<VisualizationTableObject,String> amountColumn = new TableColumn("발생 가능성");
                numColumn.setStyle("-fx-alignment: CENTER;");
                sectorColumn.setStyle("-fx-alignment: CENTER;");
                timeColumn.setStyle("-fx-alignment: CENTER;");
                amountColumn.setStyle("-fx-alignment: CENTER;");
                numColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().num));
                sectorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().sector));
                timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
                amountColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().amount));
                table.getColumns().addAll(numColumn, sectorColumn, timeColumn, amountColumn);
            } else {
                TableColumn<VisualizationTableObject,String> numColumn = new TableColumn("순위");
                TableColumn<VisualizationTableObject,String> sectorColumn = new TableColumn("섹터");
                TableColumn<VisualizationTableObject,String> categoryColumn = new TableColumn("대상");
                TableColumn<VisualizationTableObject,String> elementColumn = new TableColumn("보너스");
                TableColumn<VisualizationTableObject,String> timeColumn = new TableColumn("시간");
                TableColumn<VisualizationTableObject,String> amountColumn = new TableColumn("발생 가능성");
                numColumn.setStyle("-fx-alignment: CENTER;");
                sectorColumn.setStyle("-fx-alignment: CENTER;");
                categoryColumn.setStyle("-fx-alignment: CENTER;");
                elementColumn.setStyle("-fx-alignment: CENTER;");
                timeColumn.setStyle("-fx-alignment: CENTER;");
                amountColumn.setStyle("-fx-alignment: CENTER;");
                numColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().num));
                sectorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().sector));
                categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().element));
                elementColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().subQuery));
                timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
                amountColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().amount));
                table.getColumns().addAll(numColumn, sectorColumn, categoryColumn, elementColumn, timeColumn, amountColumn);
            }
        }
        table.getItems().addAll(result);
    }

    private void drawPieChart(boolean isAbsoluteCalc, int selectedSector, List<VisualizationTableObject> result){
        pieChart.setData(FXCollections.observableArrayList());
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        double all = 0;
        for(VisualizationTableObject vto : result) {
            all += Double.parseDouble(vto.amount);
        }
        double percent;
        for(VisualizationTableObject vto : result) {
            percent = Double.parseDouble(vto.amount) / all * 100;
            pieChartData.add(new PieChart.Data(String.format("%.2f", percent)+"％%", Double.parseDouble(vto.amount)));
        }
        pieChart.setData(pieChartData);
        String[] pieColor = {"rgb(255,161,78);","rgb(255,204,155);","rgb(255,226,193);","rgb(255,248,232);","rgb(177,177,177);"};
        int grade = 0;
        for (final PieChart.Data data : pieChart.getData()) {
            VisualizationTableObject vto = result.get(grade);
            String percentage = String.format("%.2f", (Double.parseDouble(vto.amount) / all * 100));
            if(grade < 5) {
                data.getNode().setStyle("-fx-background-color: "+pieColor[grade]+";");
            }
            grade ++;
            if(isAbsoluteCalc) {
                data.getNode().setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
                    @Override
                    public void handle(javafx.scene.input.MouseEvent event) {
                        pieChartLabel.setText("["+vto.sector+"번 섹터] ["+vto.getTime()+"] ["+vto.getCategoryElement()+"]\n" + vto.amount + "건" + "("+ percentage +"％)");
                    }
                });
            } else {
                if(selectedSector == -1) {
                    data.getNode().setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
                        @Override
                        public void handle(javafx.scene.input.MouseEvent event) {
                            pieChartLabel.setText("["+vto.sector+"번 섹터] ["+vto.getTime()+"]\n발생 가능성 : " + vto.amount + "("+ percentage +"％)");
                        }
                    });
                } else {
                    data.getNode().setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
                        @Override
                        public void handle(javafx.scene.input.MouseEvent event) {
                            pieChartLabel.setText("["+vto.sector+"번 섹터] ["+vto.getTime()+"] ["+vto.element+"("+ vto.subQuery +")]\n발생 가능성 : " + vto.amount + "("+ percentage +"％)");
                        }
                    });
                }
            }


        }
    }

    private void drawLineChart(TimeEnum te, boolean isAbsoluteCalc, int selectedSector, Map<String, Map<String, Double>> result){
        lineChart.setData(FXCollections.observableArrayList());
        ObservableList<XYChart.Series<String, Number>> list = FXCollections.observableArrayList();
        XYChart.Series series;
        List<Integer> timeSort;
        String timeString;
        for (String key : result.keySet()) {
            series = new XYChart.Series();
            series.setName(key);
            timeSort = new ArrayList<>();
            for(String time : result.get(key).keySet()) {
                timeSort.add(Integer.parseInt(time));
            }
            Collections.sort(timeSort);
            for(int time : timeSort) {
                if(te != TimeEnum.WEEK && time < 10) {
                    timeString = "0"+time;
                } else {
                    timeString = Integer.toString(time);
                }
                if(te == TimeEnum.WEEK) {
                    series.getData().add(new XYChart.Data(getIntegerToWeek(time)+"요일", result.get(key).get(timeString)));
                } else if(te == TimeEnum.MONTH){
                    series.getData().add(new XYChart.Data(time + "월", result.get(key).get(timeString)));
                } else if(te == TimeEnum.HOUR){
                    series.getData().add(new XYChart.Data(timeString + "시", result.get(key).get(timeString)));
                } else {
                    series.getData().add(new XYChart.Data(timeString + "년", result.get(key).get(timeString)));
                }
            }
            list.add(series);
        }

        lineChart.setData(list);
        lineChart.setCreateSymbols(false);
//        if (isAbsoluteCalc) {
//            for(XYChart.Series data : list) {
//                data.getNode().setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
//                    @Override
//                    public void handle(javafx.scene.input.MouseEvent event) {
//                        lineChartLabel.setText("[평균 상승률 : ]" + data.getData());
//                    }
//                });
//            }
//        } else {
//            if (selectedSector == -1) {
//                for(XYChart.Series data : list) {
//                    data.getNode().setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
//                        @Override
//                        public void handle(javafx.scene.input.MouseEvent event) {
//                            lineChartLabel.setText("[평균 상승률 : ]");
//                        }
//                    });
//                }
//            } else {
//                for(XYChart.Series data : list) {
//                    data.getNode().setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
//                        @Override
//                        public void handle(javafx.scene.input.MouseEvent event) {
//                            lineChartLabel.setText("[지역 내 평균 상승률 : ]");
//                        }
//                    });
//                }
//            }
//        }

    }

    private void drawLineChartOccurrence(TimeEnum te, boolean isAbsoluteCalc, int selectedSector, Map<String, Double> result){
        lineChart.setData(FXCollections.observableArrayList());
        ObservableList<XYChart.Series<String, Number>> list = FXCollections.observableArrayList();
        XYChart.Series series = new XYChart.Series();
        List<Integer> timeSort = new ArrayList<>();
        series.setName("발생확률");
        for(String time : result.keySet()) {
            timeSort.add(Integer.parseInt(time));
        }
        Collections.sort(timeSort);
        String timeString;
        for(int time : timeSort) {
            if(te != TimeEnum.WEEK && time < 10) {
                timeString = "0"+time;
            } else {
                timeString = Integer.toString(time);
            }
            if(te == TimeEnum.WEEK) {
                series.getData().add(new XYChart.Data(getIntegerToWeek(time)+"요일", result.get(timeString)));
            } else if(te == TimeEnum.MONTH){
                series.getData().add(new XYChart.Data(time + "월", result.get(timeString)));
            } else if(te == TimeEnum.HOUR){
                series.getData().add(new XYChart.Data(timeString + "시", result.get(timeString)));
            } else {
                series.getData().add(new XYChart.Data(timeString + "년", result.get(timeString)));
            }
        }
        list.add(series);
        lineChart.setData(list);
        lineChart.setCreateSymbols(false);
//        if (isAbsoluteCalc) {
//            for(XYChart.Series data : list) {
//                data.getNode().setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
//                    @Override
//                    public void handle(javafx.scene.input.MouseEvent event) {
//                        lineChartLabel.setText("[평균 상승률 : ]" + data.getData());
//                    }
//                });
//            }
//        } else {
//            if (selectedSector == -1) {
//                for(XYChart.Series data : list) {
//                    data.getNode().setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
//                        @Override
//                        public void handle(javafx.scene.input.MouseEvent event) {
//                            lineChartLabel.setText("[평균 상승률 : ]");
//                        }
//                    });
//                }
//            } else {
//                for(XYChart.Series data : list) {
//                    data.getNode().setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
//                        @Override
//                        public void handle(javafx.scene.input.MouseEvent event) {
//                            lineChartLabel.setText("[지역 내 평균 상승률 : ]");
//                        }
//                    });
//                }
//            }
//        }
    }

    private int getWeekToInteger(String week){
        if(week.equals("월")){
            return 1;
        } else if(week.equals("화")) {
            return 2;
        } else if(week.equals("수")) {
            return 3;
        } else if(week.equals("목")) {
            return 4;
        } else if(week.equals("금")) {
            return 5;
        } else if(week.equals("토")) {
            return 6;
        } else {
            return 0;
        }
    }

    private String getIntegerToWeek(int week){
        if(week == 1){
            return "월";
        } else if(week == 2) {
            return "화";
        } else if(week == 3) {
            return "수";
        } else if(week == 4) {
            return "목";
        } else if(week == 5) {
            return "금";
        } else if(week == 6) {
            return "토";
        } else {
            return "일";
        }
    }

}
