package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.controller;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.function.alarm.enums.AfterWorkType;
import goldbigdragon.github.io.function.alarm.enums.BeforeWorkType;
import goldbigdragon.github.io.enums.SoundType;
import goldbigdragon.github.io.function.BaseController;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import goldbigdragon.github.io.function.alarm.view.ProgressView;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.view.DataCreatorView;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.AdditionalDataColumnObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.AdditionalDataTableObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.DataObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.DataTableObject;
import goldbigdragon.github.io.node.DateTimePicker;
import goldbigdragon.github.io.node.NumberField;
import goldbigdragon.github.io.node.RealField;
import goldbigdragon.github.io.util.DateUtil;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class DataEditorController extends BaseController {

    @FXML GridPane container;

    @FXML MenuButton categoryMenu;
    @FXML MenuButton elementMenu;
    @FXML MenuButton sortMenu;
    @FXML MenuButton descMenu;
    @FXML MenuButton createCategoryMenu;
    @FXML MenuButton createElementMenu;
    @FXML MenuButton typeMenu;
    @FXML MenuButton newPointDefaultBoolean;
    @FXML MenuButton newAdditionalDataCategory;

    @FXML Button createButton;
    @FXML Button createRandomDataButton;
    @FXML Button additionalDataEditButton;
    @FXML Button createAdditionalDataButton;

    @FXML DateTimePicker startDate;
    @FXML DateTimePicker endDate;

    @FXML TableView dataTable;
    @FXML TableColumn<DataTableObject, String> numTableColumn;
    @FXML TableColumn<DataTableObject, String> mainTableColumn;
    @FXML TableColumn<DataTableObject, String> subTableColumn;
    @FXML TableColumn<DataTableObject, String> timeTableColumn;
    @FXML TableColumn<DataTableObject, String> amountTableColumn;
    @FXML TableColumn<DataTableObject, String> latitudeTableColumn;
    @FXML TableColumn<DataTableObject, String> longitudeTableColumn;
    @FXML TableColumn<DataTableObject, String> deleteDataTableColumn;

    @FXML TableView additionalDataTable;
    @FXML TableColumn<AdditionalDataTableObject, String> additionalDataNameTableColumn;
    @FXML TableColumn<AdditionalDataTableObject, String> additionalDataTypeTableColumn;
    @FXML TableColumn<AdditionalDataTableObject, String> additionalDataValueTableColumn;

    @FXML TableView additionalDataListTable;
    @FXML TableColumn<AdditionalDataColumnObject, String> additionalDataListNameTableColumn;
    @FXML TableColumn<AdditionalDataColumnObject, String> additionalDataListTypeTableColumn;
    @FXML TableColumn<AdditionalDataColumnObject, String> additionalDataListValueTableColumn;
    @FXML TableColumn<AdditionalDataColumnObject, String> additionalDataListDeleteTableColumn;

    @FXML TextField additionalDataTextField;
    @FXML NumberField additionalDataNumberField;
    @FXML RealField additionalDataRealField;
    @FXML TextField newAdditionalDataName;
    @FXML TextField newPointDefaultText;
    @FXML NumberField newPointDefaultInteger;
    @FXML RealField newPointDefaultReal;

    @FXML Label maxPageLabel;
    @FXML Label progress;
    @FXML NumberField nowPageField;
    @FXML NumberField createAmount;
    @FXML RealField startLatitude;
    @FXML RealField startLongitude;
    @FXML RealField endLatitude;
    @FXML RealField endLongitude;

    @FXML Tab additionalDataTab;

    public int page = 0;
    public static int maxPage = 0;
    public Map<String, List<String>> model = new HashMap<>();

    private static String selectedData;
    private static String selectedAdditionalData;

    public static void clearAll(){
        selectedData = null;
        selectedAdditionalData = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        page = 0;
        maxPage = 0;
        dataTable.setPlaceholder(new Label("검색된 데이터가 없습니다."));
        additionalDataTable.setPlaceholder(new Label("추가정보가 없습니다."));
        additionalDataListTable.setPlaceholder(new Label("추가정보가 없습니다."));
        model = MainController.modelDb.getModel();

        MenuItem mi;
        for(String key : model.keySet()) {
            mi = new MenuItem();
            mi.setText(key);
            mi.setOnAction(event -> categorySelect(event));
            categoryMenu.getItems().add(mi);
        }
        for(String key : model.keySet()) {
            mi = new MenuItem();
            mi.setText(key);
            mi.setOnAction(event -> createCategorySelect(event));
            createCategoryMenu.getItems().add(mi);
        }

        for(String key : model.keySet()) {
            mi = new MenuItem();
            mi.setText(key);
            mi.setOnAction(event -> createAdditionalDataSelect(event));
            newAdditionalDataCategory.getItems().add(mi);
        }

        Platform.runLater(() -> {
            numTableColumn.setCellValueFactory(cellData -> cellData.getValue().getNum());
            mainTableColumn.setCellValueFactory(cellData -> cellData.getValue().getCategory());
            subTableColumn.setCellValueFactory(cellData -> cellData.getValue().getElement());
            timeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getTime());
            amountTableColumn.setCellValueFactory(cellData -> cellData.getValue().getAmount());
            latitudeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getLatitude());
            longitudeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getLongitude());
            deleteDataTableColumn.setCellFactory(tc -> new TableCell<DataTableObject, String>() {
                final Button btn = new Button();
                @Override public void updateItem(String value ,boolean empty){
                    btn.setId("deleteButton");
                    btn.setOnAction(event -> {
                        int index = getIndex();
                        ObservableList<DataTableObject> tableList = dataTable.getItems();
                        int num = Integer.parseInt(tableList.get(index).getNum().getValue());
                        String category = tableList.get(index).getCategory().getValue();
                        MainController.dataDb.deleteData(num, category);
                        dataTable.getItems().remove(index);
                    });
                    super.updateItem(value, empty);
                    if (empty) {
                        setGraphic(null);
                    }
                    else {
                        setGraphic(btn);
                    }
                    setText(null);
                }
            });


            additionalDataNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
            additionalDataTypeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getType());
            additionalDataValueTableColumn.setCellValueFactory(cellData -> cellData.getValue().getValue());

            additionalDataListNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
            additionalDataListTypeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getType());
            additionalDataListValueTableColumn.setCellValueFactory(cellData -> cellData.getValue().getDefaultValue());

            additionalDataListDeleteTableColumn.setCellFactory(tc -> new TableCell<AdditionalDataColumnObject, String>() {
                final Button btn = new Button();
                @Override public void updateItem(String value ,boolean empty){
                    btn.setId("deleteButton");
                    btn.setOnAction(event -> {
                        int index = getIndex();
                        ObservableList<AdditionalDataColumnObject> tableList = additionalDataListTable.getItems();
                        String name = tableList.get(index).getName().getValue();
                        deleteAdditionalData(name);
                        additionalDataListTable.getItems().remove(index);
                    });
                    super.updateItem(value, empty);
                    if (empty) {
                        setGraphic(null);
                    }
                    else {
                        setGraphic(btn);
                    }
                    setText(null);
                }
            });

            dataTable.setRowFactory(tv -> {
                TableRow<DataTableObject> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (! row.isEmpty()) {
                        selectedAdditionalData = null;
                        additionalDataTable.getItems().clear();
                        additionalDataEditButton.setDisable(true);
                        additionalDataTextField.clear();
                        additionalDataNumberField.clear();
                        additionalDataRealField.clear();
                        additionalDataTextField.setDisable(true);
                        additionalDataRealField.setDisable(true);
                        additionalDataNumberField.setDisable(true);
                        additionalDataTextField.setVisible(true);
                        additionalDataNumberField.setVisible(false);
                        additionalDataRealField.setVisible(false);
                        DataTableObject data = row.getItem();
                        selectedData = data.getNum().getValue() + Main.SPLIT_SYMBOL + data.getCategory().getValue() + Main.SPLIT_SYMBOL + data.getElement().getValue();
                        additionalDataTab.setDisable(false);
                        additionalDataTab.getTabPane().getSelectionModel().select(2);
                        List<AdditionalDataTableObject> additionalDataList = MainController.dataDb.getAdditionalData(Integer.parseInt(data.getNum().getValue()), data.getCategory().getValue());

                        for(AdditionalDataTableObject additionalData : additionalDataList) {
                            additionalDataTable.getItems().add(additionalData);
                        }
                    }
                });
                return row ;
            });
            additionalDataTable.setRowFactory(tv -> {
                TableRow<AdditionalDataTableObject> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (! row.isEmpty()) {
                        AdditionalDataTableObject data = row.getItem();
                        selectedAdditionalData = data.getName().getValue() + Main.SPLIT_SYMBOL + data.getType().getValue();
                        String type = data.getType().getValue();
                        boolean text = false;
                        boolean number = false;
                        boolean real = false;
                        if(type.equals("정수")) {
                            number = true;
                        }
                        else if(type.equals("실수")) {
                            real = true;
                        }
                        else {
                            text = true;
                        }
                        additionalDataTextField.setVisible(text);
                        additionalDataNumberField.setVisible(number);
                        additionalDataRealField.setVisible(real);
                        if(type.equals("정수")) {
                            additionalDataNumberField.setText(data.getValue().getValue());
                            additionalDataNumberField.setDisable(false);
                            additionalDataNumberField.requestFocus();
                        }
                        else if(type.equals("실수")) {
                            additionalDataRealField.setText(data.getValue().getValue());
                            additionalDataRealField.setDisable(false);
                            additionalDataRealField.requestFocus();
                        }
                        else {
                            additionalDataTextField.setText(data.getValue().getValue());
                            additionalDataTextField.setDisable(false);
                            additionalDataTextField.requestFocus();
                        }

                        additionalDataEditButton.setDisable(false);
                    }
                });
                return row ;
            });
            startDate.setValue(LocalDate.now());
            endDate.setValue(LocalDate.now());
            if(createCategoryMenu.getItems().size() > 0) {
                String selected = createCategoryMenu.getItems().get(0).getText();
                createCategoryMenu.setText(selected);
                MenuItem mi2;
                for(String key : model.get(selected)) {
                    mi2 = new MenuItem();
                    mi2.setText(key);
                    mi2.setOnAction(event -> createElementSelect(event));
                    createElementMenu.getItems().add(mi2);
                }
                if(createElementMenu.getItems().size() > 0) {
                    createElementMenu.setText(createElementMenu.getItems().get(0).getText());
                    createButton.setText("등록");
                    createButton.setDisable(false);
                    createRandomDataButton.setDisable(false);
                }
            }

            if(newAdditionalDataCategory.getItems().size() > 0) {
                String selected = newAdditionalDataCategory.getItems().get(0).getText();
                newAdditionalDataCategory.setText(selected);
                createAdditionalDataButton.setText("등록");
                createAdditionalDataButton.setDisable(false);
                additionalDataSearch();
            }
        });
    }

    @Override
    @FXML public void close(ActionEvent event){
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.DISABLE);
        stage.close();
        List<BeforeWorkType> beforeWork = new ArrayList<>();
        beforeWork.add(BeforeWorkType.OVERWRITE_CRIMINALSTATISTICSANALYSIS_DATA);
        List<AfterWorkType> afterWork = new ArrayList<>();
        afterWork.add(AfterWorkType.OPEN_CRIMINALSTATISTICSANALYSIS_MAIN);
        new ProgressView().view("데이터를 저장 중입니다..!", beforeWork, afterWork);
        clearAll();
    }

    @FXML private void search(){
        page = 0;
        maxPage = 0;
        querySearch(true);
    }

    @FXML private void prevPage(ActionEvent event){
        pageMove(page - 1);
    }

    @FXML private void nextPage(ActionEvent event){
        pageMove(page + 1);
    }

    @FXML private void pageMove(int page) {
        if(page >= 0 && page+1 <= maxPage) {
            nowPageField.setText(Integer.toString(page+1));
            this.page = page;
            querySearch(false);
        }
    }

    @FXML private void jumpPageKeyPress(KeyEvent event){
        if(event.getCode().getName().equalsIgnoreCase("ENTER")) {
            int typedPage = nowPageField.getNumber()-1;
            if(typedPage < 1)
                typedPage = 0;
            else if(typedPage > maxPage)
                typedPage = maxPage-1;
            pageMove(typedPage);
        }
    }

    public void querySearch(boolean pageInitialize) {
        createButton.setText("등록");
        dataTable.getItems().clear();
        setProgressing(true);
        Thread thread = new Thread() {
            @Override
            public void run() {
                maxPage = MainController.dataDb.getMaxPage(categoryMenu.getText(), elementMenu.getText());
                List<DataTableObject> datas = MainController.dataDb.getDatas(categoryMenu.getText(), elementMenu.getText(), sortMenu.getText(), descMenu.getText(), 20, page);
                for(DataTableObject data : datas) {
                    dataTable.getItems().add(data);
                }
                if(datas.isEmpty()) {
                    maxPage = 0;
                    Platform.runLater(() -> {
                        nowPageField.textProperty().setValue("0");
                        maxPageLabel.textProperty().setValue("0");
                    });
                    setProgressing(false);
                    return;
                } else {
                    if(pageInitialize) {
                        Platform.runLater(() -> {
                            nowPageField.textProperty().setValue("1");
                        });
                    } else {
                        Platform.runLater(() -> {
                            nowPageField.textProperty().setValue(Integer.toString(page+1));
                        });
                    }
                }
                Platform.runLater(() -> {
                    maxPageLabel.textProperty().setValue(Integer.toString(maxPage));
                });
                setProgressing(false);
            }
        };
        Platform.runLater(() -> {
            thread.setDaemon(true);
            thread.start();
        });
    }

    private void setProgressing(boolean progressing) {
        progress.setVisible(progressing);
        container.setDisable(progressing);
    }

    @FXML private void categorySelect(ActionEvent e) {
        MenuItem selectedMenuItem = (MenuItem) e.getSource();
        String selectedCategory = selectedMenuItem.getText();
        categoryMenu.setText(selectedMenuItem.getText());

        elementMenu.setText("모든 엘리먼트");
        elementMenu.getItems().clear();
        MenuItem allElements = new MenuItem();
        allElements.setText("모든 엘리먼트");
        allElements.setOnAction(event -> elementSelect(event));
        elementMenu.getItems().add(allElements);
        MenuItem mi;
        if( ! selectedCategory.equals("모든 카테고리")) {
            for(String key : model.get(selectedCategory)) {
                mi = new MenuItem();
                mi.setText(key);
                mi.setOnAction(event -> elementSelect(event));
                elementMenu.getItems().add(mi);
            }
        }
    }

    @FXML private void elementSelect(ActionEvent e) {
        MenuItem mi = (MenuItem) e.getSource();
        elementMenu.setText(mi.getText());
    }

    @FXML private void sortSelect(ActionEvent event) {
        MenuItem mi = (MenuItem) event.getSource();
        sortMenu.setText(mi.getText());
    }

    @FXML private void descSelect(ActionEvent event) {
        MenuItem mi = (MenuItem) event.getSource();
        descMenu.setText(mi.getText());
    }

    @FXML private void createCategorySelect(ActionEvent e) {
        createButton.setText("등록");
        MenuItem selectedMenuItem = (MenuItem) e.getSource();
        String selectedCategory = selectedMenuItem.getText();
        createCategoryMenu.setText(selectedMenuItem.getText());

        createElementMenu.getItems().clear();
        MenuItem mi;
        for(String key : model.get(selectedCategory)) {
            mi = new MenuItem();
            mi.setText(key);
            mi.setOnAction(event -> createElementSelect(event));
            createElementMenu.getItems().add(mi);
        }
        if(createElementMenu.getItems().size() > 0) {
            createElementMenu.setText(createElementMenu.getItems().get(0).getText());
            createButton.setDisable(false);
            createRandomDataButton.setDisable(false);
            createButton.setText("등록");
        } else {
            createElementMenu.setText(" - ");
            createButton.setDisable(true);
            createButton.setText("모델 수정 필요");
        }
    }

    @FXML private void createElementSelect(ActionEvent e) {
        createButton.setText("등록");
        MenuItem mi = (MenuItem) e.getSource();
        createElementMenu.setText(mi.getText());
    }

    @FXML private void createData() {
        String category = createCategoryMenu.getText();
        String element = createElementMenu.getText();
        if(model.containsKey(category) && model.get(category).contains(element)) {
            long startEpoch = startDate.getEpoch();
            long endEpoch = endDate.getEpoch();
            DateUtil du = new DateUtil();
            String startTime = du.epochToString(startEpoch);
            String endTime = du.epochToString(endEpoch);
            int amount = createAmount.getNumber();
            double startLati = startLatitude.getReal();
            double startLongi = startLongitude.getReal();
            double endLati = endLatitude.getReal();
            double endLongi = endLongitude.getReal();
            List<String> additionalData = new ArrayList<>();
            DataObject dataObject = new DataObject(category, element, startTime, endTime, amount, startLati, startLongi, endLati, endLongi, additionalData);
            if(MainController.dataDb.insertData(Arrays.asList(dataObject))) {
                createButton.setText("등록 성공!");
            } else {
                createButton.setText("등록 실패!");
            }
        }
    }

    @FXML private void createRandomData(){
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.DISABLE);
        stage.close();
        clearAll();
        new DataCreatorView().view();
    }

    @FXML public void updateAdditionalData(){
        if(selectedAdditionalData != null) {
            String name = selectedAdditionalData.split(Main.SPLIT_SYMBOL)[0];
            String type = selectedAdditionalData.split(Main.SPLIT_SYMBOL)[1];
            int targetNum = Integer.parseInt(selectedData.split(Main.SPLIT_SYMBOL)[0]);
            String category = selectedData.split(Main.SPLIT_SYMBOL)[1];
            String element = selectedData.split(Main.SPLIT_SYMBOL)[2];
            String value;
            if(type.equals("정수")) {
                value = Long.toString(additionalDataNumberField.getNumberLong());
                type = "INTEGER";
            } else if(type.equals("실수")) {
                value = Double.toString(additionalDataRealField.getReal());
                type = "REAL";
            } else if(type.equals("이진수")) {
                value = Double.toString(additionalDataRealField.getReal());
                type = "BOOLEAN";
            } else {
                value = additionalDataTextField.getText();
                type = "TEXT";
            }
            MainController.dataDb.updateAdditionalDataName(category, element, targetNum, name, type, value);

            ObservableList<AdditionalDataTableObject> tableList = additionalDataTable.getItems();
            for(int count = 0; count < tableList.size(); count++) {
                if(tableList.get(count).getName().getValue().equals(name)) {
                    tableList.get(count).setValue(value);
                    tableList.set(count, new AdditionalDataTableObject(name, type, value));
                    additionalDataTable.setItems(tableList);
                    break;
                }
            }
        }
    }

    @FXML private void keyPush(KeyEvent event) {
        if (event.getCode().getName().equalsIgnoreCase("ENTER")) {
            String id = ((TextField) event.getSource()).getId();
            if (id.equals("additionalDataTextField") || id.equals("additionalDataNumberField") || id.equals("additionalDataRealField"))
                updateAdditionalData();
        }
    }

    @FXML private void setDefaultTextType(){
        setTypeMenuName("문자열");
    }
    @FXML private void setDefaultIntegerType(){
        setTypeMenuName("정수");
    }
    @FXML private void setDefaultRealType(){
        setTypeMenuName("실수");
    }
    @FXML private void setDefaultBooleanType(){
        setTypeMenuName("이진수");
    }






    public void additionalDataSearch(){
        String category = newAdditionalDataCategory.getText();
        additionalDataListTable.getItems().clear();
        if(category == null || category.equals(" - ") || ! model.containsKey(category)) {
            return;
        } else {
            List<AdditionalDataColumnObject> adcoList = MainController.dataDb.getAdditionalDataColumns(category);
            for(AdditionalDataColumnObject ado : adcoList)
                additionalDataListTable.getItems().add(ado);
        }
    }

    @FXML private void createAdditionalDataSelect(ActionEvent e) {
        MenuItem selectedMenuItem = (MenuItem) e.getSource();
        newAdditionalDataCategory.setText(selectedMenuItem.getText());
        additionalDataSearch();
    }

    private void setTypeMenuName(String name) {
        typeMenu.setText(name);
        newPointDefaultText.setText(null);
        newPointDefaultText.setVisible(false);
        newPointDefaultInteger.setText("0");
        newPointDefaultInteger.setVisible(false);
        newPointDefaultReal.setText("0.0");
        newPointDefaultReal.setVisible(false);
        newPointDefaultBoolean.setText("false");
        newPointDefaultBoolean.setVisible(false);
        if(name.equals("문자열"))
            newPointDefaultText.setVisible(true);
        else if(name.equals("정수"))
            newPointDefaultInteger.setVisible(true);
        else if(name.equals("실수"))
            newPointDefaultReal.setVisible(true);
        else if(name.equals("이진수"))
            newPointDefaultBoolean.setVisible(true);
    }

    @FXML private void setDefaultTrue(){
        newPointDefaultBoolean.setText("true");
    }

    @FXML private void setDefaultFalse(){
        newPointDefaultBoolean.setText("false");
    }

    @FXML private void addAdditionalData(){
        String additionalDataName = newAdditionalDataName.getText();
        String category = newAdditionalDataCategory.getText();
        if(additionalDataName != null && additionalDataName.trim().length() > 0) {
            List<AdditionalDataColumnObject> adcoList = MainController.dataDb.getAdditionalDataColumns(category);
            for(AdditionalDataColumnObject ado : adcoList) {
                if(additionalDataName.equals(ado.getName().getValue())) {
                    new AlarmAPI().existAdditionalValueName();
                    return;
                }
            }
            String name = additionalDataName;
            String type = null;
            String defaultValue = null;
            if(typeMenu.getText().equals("문자열")){
                defaultValue = newPointDefaultText.getText();
                type = "TEXT";
                if(defaultValue == null || defaultValue.trim().length() < 1) {
                    defaultValue = "NULL";
                    MainController.dataDb.createAdditionalDataTable(category, additionalDataName, "TEXT", null);
                } else {
                    MainController.dataDb.createAdditionalDataTable(category, additionalDataName, "TEXT", newPointDefaultText.getText());
                }
            }
            else if(typeMenu.getText().equals("정수")){
                MainController.dataDb.createAdditionalDataTable(category, additionalDataName, "INTEGER", newPointDefaultInteger.getText());
                type = "INTEGER";
                defaultValue = newPointDefaultInteger.getText();
            }
            else if(typeMenu.getText().equals("실수")){
                MainController.dataDb.createAdditionalDataTable(category, additionalDataName, "REAL", newPointDefaultReal.getText());
                type = "REAL";
                defaultValue = newPointDefaultReal.getText();
            }
            else if(typeMenu.getText().equals("이진수")) {
                MainController.dataDb.createAdditionalDataTable(category, additionalDataName, "BOOLEAN", newPointDefaultBoolean.getText());
                type = "BOOLEAN";
                defaultValue = newPointDefaultBoolean.getText();
            }
            else {
                MainController.dataDb.createAdditionalDataTable(category, additionalDataName, "INTEGER", "0");
                type = "INTEGER";
                defaultValue = "0";
            }
            additionalDataListTable.getItems().add(new AdditionalDataColumnObject(0, name, type, 0, defaultValue, 0));
            newPointDefaultText.setText(null);
            newPointDefaultInteger.setText("0");
            newPointDefaultReal.setText("0.0");
            newPointDefaultBoolean.setText("false");
            newAdditionalDataName.setText(null);
            newAdditionalDataName.requestFocus();
        }
    }

    private void deleteAdditionalData(String name) {
        String category = newAdditionalDataCategory.getText();
        if(category == null || category.equals(" - ") || ! model.containsKey(category)) {
            return;
        } else {
            MainController.dataDb.deleteAdditionalData(category, name);
        }
    }
}