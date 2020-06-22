package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.controller;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.function.alarm.enums.AfterWorkType;
import goldbigdragon.github.io.function.alarm.enums.BeforeWorkType;
import goldbigdragon.github.io.enums.SoundType;
import goldbigdragon.github.io.function.BaseController;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import goldbigdragon.github.io.function.alarm.controller.ProgressController;
import goldbigdragon.github.io.function.alarm.view.ProgressView;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.enums.AddEventType;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.view.DataEditorView;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.AdditionalDataColumnObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.DataFluctuationObject;
import goldbigdragon.github.io.node.DateTimePicker;
import goldbigdragon.github.io.node.NumberField;
import goldbigdragon.github.io.node.RealField;
import goldbigdragon.github.io.util.DateUtil;
import goldbigdragon.github.io.util.FileUtil;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class DataCreatorController extends BaseController {

    @FXML private ScrollPane additionalDataBox;

    @FXML private NumberField minEventAmount;
    @FXML private NumberField maxEventAmount;
    @FXML private DateTimePicker minStartTime;
    @FXML private DateTimePicker maxStartTime;
    @FXML private DateTimePicker minEndTime;
    @FXML private DateTimePicker maxEndTime;
    @FXML private CheckBox shortTime;
    @FXML private NumberField minAmount;
    @FXML private NumberField maxAmount;

    @FXML private VBox eventAreaPanel;
    @FXML private RadioButton areaRadio;
    @FXML private RealField minStartLatitude;
    @FXML private RealField minStartLongitude;
    @FXML private RealField maxStartLatitude;
    @FXML private RealField maxStartLongitude;
    @FXML private RealField minEndLatitude;
    @FXML private RealField minEndLongitude;
    @FXML private RealField maxEndLatitude;
    @FXML private RealField maxEndLongitude;

    @FXML private VBox eventAddTargetPanel;
    @FXML private RadioButton enviroAddRadio;
    @FXML private MenuButton eventAddTargetEnviroment;
    @FXML private RealField minAddEventRange;
    @FXML private RealField maxAddEventRange;

    @FXML private VBox eventRemoveTargetPanel;
    @FXML private RadioButton enviroRemoveRadio;
    @FXML private MenuButton eventRemoveTargetEnviroment;
    @FXML private RealField minRemoveEventRange;
    @FXML private RealField maxRemoveEventRange;

    @FXML private TreeView treeView;
    @FXML private VBox activePane;

    @FXML private MenuButton elementSelectMenuButton;
    @FXML private Button addButton;
    @FXML private Button saveEditted;
    @FXML private Button deletePattern;

    private static String init = null;
    private static List<DataFluctuationObject> dfoList = null;
    private static int selectedPattern = -1;
    private AddEventType selectedEventType;


    public void clearAll(){
        init = null;
        dfoList = null;
        selectedPattern = -1;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            if(init == null) {
                dfoList = new ArrayList<>();
                treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                        TreeItem<String> parent = selectedItem.getParent();
                        if(parent != null) {
                            selectedPattern = parent.getChildren().indexOf(newValue);
                            showPatternEditPage();
                        } else {
                            showPatternAddPage();
                        }
                    }
                });
                clearTree();
            } else if(init == "ADD_PATTERN") {
                if(elementSelectMenuButton.getItems().size() < 1) {
                    Map<String, List<String>> model = MainController.modelDb.getModel();
                    MenuItem mi;
                    for(String key : model.keySet()) {
                        for(String element : model.get(key)) {
                            mi = new MenuItem(key + "::" + element);
                            mi.setOnAction(event -> selectElement(event));
                            elementSelectMenuButton.getItems().add(mi);
                        }
                    }
                }
                if(elementSelectMenuButton.getItems().size() > 1) {
                    elementSelectMenuButton.setText(elementSelectMenuButton.getItems().get(0).getText());
                    addButton.setDisable(false);
                }
            } else if(init == "EDIT_PATTERN") {
                DataFluctuationObject dfo = dfoList.get(selectedPattern);
                minEventAmount.setText(Integer.toString(dfo.getMinEventAmount()));
                maxEventAmount.setText(Integer.toString(dfo.getMaxEventAmount()));
                minAmount.setText(Integer.toString(dfo.getMinAmount()));
                maxAmount.setText(Integer.toString(dfo.getMaxAmount()));
                minStartLatitude.setText(Double.toString(dfo.getMinStartLatitude()));
                maxStartLatitude.setText(Double.toString(dfo.getMaxStartLatitude()));
                minStartLongitude.setText(Double.toString(dfo.getMinStartLongitude()));
                maxStartLongitude.setText(Double.toString(dfo.getMaxStartLongitude()));
                minEndLatitude.setText(Double.toString(dfo.getMinEndLatitude()));
                maxEndLatitude.setText(Double.toString(dfo.getMaxEndLatitude()));
                minEndLongitude.setText(Double.toString(dfo.getMinEndLongitude()));
                maxEndLongitude.setText(Double.toString(dfo.getMaxEndLongitude()));

                shortTime.setSelected(dfo.getShortTime());
                DateUtil du = new DateUtil();
                LocalDateTime minStartDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(du.stringToEpoch(dfo.getMinStartTime())), ZoneId.systemDefault());
                LocalDateTime maxStartDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(du.stringToEpoch(dfo.getMaxStartTime())), ZoneId.systemDefault());
                LocalDateTime minEndDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(du.stringToEpoch(dfo.getMinEndTime())), ZoneId.systemDefault());
                LocalDateTime maxEndDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(du.stringToEpoch(dfo.getMaxEndTime())), ZoneId.systemDefault());
                minStartTime.setDateTimeValue(minStartDateTime);
                maxStartTime.setDateTimeValue(maxStartDateTime);
                minEndTime.setDateTimeValue(minEndDateTime);
                maxEndTime.setDateTimeValue(maxEndDateTime);
                selectedEventType = dfo.getAddEventType();

                Map<String, List<String>> model = MainController.modelDb.getModel();
                MenuItem mi;
                MenuItem mi2;
                for(String key : model.keySet()) {
                    for(String element : model.get(key)) {
                        mi = new MenuItem(key + "::" + element);
                        mi.setOnAction(event -> selectEventAddElement(event));
                        mi2 = new MenuItem(key + "::" + element);
                        mi2.setOnAction(event -> selectEventRemoveElement(event));
                        eventAddTargetEnviroment.getItems().add(mi);
                        eventRemoveTargetEnviroment.getItems().add(mi2);
                    }
                }
                eventAddTargetEnviroment.setText(eventAddTargetEnviroment.getItems().get(0).getText());
                eventRemoveTargetEnviroment.setText(eventRemoveTargetEnviroment.getItems().get(0).getText());

                if(selectedEventType == AddEventType.ENVIROMENT_ADD) {
                    eventAddTargetEnviroment.setText(dfo.getTempString());
                    minAddEventRange.setText(Double.toString(dfo.getMinStartLatitude()));
                    maxAddEventRange.setText(Double.toString(dfo.getMinStartLongitude()));
                } else if(selectedEventType == AddEventType.ENVIROMENT_REMOVE){
                    eventRemoveTargetEnviroment.setText(dfo.getTempString());
                    minRemoveEventRange.setText(Double.toString(dfo.getMinStartLatitude()));
                    maxRemoveEventRange.setText(Double.toString(dfo.getMinStartLongitude()));
                }
                showEventPanel(selectedEventType==AddEventType.AREA, selectedEventType==AddEventType.ENVIROMENT_ADD, selectedEventType==AddEventType.ENVIROMENT_REMOVE);

                VBox rootBox = new VBox();
                rootBox.setStyle("-fx-background-color: white; -fx-fill-height: true;");
                rootBox.setPadding(new Insets(0, 0, 10, 0));
                VBox vbox;
                HBox minHbox;
                HBox maxHbox;
                Label transparentLabel;
                Label nameLabel;
                String[] splitted;
                String type;
                String name;
                for(String rawData : dfo.getAdditionalData()){
                    vbox = new VBox();
                    vbox.setPadding(new Insets(0, 0, 4, 2));
                    splitted = rawData.split(Main.SPLIT_SYMBOL);
                    type = splitted[0];
                    name = splitted[1];
                    transparentLabel = new Label(type);
                    transparentLabel.setVisible(false);
                    nameLabel = new Label(name);
                    vbox.getChildren().add(transparentLabel);
                    vbox.getChildren().add(nameLabel);
                    minHbox = new HBox();
                    minHbox.setPadding(new Insets(0, 4, 0, 2));
                    minHbox.setSpacing(10);
                    maxHbox = new HBox();
                    maxHbox.setPadding(new Insets(0, 4, 0, 2));
                    maxHbox.setSpacing(10);
                    if(type.equals("TEXT")) {
                        for(int count = 2; count < splitted.length; count++) {
                            minHbox = new HBox();
                            minHbox.setPadding(new Insets(0, 4, 0, 2));
                            minHbox.setSpacing(10);
                            maxHbox = new HBox();
                            TextField tf = new TextField();
                            if(splitted[count].equals("NULL"))
                                tf.setPromptText(splitted[count]);
                            else
                                tf.setText(splitted[count]);
                            tf.setPrefWidth(200);
                            Button addTextField = new Button("＋");
                            Button deleteTextField = new Button("－");
                            addTextField.setOnAction((e)->{addTextField(e);});
                            deleteTextField.setOnAction((e)->{removeTextField(e);});
                            minHbox.getChildren().add(tf);
                            minHbox.getChildren().add(addTextField);
                            minHbox.getChildren().add(deleteTextField);
                            vbox.getChildren().add(minHbox);
                        }
                        if(maxHbox.getChildren().size() > 1) {
                            vbox.getChildren().add(maxHbox);
                        }
                        rootBox.getChildren().add(vbox);
                        continue;
                    }else if(type.equals("INTEGER") || type.equals("LONG")) {
                        Label minLabel = new Label("최소 : ");
                        NumberField minNf = new NumberField();
                        minNf.setText(splitted[2]);
                        minNf.setPrefWidth(200);
                        Label maxLabel = new Label("최대 : ");
                        NumberField maxNf = new NumberField();
                        maxNf.setText(splitted[3]);
                        maxNf.setPrefWidth(200);
                        minHbox.getChildren().add(minLabel);
                        minHbox.getChildren().add(minNf);
                        maxHbox.getChildren().add(maxLabel);
                        maxHbox.getChildren().add(maxNf);
                    }else if(type.equals("REAL")) {
                        Label minLabel = new Label("최소 : ");
                        RealField minRf = new RealField();
                        minRf.setText(splitted[2]);
                        minRf.setPrefWidth(200);
                        Label maxLabel = new Label("최대 : ");
                        RealField maxRf = new RealField();
                        maxRf.setText(splitted[3]);
                        maxRf.setPrefWidth(200);
                        minHbox.getChildren().add(minLabel);
                        minHbox.getChildren().add(minRf);
                        maxHbox.getChildren().add(maxLabel);
                        maxHbox.getChildren().add(maxRf);
                    }else if(type.equals("BOOLEAN")) {
                        Label trueLabel = new Label("True 확률 : ");
                        Label percent = new Label("%");
                        RealField minRf = new RealField();
                        minRf.setText(splitted[2]);
                        minRf.setPrefWidth(100);
                        minHbox.getChildren().add(trueLabel);
                        minHbox.getChildren().add(minRf);
                        minHbox.getChildren().add(percent);
                    }
                    vbox.getChildren().add(minHbox);
                    if(maxHbox.getChildren().size() > 1) {
                        vbox.getChildren().add(maxHbox);
                    }
                    rootBox.getChildren().add(vbox);
                }
                additionalDataBox.setContent(rootBox);
            }
        });
    }

    @FXML private void showAreaEvent(){
        showEventPanel(true, false, false);
        selectedEventType = AddEventType.AREA;
    }

    @FXML private void showEnviromentAdd(){
        showEventPanel(false, true, false);
        selectedEventType = AddEventType.ENVIROMENT_ADD;
    }

    @FXML private void showEnviromentRemove(){
        showEventPanel(false, false, true);
        selectedEventType = AddEventType.ENVIROMENT_REMOVE;
    }

    private void showEventPanel(boolean isArea, boolean isEnviromentAdd, boolean isEnviromentRemove){
        areaRadio.setSelected(isArea);
        eventAreaPanel.setVisible(isArea);
        enviroAddRadio.setSelected(isEnviromentAdd);
        eventAddTargetPanel.setVisible(isEnviromentAdd);
        enviroRemoveRadio.setSelected(isEnviromentRemove);
        eventRemoveTargetPanel.setVisible(isEnviromentRemove);
    }

    @FXML private void addTextField(ActionEvent event){
        Button addButton = (Button)event.getSource();
        HBox addedHbox = new HBox();
        addedHbox.setPadding(new Insets(0, 4, 0, 2));
        addedHbox.setSpacing(10);
        TextField addedTextField = new TextField();
        addedTextField.setPromptText("NULL");
        addedTextField.setPrefWidth(200);
        Button addedAddTextField = new Button("＋");
        addedAddTextField.setOnAction((e)->{addTextField(e);});
        Button addedDeleteTextField = new Button("－");
        addedDeleteTextField.setOnAction((e)->{removeTextField(e);});
        addedHbox.getChildren().add(addedTextField);
        addedHbox.getChildren().add(addedAddTextField);
        addedHbox.getChildren().add(addedDeleteTextField);
        ((VBox) addButton.getParent().getParent()).getChildren().add(addedHbox);
    }

    @FXML private void removeTextField(ActionEvent event){
        Button removeButton = (Button)event.getSource();
        HBox parent = ((HBox) removeButton.getParent());
        VBox grandParent = ((VBox) removeButton.getParent().getParent());
        if(grandParent.getChildren().size() > 3) {
            grandParent.getChildren().remove(parent);
        } else {
            ((TextField) parent.getChildren().get(0)).setText(null);
        }
    }

    private void clearTree(){
        TreeItem root = new TreeItem<String>("생성 패턴");
        treeView.setRoot(null);
        treeView.setRoot(root);
    }

    @Override
    @FXML public void close(ActionEvent event){
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.DISABLE);
        stage.close();
        clearAll();
        new DataEditorView().view();
    }

    private void showPatternAddPage(){
        init = "ADD_PATTERN";
        loadFxml("criminalstatisticsanalysis/dataEditor/addPattern");
    }

    private void showPatternEditPage(){
        init = "EDIT_PATTERN";
        loadFxml("criminalstatisticsanalysis/dataEditor/editPattern");
    }

    @FXML private void selectElement(ActionEvent e) {
        String text = ((MenuItem) e.getSource()).getText();
        elementSelectMenuButton.setText(text);
    }

    @FXML private void selectEventAddElement(ActionEvent e) {
        String text = ((MenuItem) e.getSource()).getText();
        eventAddTargetEnviroment.setText(text);
    }

    @FXML private void selectEventRemoveElement(ActionEvent e) {
        String text = ((MenuItem) e.getSource()).getText();
        eventRemoveTargetEnviroment.setText(text);
    }

    @FXML public void addPattern(){
        String name = elementSelectMenuButton.getText();
        if(name != null && name.trim().length() > 0) {
            TreeItem tv = ((TreeView)(elementSelectMenuButton.getParent().getParent().getParent().getChildrenUnmodifiable().get(1))).getRoot();
            addTreeItem(tv, name);
            elementSelectMenuButton.requestFocus();
            String category = name.split("::")[0];
            String element = name.split("::")[1];

            List<AdditionalDataColumnObject> additionalDataColumnList = MainController.dataDb.getAdditionalDataColumns(category);
            List<String> additionalData = new ArrayList<>();
            StringBuilder sb;
            for(AdditionalDataColumnObject ado : additionalDataColumnList) {
                sb = new StringBuilder();
                sb.append(ado.type);
                sb.append(Main.SPLIT_SYMBOL);
                sb.append(ado.name);
                sb.append(Main.SPLIT_SYMBOL);
                if(ado.type.equals("TEXT")) {
                    sb.append("NULL");
                } else if(ado.type.equals("INTEGER") || ado.type.equals("LONG") || ado.type.equals("REAL")) {
                    sb.append("0");
                    sb.append(Main.SPLIT_SYMBOL);
                    sb.append("1000");
                } else if(ado.type.equals("BOOLEAN")) {
                    sb.append("50.0");
                }
                additionalData.add(sb.toString());
            }
            DataFluctuationObject dfo = new DataFluctuationObject(500, 1000, category, element, "2010-01-01 09:00:00", "2020-01-01 09:00:00", "2010-01-01 09:00:00", "2020-01-01 09:00:00", false, 1, 100, 124, 124, 33, 33, 132, 132,  42, 42, additionalData, AddEventType.AREA, null);
            dfoList.add(dfo);
        }
    }

    private TreeItem addTreeItem(TreeItem root, String name){
        TreeItem<String> item = new TreeItem<String>(name);
        if(root != null) {
            root.getChildren().add(item);
            root.setExpanded(true);
        }
        else
            ((TreeItem)(treeView.getRoot().getChildren().get(0))).getChildren().add(item);
        return item;
    }

    @FXML private void deletePattern(){
        dfoList.remove(selectedPattern);
        TreeItem tv = ((TreeView)(deletePattern.getParent().getParent().getParent().getChildrenUnmodifiable().get(1))).getRoot();
        tv.getChildren().remove(selectedPattern);
    }

    @FXML private void savePattern(){
        List<Node> nodeList = ((Pane)additionalDataBox.getContent()).getChildren();
        List<String> additionalData = dfoList.get(selectedPattern).getAdditionalData();
        List<String> newAdditionalData = new ArrayList<>();
        String type;
        String name;
        String[] splitted;
        List<Node> innerVbox;
        StringBuilder sb;
        for(int count = 0; count < nodeList.size(); count++) {
            sb = new StringBuilder();
            splitted = additionalData.get(count).split(Main.SPLIT_SYMBOL);
            type = splitted[0];
            name = splitted[1];
            sb.append(type);
            sb.append(Main.SPLIT_SYMBOL);
            sb.append(name);
            sb.append(Main.SPLIT_SYMBOL);
            innerVbox = ((VBox)nodeList.get(count)).getChildren();
            if(name.equals(((Label)innerVbox.get(1)).getText()) && type.equals(((Label)innerVbox.get(0)).getText())) {
                if(type.equals("TEXT")) {
                    String text;
                    Node n;
                    for(int count2 = 2; count2 < innerVbox.size(); count2++) {
                        if(count2 > 2)
                            sb.append(Main.SPLIT_SYMBOL);
                        n = innerVbox.get(count2);
                        if(n.getClass().getSimpleName().equals("HBox")) {
                            text = ((TextField)((HBox)n).getChildren().get(0)).getText();
                            if(text == null || text.length() < 1 || text.equals("null")) {
                                sb.append("NULL");
                            } else {
                                sb.append(text);
                            }
                        }
                    }
                }else if(type.equals("INTEGER") || type.equals("LONG")) {
                    long min = ((NumberField)((HBox)innerVbox.get(2)).getChildren().get(1)).getNumber();
                    long max = ((NumberField)((HBox)innerVbox.get(3)).getChildren().get(1)).getNumber();
                    if(min > max) {
                        long temp = max;
                        max = min;
                        min = temp;
                    }
                    sb.append(min);
                    sb.append(Main.SPLIT_SYMBOL);
                    sb.append(max);
                }else if(type.equals("REAL")) {
                    double min = ((RealField)((HBox)innerVbox.get(2)).getChildren().get(1)).getReal();
                    double max = ((RealField)((HBox)innerVbox.get(3)).getChildren().get(1)).getReal();
                    if(min > max) {
                        double temp = max;
                        max = min;
                        min = temp;
                    }
                    sb.append(min);
                    sb.append(Main.SPLIT_SYMBOL);
                    sb.append(max);
                }else if(type.equals("BOOLEAN")) {
                    double percent = ((RealField)((HBox)innerVbox.get(2)).getChildren().get(1)).getReal();
                    sb.append(percent);
                }
                newAdditionalData.add(sb.toString());
            }
        }
        boolean isShortTime = shortTime.isSelected();
        String main = dfoList.get(selectedPattern).getMain();
        String sub = dfoList.get(selectedPattern).getSub();

        int tempInt;
        int minEA =  minEventAmount.getNumber();
        int maxEA =  maxEventAmount.getNumber();
        if(minEA > maxEA) {
            tempInt = maxEA;
            maxEA = minEA;
            minEA = tempInt;
        }
        int minA =  minAmount.getNumber();
        int maxA =  maxAmount.getNumber();
        if(minA > maxA) {
            tempInt = maxA;
            maxA = minA;
            minA = tempInt;
        }
        DateUtil du = new DateUtil();
        String minStart = du.epochToString(minStartTime.getEpoch());
        String maxStart = du.epochToString(maxStartTime.getEpoch());
        String minEnd = du.epochToString(minEndTime.getEpoch());
        String maxEnd = du.epochToString(maxEndTime.getEpoch());

        double minStartLa = minStartLatitude.getReal();
        double maxStartLa = maxStartLatitude.getReal();
        double minEndLa = minEndLatitude.getReal();
        double maxEndLa = maxEndLatitude.getReal();

        double minStartLo = minStartLongitude.getReal();
        double maxStartLo = maxStartLongitude.getReal();
        double minEndLo = minEndLongitude.getReal();
        double maxEndLo = maxEndLongitude.getReal();
        String tempString = null;

        if(selectedEventType == AddEventType.ENVIROMENT_ADD) {
            tempString = eventAddTargetEnviroment.getText();
            minStartLa = minAddEventRange.getReal();
            minStartLo = maxAddEventRange.getReal();
        } else if(selectedEventType == AddEventType.ENVIROMENT_REMOVE) {
            tempString = eventRemoveTargetEnviroment.getText();
            minStartLa = minRemoveEventRange.getReal();
            minStartLo = maxRemoveEventRange.getReal();
        }

        DataFluctuationObject newDataFluctuationObject = new DataFluctuationObject(minEA, maxEA, main, sub, minStart, maxStart, minEnd, maxEnd, isShortTime, minA, maxA, minStartLa, maxStartLa, minStartLo, maxStartLo, minEndLa, maxEndLa,  minEndLo, maxEndLo, newAdditionalData, selectedEventType, tempString);
        dfoList.set(selectedPattern, newDataFluctuationObject);
        saveEditted.setText("저장 완료!");
    }

    private void loadFxml(String file) {
        activePane.getChildren().clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/" + file + ".fxml"));
            activePane.getChildren().setAll(root);
        } catch(IOException e) {}
    }


    @FXML private void loadFile(){
        SoundUtil.playSound(SoundType.ENABLE);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("임의 데이터 생성 패턴 불러오기 (다중선택 가능)");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("임의 데이터 생성 패턴 파일", "*.dfo"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(((GridPane) treeView.getParent().getParent().getParent()).getScene().getWindow());
        if (selectedFiles != null && ! selectedFiles.isEmpty()) {
            FileUtil fu = new FileUtil();
            DataFluctuationObject dfo;

            Map<String,List<String>> additionalDataNames = MainController.modelDb.getModel();
            List<String> additionalDataNameList;
            Map<String,List<String>> models = MainController.modelDb.getModel();
            List<AdditionalDataColumnObject> adcoList;
            String dataName;
            for(String category : models.keySet()) {
                adcoList = MainController.dataDb.getAdditionalDataColumns(category);
                additionalDataNameList = new ArrayList<>();
                for(AdditionalDataColumnObject adco : adcoList){
                    dataName = adco.getType().getValue()+Main.SPLIT_SYMBOL+adco.name;
                    additionalDataNameList.add(dataName);
                }
                additionalDataNames.put(category, additionalDataNameList);
            }

            List<String> fixedAdditionalData;
            String main;
            String[] splitted;
            List<String> existNameList = new ArrayList<>();
            String type;
            for(File f : selectedFiles) {
                try {
                    List<String> data = fu.readFile(f, "UTF8");
                    for(String string : data) {
                        dfo = new DataFluctuationObject(string);
                        main = dfo.getMain();
                        fixedAdditionalData = new ArrayList<>();
                        existNameList.clear();
                        existNameList.addAll(additionalDataNames.get(main));
                        for(String additionalData : dfo.getAdditionalData()) {
                            splitted = additionalData.split(Main.SPLIT_SYMBOL);
                            if(additionalDataNames.get(main).contains(splitted[0]+Main.SPLIT_SYMBOL+splitted[1])){
                                fixedAdditionalData.add(additionalData);
                                existNameList.remove(splitted[0]+Main.SPLIT_SYMBOL+splitted[1]);
                            }
                        }
                        for(String notCludedTypeAndName : existNameList) {
                            splitted = notCludedTypeAndName.split(Main.SPLIT_SYMBOL);
                            type = splitted[0];
                            if(type.equals("INTEGER") || type.equals("LONG") || type.equals("REAL")) {
                                fixedAdditionalData.add(notCludedTypeAndName + Main.SPLIT_SYMBOL + "0");
                            }
                            else if(type.equals("TEXT")) {
                                fixedAdditionalData.add(notCludedTypeAndName + Main.SPLIT_SYMBOL + "NULL");
                            }
                            else if(type.equals("BOOLEAN")) {
                                fixedAdditionalData.add(notCludedTypeAndName + Main.SPLIT_SYMBOL + "50");
                            }
                        }
                        dfo.setAdditionalData(fixedAdditionalData);
                        dfoList.add(dfo);
                        TreeItem tv = treeView.getRoot();
                        addTreeItem(tv, main+"::"+dfo.getSub());
                    }
                } catch(IOException e) {

                }
            }
        }
    }

    @FXML private void saveFile(){
        if(!dfoList.isEmpty()) {
            SoundUtil.playSound(SoundType.ENABLE);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("임의 데이터 생성 패턴 저장");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("임의 데이터 생성 패턴 파일", "*.dfo");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(((GridPane) treeView.getParent().getParent().getParent()).getScene().getWindow());
            if (file != null) {
                FileUtil fu = new FileUtil();
                List<String> data = new ArrayList<>();
                for(DataFluctuationObject dfo : dfoList) {
                    data.add(dfo.toString());
                }
                try {
                    fu.writeFile(file, data, "UTF8");
                } catch (IOException e){
                    new AlarmAPI().fileExportError();
                }
            }
        } else {
            new AlarmAPI().listEmptyError();
        }
    }

    @FXML private void createData(){
        File file = new File(Main.dbDirectory + "/data/instance.dfo");
        file.deleteOnExit();
        FileUtil fu = new FileUtil();
        List<String> data = new ArrayList<>();
        for(DataFluctuationObject dfo : dfoList) {
            data.add(dfo.toString());
        }
        try {
            fu.writeFile(file, data, "UTF8");
        } catch (IOException e){
        }
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.DISABLE);
        stage.close();
        List<BeforeWorkType> beforeWork = new ArrayList<>();
        beforeWork.add(BeforeWorkType.CREATE_FLUCTUATED_DATA);
        List<AfterWorkType> afterWork = new ArrayList<>();
        afterWork.add(AfterWorkType.OPEN_CRIMINALSTATISTICSANALYSIS_DATA_EDITOR);
        ProgressController.param = Main.dbDirectory + "/data/instance.dfo";
        new ProgressView().view("임의 데이터를 생성 중입니다..!", beforeWork, afterWork);
        clearAll();
    }
}