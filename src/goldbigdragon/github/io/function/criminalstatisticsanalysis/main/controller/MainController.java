package goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.*;
import goldbigdragon.github.io.function.BaseController;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import goldbigdragon.github.io.function.alarm.controller.ProgressController;
import goldbigdragon.github.io.function.alarm.enums.AfterWorkType;
import goldbigdragon.github.io.function.alarm.enums.BeforeWorkType;
import goldbigdragon.github.io.function.alarm.view.ProgressView;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.Absolute;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.AbsoluteDataAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.DataAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.view.DataEditorView;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.ModelAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.view.ModelEditorView;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.kakaomap.web.KakaoMapServer;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.threads.DataRangeCalculateThread;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.AdditionalDataColumnObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.view.VisualizationMainView;
import goldbigdragon.github.io.object.SubQueryObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.object.VisualizationObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.query.*;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.view.VisualizationTargetView;
import goldbigdragon.github.io.node.NumberField;
import goldbigdragon.github.io.util.DbHandler;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSException;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;

public class MainController extends BaseController {

    private static boolean checkTiming = false;

    public static KakaoMapServer mapServer = null;

    public static ModelAPI modelDb = null;
    public static DataAPI dataDb = null;

    public static List<VisualizationObject> visualizationTarget;

    public static List<String> point;
    public static Map<String, List<String>> model;
    public static TimeEnum selectedTime = TimeEnum.YEAR;
    public List<Integer> years;
    public static List<Double> selectedLatiLongti;
    public static boolean added = false;

    @FXML MenuButton sortTypeMenuButton;
    @FXML TreeView treeView;

    @FXML Button calculateButton;
    @FXML Button visualizationTargetEditButton;

    @FXML WebView map;

    @FXML PieChart publicPieChart;

    @FXML TableView publicTable;

    @FXML NumberField rowNmberField;
    @FXML NumberField columnNmberField;
    @FXML Label widthLabel;
    @FXML Label heightLabel;
    @FXML Label areaLabel;
    @FXML Label targetAmountLabel;
    @FXML Label arrayCount;

    private List<String> selectedElement = new ArrayList<>();

    private void clearAll(){
        if(modelDb != null)
            modelDb.close();
        if(dataDb != null)
            dataDb.close();
        modelDb = null;
        dataDb = null;
        point = null;
        model = null;
        if(mapServer != null)
            mapServer.close();
        mapServer = null;
        visualizationTarget = null;
        selectedLatiLongti = null;
    }

//                              Initialize
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        basicInitialize();
        setVisualizationTargetInitialize();
        calculatableCheck();
        targetAmountLabel.setText(Integer.toString(visualizationTarget.size()));
    }
    private void basicInitialize() {
        calculateButton.setDisable(true);
        if(modelDb != null) {
            point = modelDb.getPoint();
            model = modelDb.getModel();
        } else {
            new AlarmAPI().noModelFound();
        }
        if(dataDb != null) {
            this.years = dataDb.getTimes(TimeEnum.YEAR);
        }
        sortYear();
        if(mapServer != null)
            mapServer.close();
        mapServer = null;
        mapServer = new KakaoMapServer(1120);
        loadMap();
    }
    private void setVisualizationTargetInitialize(){
        List<VisualizationObject> newVisualizationTarget = new ArrayList<>();
        if(!visualizationTarget.isEmpty()) {
            String category;
            String element;
            String[] splitted;

            List<AdditionalDataColumnObject> adcoList;
            List<SubQueryObject> newSubQueryList;
            List<String> adcoStringList;
            for(VisualizationObject vo: visualizationTarget) {
                category = null;
                if(vo.type.code.equals("P")) {
                    if(point.contains(vo.name)) {
                        newVisualizationTarget.add(vo);
                    }
                } else {
                    if(vo.type.code.equals("E") && vo.name.indexOf(Main.SPLIT_SYMBOL) != -1) {
                        splitted = vo.name.split(Main.SPLIT_SYMBOL);
                        if(splitted.length > 1) {
                            category = splitted[0];
                            element = splitted[1];
                            if(! model.containsKey(category) || ! model.get(category).contains(element)) {
                                continue;
                            }
                        }
                    } else if(vo.type.code.equals("C")){
                        category = vo.name;
                    }
                    if(category != null && model.containsKey(category)) {
                        if(!vo.subQueryList.isEmpty()) {
                            adcoList = dataDb.getAdditionalDataColumns(category);
                            adcoStringList = new ArrayList<>();
                            newSubQueryList = new ArrayList<>();
                            for(AdditionalDataColumnObject adco : adcoList) {
                                adcoStringList.add(adco.type+adco.name);
                            }
                            for(SubQueryObject sqo : vo.subQueryList) {
                                if(adcoStringList.contains(sqo.type.name()+sqo.name)) {
                                    newSubQueryList.add(sqo);
                                }
                            }
                            vo.subQueryList = newSubQueryList;
                        }
                        newVisualizationTarget.add(vo);
                    }
                }
            }
            visualizationTarget = newVisualizationTarget;
        }
    }

    private void loadMap(){
        double minX = 0;
        double maxX = 0;
        double minY = 0;
        double maxY = 0;

        int row = 1;
        int column = 1;

        Platform.runLater(() -> {
            Map<String, String> param = new HashMap<>();
            param.put("minX", Double.toString(minX));
            param.put("maxX", Double.toString(maxX));
            param.put("minY", Double.toString(minY));
            param.put("maxY", Double.toString(maxY));
            param.put("row", Integer.toString(row));
            param.put("column", Integer.toString(column));
            // row*column 만큼의 직사각형 필드 중, 레벨이 0 이상인것만 색칠하기.

            StringBuilder sb = new StringBuilder("http://localhost:1120/map?");
            for(String key : param.keySet()) {
                sb.append(key);
                sb.append("=");
                sb.append(param.get(key));
                sb.append("&");
            }
            String url = sb.toString().substring(0, sb.length()-1);
            System.out.println(url);
            map.getEngine().setJavaScriptEnabled(true);
            map.getEngine().load(url);
        });
    }
    @FXML private void checkSound() {
        SoundUtil.playSound(SoundType.CHECK);
    }


//                              DataTree 정렬 선택
    @FXML private void sortAll(){
        sortTypeMenuButton.setText("전체 기간 통합");
        clearTree("전체 기간");
        addMainTree(null);
        selectedTime = TimeEnum.ALL;
    }
    @FXML private void sortYear(){
        sortTypeMenuButton.setText("1년 단위");
        clearTree("전체 년도");
        for(int year : years) {
            addTree(year+"년");
        }
        selectedTime = TimeEnum.YEAR;
    }
    @FXML private void sortMonth(){
        sortTypeMenuButton.setText("월별");
        clearTree("전체 달");
        for(int count = 1; count < 13; count++) {
            if(count < 10)
                addTree("0"+count+"월");
            else
                addTree(count+"월");
        }
        selectedTime = TimeEnum.MONTH;
    }
    @FXML private void sortWeek(){
        sortTypeMenuButton.setText("요일별");
        clearTree("전체 요일");
        addTree("일");
        addTree("월");
        addTree("화");
        addTree("수");
        addTree("목");
        addTree("금");
        addTree("토");
        selectedTime = TimeEnum.WEEK;
    }
    @FXML private void sortHour(){
        sortTypeMenuButton.setText("시간별");
        clearTree("전체 시간");
        for(int count = 0; count < 24; count++) {
            if(count < 10)
                addTree("0"+count+"시");
            else
                addTree(count+"시");
        }
        selectedTime = TimeEnum.HOUR;
    }
    private void clearTree(String rootName){
        calculateButton.setDisable(true);
        selectedElement.clear();
        CheckBoxTreeItem root = new CheckBoxTreeItem<String>(rootName);
        treeView.setRoot(null);
        treeView.setRoot(root);
        treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
        treeView.setShowRoot(true);
        calculatableCheck();
    }

    private void addEvent(CheckBoxTreeItem target) {
        target.selectedProperty().addListener((obs) -> {
            timingCheck();
            String path = getPath(target);
            if(target.isSelected()) {
                selectedElement.add(path);
            } else {
                selectedElement.remove(path);
            }
        });
    }

    public void timingCheck(){
        if( ! checkTiming) {
            checkTiming = true;
            treeView.setDisable(true);
            rowNmberField.setDisable(true);
            columnNmberField.setDisable(true);
            sortTypeMenuButton.setDisable(true);
            calculateButton.setDisable(true);
            visualizationTargetEditButton.setDisable(true);
            Thread t = new Thread(()->{
                try {
                    Thread.sleep(500);
                    calculatableCheck();
                } catch(InterruptedException e) {

                }
                Thread.interrupted();
            });
            t.start();
        }
    }

    private void calculatableCheck(){
        if( ! selectedElement.isEmpty()) {
            try {
                added = false;
                selectedLatiLongti = new ArrayList<>();
                selectedLatiLongti.add(Double.MAX_VALUE);
                selectedLatiLongti.add(Double.MAX_VALUE);
                selectedLatiLongti.add(Double.MIN_VALUE);
                selectedLatiLongti.add(Double.MIN_VALUE);
                DataRangeCalculateThread.queryList = new ArrayList<>();
                String[] splitted;
                if(selectedTime != TimeEnum.ALL) {
                    String rawTime;
                    String key;
                    Map<String, List<Integer>> elementWithTime = new HashMap<>();
                    List<Integer> tempList;
                    for(String e : selectedElement) {
                        splitted = e.split(Main.SPLIT_SYMBOL);
                        if(splitted.length > 3) {
                            if(selectedTime == TimeEnum.WEEK) {
                                if(splitted[1].equals("월")) {
                                    rawTime = "1";
                                } else if(splitted[1].equals("화")) {
                                    rawTime = "2";
                                } else if(splitted[1].equals("수")) {
                                    rawTime = "3";
                                } else if(splitted[1].equals("목")) {
                                    rawTime = "4";
                                } else if(splitted[1].equals("금")) {
                                    rawTime = "5";
                                } else if(splitted[1].equals("토")) {
                                    rawTime = "6";
                                } else {
                                    rawTime = "0";
                                }
                            } else {
                                rawTime = splitted[1].substring(0,splitted[1].length()-1);
                            }
                            key = splitted[2]+Main.SPLIT_SYMBOL+splitted[3];
                            if(elementWithTime.containsKey(key)) {
                                tempList = elementWithTime.get(key);
                            } else {
                                tempList = new ArrayList<>();
                            }
                            if(!tempList.contains(rawTime)) {
                                tempList.add(Integer.parseInt(rawTime));
                            }
                            elementWithTime.put(key, tempList);
                        }
                    }
                    int prevTime;
                    int startTime;
                    for(String element : elementWithTime.keySet()) {
                        tempList = elementWithTime.get(element);
                        Collections.sort(tempList);
                        prevTime = tempList.get(0);
                        startTime = prevTime;
                        if(tempList.size() == 1) {
                            DataRangeCalculateThread.queryList.add(element+Main.SPLIT_SYMBOL+prevTime+Main.SPLIT_SYMBOL+prevTime);
                        } else {
                            for(int time : tempList) {
                                if(time - prevTime < 2) {
                                    prevTime = time;
                                } else {
                                    DataRangeCalculateThread.queryList.add(element+Main.SPLIT_SYMBOL+startTime+Main.SPLIT_SYMBOL+prevTime);
                                    prevTime = time;
                                    startTime = time;
                                }
                            }
                            if( ! DataRangeCalculateThread.queryList.contains(element+Main.SPLIT_SYMBOL+startTime+Main.SPLIT_SYMBOL+prevTime)) {
                                DataRangeCalculateThread.queryList.add(element+Main.SPLIT_SYMBOL+startTime+Main.SPLIT_SYMBOL+prevTime);
                            }
                        }
                    }
                } else {
                    for (String e : selectedElement) {
                        splitted = e.split(Main.SPLIT_SYMBOL);
                        if (splitted.length > 2) {
                            DataRangeCalculateThread.queryList.add(splitted[1]+Main.SPLIT_SYMBOL+splitted[2]);
                        }
                    }
                }
                List<DataRangeCalculateThread> threads = new ArrayList<>();
                for(int count = 0; count < Main.maxThreads; count++) {
                    threads.add(new DataRangeCalculateThread(selectedTime, count));
                }
                for(DataRangeCalculateThread dct : threads) {
                    dct.start();
                }
                try{
                    for(DataRangeCalculateThread dct : threads) {
                        dct.join();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                DataRangeCalculateThread.queryList = null;
                if(added) {
                    markDataRange();
                } else {
                    removeAllOverlays();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            removeAllOverlays();
        }
        if (visualizationTarget.isEmpty()) {
            calculateButton.setDisable(true);
            calculateButton.setStyle("-fx-background-image: url(\"/resources/img/common/target_unlock_target.png\");");
        } else {
            calculateButton.setDisable(false);
            calculateButton.setStyle("-fx-background-image: url(\"/resources/img/common/target_lock.png\");");
        }
        treeView.setDisable(false);
        rowNmberField.setDisable(false);
        columnNmberField.setDisable(false);
        sortTypeMenuButton.setDisable(false);
        calculateButton.setDisable(false);
        visualizationTargetEditButton.setDisable(false);
        checkTiming = false;
    }

//                              DataTree 생성, 클릭 함수
    private void addTree(String name){
        CheckBoxTreeItem item = new CheckBoxTreeItem<String>(name);
        item.setExpanded(false);
        treeView.getRoot().getChildren().add(item);
        addMainTree(item);
    }
    private void addMainTree(CheckBoxTreeItem root){
        CheckBoxTreeItem cbti;
        for(String main : model.keySet()) {
            cbti = addTreeItem(root, main);
            if( ! model.get(main).isEmpty())
                addSubTree(cbti, main);
        }
    }
    private void addSubTree(CheckBoxTreeItem root, String main){
        for(String sub : model.get(main)) {
            addTreeItem(root, sub);
        }
    }
    private CheckBoxTreeItem addTreeItem(CheckBoxTreeItem root, String name){
        CheckBoxTreeItem<String> item = new CheckBoxTreeItem<String>(name);
        addEvent(item);
        if(root == null)
            treeView.getRoot().getChildren().add(item);
        else
            root.getChildren().add(item);
        return item;
    }
    private String getPath(CheckBoxTreeItem target){
        StringBuilder sb = new StringBuilder();
        sb.insert(0, target.getValue());
        TreeItem temp = target.getParent();
        while(temp != null) {
            sb.insert(0, temp.getValue() + Main.SPLIT_SYMBOL);
            temp = temp.getParent();
        }
        return sb.toString();
    }



//                              GUI 닫고 이동
    @FXML private void shutdown(){
        new goldbigdragon.github.io.function.menu.main.MainView().view();
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.DISABLE);
        stage.close();
        clearAll();
    }
    @FXML private void dataEditorOpen(){
        new DataEditorView().view();
        closeMainPane();
    }

    @FXML private void modelEditorOpen(){
        new ModelEditorView().view();
        closeMainPane();
    }

    @FXML private void visualizationTargetOpen(){
        new VisualizationTargetView().view();
        closeMainPane();
    }

    private void closeMainPane(){
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.DISABLE);
        stage.close();
    }


//                              데이터 저장/불러오기
    @FXML private void save() {
        SoundUtil.playSound(SoundType.ENABLE);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("모델 및 데이터 저장");
        FileChooser.ExtensionFilter dbFilter = new FileChooser.ExtensionFilter("SQLite 데이터베이스 파일", "*.db");
        FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV 쉽표로 구분된 엑셀 파일", "*.csv");
        fileChooser.getExtensionFilters().addAll(dbFilter, csvFilter);
        File file = fileChooser.showSaveDialog(mainPane.getScene().getWindow());
        if (file != null) {
            FileInputStream modelFis = null;
            FileOutputStream modelFos = null;
            FileInputStream dataFis = null;
            FileOutputStream dataFos = null;
            try
            {
                File openedModelDb = new File(modelDb.getDatabaseName());
                File openedDataDb = new File(dataDb.getDatabaseName());
                String path = file.getAbsolutePath();
                String type = path.substring(path.lastIndexOf("."));
                path = path.substring(0, path.lastIndexOf(type));
                if(type.equals(".db")) {
                    File newModelFile = new File(path + ".model.db");
                    File newDataFile = new File(path + ".data.db");
                    if(!newModelFile.exists())
                        newModelFile.createNewFile();
                    if(!newDataFile.exists())
                        newDataFile.createNewFile();

                    modelFis = new FileInputStream(openedModelDb);
                    modelFos = new FileOutputStream(newModelFile) ;
                    byte[] b = new byte[4096];
                    int cnt = 0;
                    while((cnt=modelFis.read(b)) != -1){
                        modelFos.write(b, 0, cnt);
                    }
                    dataFis = new FileInputStream(openedDataDb);
                    dataFos = new FileOutputStream(newDataFile) ;
                    cnt = 0;
                    while((cnt=dataFis.read(b)) != -1){
                        dataFos.write(b, 0, cnt);
                    }
                    new AlarmAPI().fileExportSuccess();
                    modelFis.close();
                    modelFos.close();
                    dataFis.close();
                    dataFos.close();
                } else if(type.equals(".csv")) {
                    CsvCheckAPI ccapi = new CsvCheckAPI(path);
                    ccapi.createModelCsv();
                    ccapi.createDataCsv();
                    new AlarmAPI().fileExportSuccess();
                }
            }
            catch(IOException e1) {
                new AlarmAPI().fileExportError();
            }
        }
    }

    @FXML private void load() {
        SoundUtil.playSound(SoundType.ENABLE);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("모델 혹은 데이터 불러오기 (다중선택 가능)");
        FileChooser.ExtensionFilter dbFilter = new FileChooser.ExtensionFilter("SQLite 데이터베이스 파일", "*.db");
        FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV 쉽표로 구분된 엑셀 파일", "*.csv");
        fileChooser.getExtensionFilters().addAll(dbFilter, csvFilter);
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(mainPane.getScene().getWindow());

        if (selectedFiles != null && ! selectedFiles.isEmpty()) {
            visualizationTarget.clear();
            List<String> dataDbFileList = new ArrayList<>();
            List<String> modelDbFileList = new ArrayList<>();
            List<String> dataCsvFileList = new ArrayList<>();
            List<String> modelCsvFileList = new ArrayList<>();

            String path;
            DbHandler dbhandler = new DbHandler();
            CsvCheckAPI openedCsv;
            DatabaseType dt;

            for(File selectedFile : selectedFiles) {
                path = selectedFile.getAbsolutePath();
                String type = path.substring(path.lastIndexOf("."));
                if(type.equals(".db")) {
                    dbhandler.connectDb(path);
                    dt = dbhandler.getDbType();
                    if(dt == DatabaseType.CRIMINAL_STATISTICS_ANALYSIS_MODEL)
                        modelDbFileList.add(path);
                    else if(dt == DatabaseType.CRIMINAL_STATISTICS_ANALYSIS_DATA)
                        dataDbFileList.add(path);
                    dbhandler.close();
                }
                else if(type.equals(".csv")) {
                    openedCsv = new CsvCheckAPI(path);
                    dt = openedCsv.check();
                    if(dt == DatabaseType.CRIMINAL_STATISTICS_ANALYSIS_MODEL)
                        modelCsvFileList.add(path);
                    else if(dt == DatabaseType.CRIMINAL_STATISTICS_ANALYSIS_DATA)
                        dataCsvFileList.add(path);
                }
            }

            StringBuilder paramString = new StringBuilder();
            if(modelDbFileList.isEmpty() && modelCsvFileList.isEmpty()) {
                paramString.append("null");
            } else {
                for(int count = 0; count < modelDbFileList.size(); count++) {
                    if(count != 0)
                        paramString.append(Main.SPLIT_SYMBOL);
                    paramString.append(modelDbFileList.get(count));
                }
                for(int count = 0; count < modelCsvFileList.size(); count++) {
                    if(count != 0)
                        paramString.append(Main.SPLIT_SYMBOL);
                    paramString.append(modelCsvFileList.get(count));
                }
            }
            paramString.append("*");
            if(dataDbFileList.isEmpty() && dataCsvFileList.isEmpty()) {
                paramString.append("null");
            } else {
                for(int count = 0; count < dataDbFileList.size(); count++) {
                    if(count != 0)
                        paramString.append(Main.SPLIT_SYMBOL);
                    paramString.append(dataDbFileList.get(count));
                }
                for(int count = 0; count < dataCsvFileList.size(); count++) {
                    if(count != 0)
                        paramString.append(Main.SPLIT_SYMBOL);
                    paramString.append(dataCsvFileList.get(count));
                }
            }

            Stage stage = (Stage) mainPane.getScene().getWindow();
            SoundUtil.playSound(SoundType.DISABLE);
            stage.close();
            List<BeforeWorkType> beforeWork = new ArrayList<>();
            beforeWork.add(BeforeWorkType.LOAD_CRIMINALSTATISTICSANALYSIS_DATABASE);
            List<AfterWorkType> afterWork = new ArrayList<>();
            afterWork.add(AfterWorkType.OPEN_CRIMINALSTATISTICSANALYSIS_MAIN);
            ProgressController.param = paramString.toString();
            new ProgressView().view("DB 파일을 불러오는 중입니다..!", beforeWork, afterWork);
        }
    }

    @FXML private void rowAndColumnChange(){
        int row = rowNmberField.getNumber();
        int column = columnNmberField.getNumber();
        if(row < 1)
            rowNmberField.setText("1");
        else if(row > Integer.MAX_VALUE)
            rowNmberField.setText(Integer.toString(Integer.MAX_VALUE));
        if(column < 1)
            columnNmberField.setText("1");
        else if(column > Integer.MAX_VALUE)
            columnNmberField.setText(Integer.toString(Integer.MAX_VALUE));
        arrayCount.setText(Long.toString(rowNmberField.getNumber() * columnNmberField.getNumber()));
        if(!selectedElement.isEmpty()) {
            markDataRange();
        } else {
            areaLabel.setText("0㎡");
        }
    }

    @FXML private void openBigMap() {
        try {
            SoundUtil.playSound(SoundType.ENABLE);
            Desktop.getDesktop().browse(new URI("http://localhost:1120/bigmap"));
        } catch(Exception e) {
            new AlarmAPI().connectionError();
        }
    }

    @FXML private void openVisualizationFile(){
        SoundUtil.playSound(SoundType.ENABLE);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("시각화 DB 불러오기");
        FileChooser.ExtensionFilter dbFilter = new FileChooser.ExtensionFilter("SQLite 데이터베이스 파일", "*.db");
        fileChooser.getExtensionFilters().addAll(dbFilter);
        File selectedFiles = fileChooser.showOpenDialog(mainPane.getScene().getWindow());

        if (selectedFiles != null) {
            if(Absolute.absoluteDB != null) {
                Absolute.absoluteDB.close();
            }
            Absolute.absoluteDB = null;
            Absolute.absoluteDB = new AbsoluteDataAPI(selectedFiles.getPath());
            if(Absolute.absoluteDB.isVisualizationDb()) {
                openVisualizationGUI();
            } else {
                new AlarmAPI().notVisualizationDbFile();
            }
        }
    }

    @FXML private void calculate(){
        if(visualizationTarget.isEmpty()) {
            new AlarmAPI().emptyVisualizationTarget();
            return;
        }
        List<Double> lalo = dataDb.getStartEndLatiLongi();
        if(lalo.size() > 3) {
            SoundUtil.playSound(SoundType.ENABLE);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("시각화 데이터 추출");
            FileChooser.ExtensionFilter dbFilter = new FileChooser.ExtensionFilter("SQLite 데이터베이스 파일", "*.db");
            fileChooser.getExtensionFilters().addAll(dbFilter, dbFilter);
            File file = fileChooser.showSaveDialog(mainPane.getScene().getWindow());
            if (file != null) {
                FileInputStream dataFis;
                FileOutputStream dataFos;
                File newDataFile = null;
                try
                {
                    File openedDataDb = new File(dataDb.getDatabaseName());
                    String path = file.getAbsolutePath();
                    String type = path.substring(path.lastIndexOf("."));
                    path = path.substring(0, path.lastIndexOf(type));
                    newDataFile = new File(path + ".visualization.data.db");
                    if(!newDataFile.exists())
                        newDataFile.createNewFile();
                    byte[] b = new byte[4096];
                    int cnt = 0;
                    dataFis = new FileInputStream(openedDataDb);
                    dataFos = new FileOutputStream(newDataFile) ;
                    cnt = 0;
                    while((cnt=dataFis.read(b)) != -1){
                        dataFos.write(b, 0, cnt);
                    }
                    dataFis.close();
                    dataFos.close();
                }
                catch(IOException e1) {
                    new AlarmAPI().fileExportError();
                }

                StringBuilder param = new StringBuilder();
                param.append(newDataFile.getPath());
                param.append(Main.SPLIT_SYMBOL);
                param.append(rowNmberField.getNumber());
                param.append(Main.SPLIT_SYMBOL);
                param.append(columnNmberField.getNumber());
                param.append(Main.SPLIT_SYMBOL);
                param.append(lalo.get(0));
                param.append(Main.SPLIT_SYMBOL);
                param.append(lalo.get(1));
                param.append(Main.SPLIT_SYMBOL);
                param.append(lalo.get(2));
                param.append(Main.SPLIT_SYMBOL);
                param.append(lalo.get(3));

                Stage stage = (Stage) mainPane.getScene().getWindow();
                SoundUtil.playSound(SoundType.DISABLE);
                stage.close();

                List<BeforeWorkType> beforeWork = new ArrayList<>();
                beforeWork.add(BeforeWorkType.CALCULATE_CRIMINALSTATISTICSANALYSIS);
                List<AfterWorkType> afterWork = new ArrayList<>();
                ProgressController.param = param.toString();
                new ProgressView().view("시각화 DB 생성 중..!", beforeWork, afterWork);
            }
        } else {
            new AlarmAPI().emptyDatas();
        }
    }

    private void openVisualizationGUI(){
        new VisualizationMainView().view();
        closeMainPane();
    }

    private void markDataRange(){
        Platform.runLater(() -> {
            map.getEngine().executeScript("clear()");
            areaLabel.setText("0㎡");
            if(!selectedElement.isEmpty()) {
                map.getEngine().executeScript("drawPolygon(" + selectedLatiLongti.get(0) + ", " + selectedLatiLongti.get(1) + ", " + selectedLatiLongti.get(2) + ", " + selectedLatiLongti.get(3) + ", 4, '#000000', 1, 'solid', '#000000', 0.1)");
                map.getEngine().executeScript("createInfoWindow(" + selectedLatiLongti.get(2) + ", " + selectedLatiLongti.get(1) + ", '데이터 분포 범위')");
                map.getEngine().executeScript("drawLine(" + selectedLatiLongti.get(0) + ", " + selectedLatiLongti.get(1)+ ", " + selectedLatiLongti.get(2)+ ", " + selectedLatiLongti.get(3) + ", " + rowNmberField.getNumber() + ", " + columnNmberField.getNumber() + ")");
                double width = distanceByMeter(selectedLatiLongti.get(0), selectedLatiLongti.get(1), selectedLatiLongti.get(0), selectedLatiLongti.get(3));
                double height = distanceByMeter(selectedLatiLongti.get(0), selectedLatiLongti.get(1), selectedLatiLongti.get(2), selectedLatiLongti.get(1));
                double cellArea = areaByMeter(width, height, rowNmberField.getNumber(), columnNmberField.getNumber());
                width/=columnNmberField.getNumber();
                height/=rowNmberField.getNumber();
                widthLabel.setText(getDistanceValue(width));
                heightLabel.setText(getDistanceValue(height));
                areaLabel.setText(getDistanceValue(cellArea));
            }
        });
    }

    private String getDistanceValue(double value){
        if(value > 1000000) {
            value = value * 0.000001;
            return (long)value + "Tm";
        } else if(value > 1000) {
            value = value * 0.001;
            return (long)value + "㎞";
        } else {
            return (long)value + "m";
        }
    }

    private void removeAllOverlays(){
        Platform.runLater(() -> {
            try{
                map.getEngine().executeScript("clear()");
            } catch (JSException e){

            }
        });
    }

    private double distanceByMeter(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        double theta = startLongitude - endLongitude;
        double dist = Math.sin(deg2rad(startLatitude)) * Math.sin(deg2rad(endLatitude))
                + Math.cos(deg2rad(startLatitude)) * Math.cos(deg2rad(endLatitude)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.344;
        return (dist);
    }
    private double areaByMeter(double width, double height, int row, int column) {
        return (width / column) * (height / row);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
