package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.controller;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.function.alarm.enums.AfterWorkType;
import goldbigdragon.github.io.function.alarm.enums.BeforeWorkType;
import goldbigdragon.github.io.enums.SoundType;
import goldbigdragon.github.io.function.BaseController;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import goldbigdragon.github.io.function.alarm.view.ProgressView;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.view.MainView;
import goldbigdragon.github.io.node.RealField;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.object.PointTableObject;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ModelEditorController extends BaseController {
    @FXML private TreeView treeView;
    @FXML private VBox activePane;
    @FXML private TextField newCategoryName;
    @FXML private TextField editCategoryName;
    @FXML private TextField newElementName;
    @FXML private TextField editElementName;
    @FXML private TextField newPointName;
    @FXML private TextField editPointName;
    @FXML private RealField defaultValue;

    @FXML private Button editDefaultValueButton;

    @FXML private TableView<PointTableObject> pointTable;
    @FXML private TableColumn<PointTableObject, String> nameTableColumn;
    @FXML private TableColumn<PointTableObject, String> valueTableColumn;

    public static Map<String, List<String>> model;
    public static List<String> point;
    public static Map<String, Double> defaultPoint;

    private final String ROOT_MODEL_NAME = "모델";
    private final String ROOT_POINT_NAME = "기준점수";
    private static String init = null;

    private static String selectedCategory;
    private static String selectedElement;
    private static String selectedDefaultValuePoint;

    public static void clearAll(){
        model = null;
        point = null;
        defaultPoint = null;
        init = null;
        selectedCategory = null;
        selectedElement = null;
        selectedDefaultValuePoint = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            Platform.runLater(() -> {
                if(init == null) {
                    treeView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener() {
                        @Override
                        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                            TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                            TreeItem<String> parent = selectedItem.getParent();
                            String parentName = parent.getValue();
                            String targetName = selectedItem.getValue();
                            if(parentName.equals("DUMMY")) {
                                if(targetName.equals(ROOT_MODEL_NAME))
                                    showModelAddPage();
                                else if(targetName.equals(ROOT_POINT_NAME))
                                    showPointAddPage();
                            } else {
                                String grandParentName = parent.getParent().getValue();
                                if(parentName.equals(ROOT_MODEL_NAME)) {
                                    showMainEditPage(targetName);
                                }
                                else if(grandParentName.equals(ROOT_MODEL_NAME)) {
                                    showSubEditPage(parentName, targetName);
                                }
                                else if(parent.getValue().equals(ROOT_POINT_NAME)) {
                                    String pointName = selectedItem.getValue();
                                    showPointInfo(pointName);
                                }
                            }
                        }
                    });
                    clearTree();
                    model = MainController.modelDb.getModel();
                    point = MainController.modelDb.getPoint();
                    defaultPoint = MainController.modelDb.getDefaultPoint(point);
                    addDefaultTree();
                } else if(init.equals("ADD_CATEGORY")) {
                    newCategoryName.requestFocus();
                } else if(init.equals("CATEGORY")) {
                    editCategoryName.requestFocus();
                } else if(init.equals("ELEMENT")) {
                    editElementName.requestFocus();
                    pointTable.setPlaceholder(new Label("등록된 기준점수가 없습니다!"));
                    nameTableColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
                    valueTableColumn.setCellValueFactory(cellData -> cellData.getValue().getValue());

                    pointTable.setRowFactory( tv -> {
                        TableRow<PointTableObject> row = new TableRow<>();
                        row.setOnMouseClicked(event -> {
                            if (! row.isEmpty()) {
                                PointTableObject data = row.getItem();
                                selectedDefaultValuePoint = data.getName().getValue();
                                defaultValue.setText(data.getValue().getValue());
                                defaultValue.setDisable(false);
                                editDefaultValueButton.setDisable(false);
                                defaultValue.requestFocus();
                            }
                        });
                        return row ;
                    });

                    defaultValue.setDisable(true);
                    editDefaultValueButton.setDisable(true);

                    String key = selectedCategory + Main.SPLIT_SYMBOL + selectedElement;
                    for(String pointName : point) {
                        if(defaultPoint.containsKey(key + Main.SPLIT_SYMBOL + pointName)) {
                            pointTable.getItems().add(new PointTableObject(pointName, defaultPoint.get(key + Main.SPLIT_SYMBOL + pointName).toString()));
                        }
                        else {
                            pointTable.getItems().add(new PointTableObject(pointName, "0.0"));
                        }
                    }
                } else if(init.equals("ADD_POINT")) {
                    newPointName.requestFocus();
                } else if(init.equals("INFO_POINT")) {
                    editPointName.requestFocus();
                }
            });
    }

    @Override
    @FXML public void close(ActionEvent event){
        new MainView().view();
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.DISABLE);
        stage.close();
        clearAll();
    }


    private void clearTree(){
        TreeItem root = new TreeItem<String>("DUMMY");
        treeView.setRoot(null);
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        TreeItem categoryRoot = new TreeItem<String>(ROOT_MODEL_NAME);
        TreeItem pointRoot = new TreeItem<String>(ROOT_POINT_NAME);
        addTreeItem(root, ROOT_MODEL_NAME);
        addTreeItem(root, ROOT_POINT_NAME);
    }

    private void addDefaultTree(){
        TreeItem cbti;
        TreeItem modelRoot = (TreeItem) treeView.getRoot().getChildren().get(0);
        for(String main : model.keySet()) {
            cbti = addTreeItem(modelRoot, main);
            if( ! model.get(main).isEmpty())
                addElementTree(cbti, main);
        }
        TreeItem pointRoot = (TreeItem) treeView.getRoot().getChildren().get(1);
        for(String point : point) {
            addTreeItem(pointRoot, point);
        }
    }

    private void addElementTree(TreeItem root, String main){
        for(String sub : model.get(main)) {
            addTreeItem(root, sub);
        }
    }

    private TreeItem addTreeItem(TreeItem root, String name){
        TreeItem<String> item = new TreeItem<String>(name);
        if(root != null)
            root.getChildren().add(item);
        else
            ((TreeItem)(treeView.getRoot().getChildren().get(0))).getChildren().add(item);
        return item;
    }

    @FXML public void addCategory(){
        TreeView tv = (TreeView) newCategoryName.getParent().getParent().getParent().getChildrenUnmodifiable().get(0);
        String name = newCategoryName.getText();
        if(name != null && name.trim().length() > 0) {
            if(! model.containsKey(name)) {
                List<String> emptyList = new ArrayList<>();
                model.put(name, emptyList);
                addTreeItem((TreeItem)tv.getRoot().getChildren().get(0), name);
                newCategoryName.setText(null);
                newCategoryName.requestFocus();
            } else {
                new AlarmAPI().existCategory();
            }
        }
    }

    @FXML public void addElement(){
        String name = newElementName.getText();
        if(name != null && name.trim().length() > 0) {
            List<String> elementList = model.get(selectedCategory);
            if(elementList == null)
                elementList = new ArrayList<>();
            if(! elementList.contains(name)) {
                elementList.add(name);
                model.put(selectedCategory, elementList);
                TreeView tv = (TreeView) newElementName.getParent().getParent().getParent().getChildrenUnmodifiable().get(0);
                TreeItem category = ((TreeItem)tv.getRoot().getChildren().get(0));
                String treeItemName;
                for(int count = 0; count < category.getChildren().size(); count++) {
                    treeItemName = ((TreeItem)category.getChildren().get(count)).getValue().toString();
                    if(treeItemName.equals(selectedCategory)) {
                        addTreeItem(((TreeItem)category.getChildren().get(count)), name);
                        newElementName.setText(null);
                        newElementName.requestFocus();
                        break;
                    }
                }
            } else {
                new AlarmAPI().existElement();
            }
        }
    }

    @FXML public void editCategoryName(){
        String name = editCategoryName.getText();
        if(name != null && name.trim().length() > 0) {
            List<String> keys = new ArrayList<>();
            keys.addAll(defaultPoint.keySet());
            String[] splitted;
            for(int count = 0; count < keys.size(); count++) {
                splitted = keys.get(count).split(Main.SPLIT_SYMBOL);
                if(splitted[0].equals(selectedCategory)) {
                    defaultPoint.put(name + Main.SPLIT_SYMBOL + splitted[1] + Main.SPLIT_SYMBOL + splitted[2], defaultPoint.get(keys.get(count)));
                    defaultPoint.remove(keys.get(count));
                }
            }
            List<String> elements = model.get(selectedCategory);
            model.remove(selectedCategory);
            model.put(name, elements);

            TreeView tv = (TreeView) newElementName.getParent().getParent().getParent().getChildrenUnmodifiable().get(0);
            TreeItem category = ((TreeItem)tv.getRoot().getChildren().get(0));
            String treeItemName;
            for(int count = 0; count < category.getChildren().size(); count++) {
                treeItemName = ((TreeItem)category.getChildren().get(count)).getValue().toString();
                if(treeItemName.equals(selectedCategory)) {
                    ((TreeItem)category.getChildren().get(count)).setValue(name);
                    selectedCategory = name;
                    editCategoryName.setText(null);
                    editCategoryName.requestFocus();
                    break;
                }
            }
        }
    }

    @FXML public void deleteCategory(){
        List<String> keys = new ArrayList<>();
        keys.addAll(defaultPoint.keySet());
        String[] splitted;
        for(int count3 = 0; count3 < keys.size(); count3++) {
            splitted = keys.get(count3).split(Main.SPLIT_SYMBOL);
            if(splitted[0].equals(selectedCategory)) {
                defaultPoint.remove(keys.get(count3));
            }
        }
        model.remove(selectedCategory);
        TreeView tv = (TreeView) newElementName.getParent().getParent().getParent().getChildrenUnmodifiable().get(0);
        TreeItem category = ((TreeItem)tv.getRoot().getChildren().get(0));
        String treeItemName;
        for(int count = 0; count < category.getChildren().size(); count++) {
            treeItemName = ((TreeItem)category.getChildren().get(count)).getValue().toString();
            if(treeItemName.equals(selectedCategory)) {
                ((TreeItem)category).getChildren().remove(count);
                activePane = null;
                break;
            }
        }
    }

    @FXML private void editElementName(){
        String name = editElementName.getText();
        if(name != null && name.trim().length() > 0) {
            List<String> keys = new ArrayList<>();
            keys.addAll(defaultPoint.keySet());
            String[] splitted;
            for(int count = 0; count < keys.size(); count++) {
                splitted = keys.get(count).split(Main.SPLIT_SYMBOL);
                if(splitted[0].equals(selectedCategory) && splitted[1].equals(selectedElement)) {
                    defaultPoint.put(splitted[0] + Main.SPLIT_SYMBOL + name + Main.SPLIT_SYMBOL + splitted[2], defaultPoint.get(keys.get(count)));
                    defaultPoint.remove(keys.get(count));
                }
            }
            List<String> elementList = model.get(selectedCategory);
            for(int count = 0; count < elementList.size(); count++) {
                if(elementList.get(count).equals(selectedElement)) {
                    elementList.set(count, name);
                    model.put(selectedCategory, elementList);
                    break;
                }
            }


            TreeView tv = (TreeView) editElementName.getParent().getParent().getParent().getChildrenUnmodifiable().get(0);
            TreeItem category = ((TreeItem)tv.getRoot().getChildren().get(0));
            String treeItemName;
            for(int count = 0; count < category.getChildren().size(); count++) {
                treeItemName = ((TreeItem)category.getChildren().get(count)).getValue().toString();
                if(treeItemName.equals(selectedCategory)) {
                    for(int count2 = 0; count2 < ((TreeItem)category.getChildren().get(count)).getChildren().size(); count2++) {
                        if(((TreeItem)((TreeItem)category.getChildren().get(count)).getChildren().get(count2)).getValue().equals(selectedElement)) {
                            TreeItem ti = (TreeItem)((TreeItem)category.getChildren().get(count)).getChildren().get(count2);
                            ti.setValue(name);
                            selectedElement = name;
                            editElementName.setText(null);
                            editElementName.requestFocus();
                            return;
                        }
                    }
                }
            }
        }
    }

    @FXML private void deleteElement(){
        TreeView tv = (TreeView) editElementName.getParent().getParent().getParent().getChildrenUnmodifiable().get(0);
        TreeItem category = ((TreeItem)tv.getRoot().getChildren().get(0));
        String treeItemName;
        for(int count = 0; count < category.getChildren().size(); count++) {
            treeItemName = ((TreeItem)category.getChildren().get(count)).getValue().toString();
            if(treeItemName.equals(selectedCategory)) {
                for(int count2 = 0; count2 < ((TreeItem)category.getChildren().get(count)).getChildren().size(); count2++) {
                    if(((TreeItem)((TreeItem)category.getChildren().get(count)).getChildren().get(count2)).getValue().equals(selectedElement)) {
                        List<String> keys = new ArrayList<>();
                        keys.addAll(defaultPoint.keySet());
                        String[] splitted;
                        for(int count3 = 0; count3 < keys.size(); count3++) {
                            splitted = keys.get(count3).split(Main.SPLIT_SYMBOL);
                            if(splitted[0].equals(selectedCategory) && splitted[1].equals(selectedElement)) {
                                defaultPoint.remove(keys.get(count3));
                            }
                        }
                        List<String> newElementList = new ArrayList<>();
                        for(String e : model.get(selectedCategory)) {
                            if(!e.equals(selectedElement)) {
                                newElementList.add(e);
                            }
                        }
                        model.put(selectedCategory, newElementList);
                        activePane = null;
                        ((TreeItem)category.getChildren().get(count)).getChildren().remove(count2);
                        return;
                    }
                }
            }
        }
    }

    @FXML private void addPoint(){
        String pointName = newPointName.getText();
        if(point.contains(pointName)) {
            new AlarmAPI().existPointName();
        } else {
            if(pointName != null && pointName.trim().length() > 0) {
                point.add(pointName);
                TreeView tv = (TreeView) newPointName.getParent().getParent().getParent().getChildrenUnmodifiable().get(0);
                TreeItem pointTree = ((TreeItem)tv.getRoot().getChildren().get(1));
                addTreeItem(pointTree, pointName);
                newPointName.setText(null);
                newPointName.requestFocus();
            }
        }
    }

    @FXML public void editPointName(){
        String name = editPointName.getText();
        if(name != null && name.trim().length() > 0) {
            editPointName(selectedCategory, name);
            TreeView tv = (TreeView) editPointName.getParent().getParent().getParent().getChildrenUnmodifiable().get(0);
            TreeItem point = ((TreeItem)tv.getRoot().getChildren().get(1));
            String treeItemName;
            for(int count = 0; count < point.getChildren().size(); count++) {
                treeItemName = ((TreeItem)point.getChildren().get(count)).getValue().toString();
                if(treeItemName.equals(selectedCategory)) {
                    ((TreeItem)point.getChildren().get(count)).setValue(name);
                    selectedCategory = name;
                    editPointName.setText(null);
                    editPointName.requestFocus();
                    break;
                }
            }
        }
    }

    @FXML public void deletePoint(){
        deletePoint(selectedCategory);
        TreeView tv = (TreeView) editPointName.getParent().getParent().getParent().getChildrenUnmodifiable().get(0);
        TreeItem point = ((TreeItem)tv.getRoot().getChildren().get(1));
        String treeItemName;
        for(int count = 0; count < point.getChildren().size(); count++) {
            treeItemName = ((TreeItem)point.getChildren().get(count)).getValue().toString();
            if(treeItemName.equals(selectedCategory)) {
                ((TreeItem)point).getChildren().remove(count);
                activePane = null;
                break;
            }
        }
    }

    private void editPointName(String name, String newName) {
        List<String> keys = new ArrayList<>();
        List<String> willRemove = new ArrayList<>();
        keys.addAll(defaultPoint.keySet());
        String key;
        String[] keySplit;
        for(int count = 0; count < keys.size(); count++) {
            key = keys.get(count);
            keySplit = key.split(Main.SPLIT_SYMBOL);
            if(keySplit[2].equals(name)) {
                defaultPoint.put(keySplit[0] + Main.SPLIT_SYMBOL + keySplit[1] + Main.SPLIT_SYMBOL + newName, defaultPoint.get(key));
                willRemove.add(key);
            }
        }
        for(String keyName : willRemove)
            defaultPoint.remove(keyName);
        for(int count = 0; count < point.size(); count++) {
            if(point.get(count).equals(name)) {
                point.set(count, newName);
            }
        }
    }

    @FXML private void editDefaultValue(){
        double value = defaultValue.getReal();
        ObservableList<PointTableObject> tableList = pointTable.getItems();
        for(int count = 0; count < tableList.size(); count++) {
            if(tableList.get(count).getName().getValue().equals(selectedDefaultValuePoint)) {
                tableList.get(count).setValue(Double.toString(value));
                tableList.set(count, new PointTableObject(tableList.get(count).getName().getValue(), Double.toString(value)));
                pointTable.setItems(tableList);
                break;
            }
        }
        String key = selectedCategory + Main.SPLIT_SYMBOL + selectedElement + Main.SPLIT_SYMBOL + selectedDefaultValuePoint;
        defaultPoint.put(key, value);
    }

    @FXML private void keyPush(KeyEvent event) {
        if (event.getCode().getName().equalsIgnoreCase("ENTER")) {
            String id = ((TextField) event.getSource()).getId();
            if (id.equals("newCategoryName"))
                addCategory();
            else if (id.equals("newPointName"))
                addPoint();
            else if (id.equals("editCategoryName"))
                editCategoryName();
            else if (id.equals("newElementName"))
                addElement();
            else if (id.equals("editElementName"))
                editElementName();
            else if (id.equals("editPointName"))
                editPointName();
            else if (id.equals("defaultValue"))
                editDefaultValue();
        }
    }

    @FXML private void saveModel(){
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.DISABLE);
        stage.close();
        List<BeforeWorkType> beforeWork = new ArrayList<>();
        beforeWork.add(BeforeWorkType.OVERWRITE_CRIMINALSTATISTICSANALYSIS_MODEL);
        List<AfterWorkType> afterWork = new ArrayList<>();
        afterWork.add(AfterWorkType.OPEN_CRIMINALSTATISTICSANALYSIS_MAIN);
        new ProgressView().view("모델 정보를 저장 중입니다..!", beforeWork, afterWork);
    }

    private void deletePoint(String name) {
        point.remove(name);
        List<String> keys = new ArrayList<>();
        keys.addAll(defaultPoint.keySet());
        for(int count = 0; count < keys.size(); count++) {
            String key = keys.get(count);
            if(key.split(Main.SPLIT_SYMBOL)[2].equals(name))
                defaultPoint.remove(key);
        }
    }


    private boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    private void showModelAddPage(){
        init = "ADD_CATEGORY";
        loadFxml("criminalstatisticsanalysis/modelEditor/addCategory");
    }

    private void showMainEditPage(String main){
        init = "CATEGORY";
        selectedCategory = main;
        loadFxml("criminalstatisticsanalysis/modelEditor/categoryEditor");
    }

    private void showSubEditPage(String main, String sub){
        init = "ELEMENT";
        selectedCategory = main;
        selectedElement = sub;
        loadFxml("criminalstatisticsanalysis/modelEditor/elementEditor");
    }

    private void showPointAddPage(){
        init = "ADD_POINT";
        loadFxml("criminalstatisticsanalysis/modelEditor/addPoint");
    }

    private void showPointInfo(String pointName){
        init = "INFO_POINT";
        selectedCategory = pointName;
        loadFxml("criminalstatisticsanalysis/modelEditor/pointInfo");
    }
    private void loadFxml(String file) {
        activePane.getChildren().clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/" + file + ".fxml"));
            activePane.getChildren().setAll(root);
        } catch(IOException e) {}
    }
}
