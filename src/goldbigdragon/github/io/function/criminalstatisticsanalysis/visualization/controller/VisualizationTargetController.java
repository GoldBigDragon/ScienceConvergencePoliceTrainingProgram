package goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.controller;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.*;
import goldbigdragon.github.io.function.BaseController;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.view.MainView;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.AdditionalDataColumnObject;
import goldbigdragon.github.io.object.SubQueryObject;
import goldbigdragon.github.io.object.SubQueryTableObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.object.VisualizationObject;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class VisualizationTargetController extends BaseController {
    @FXML MenuButton willAddTarget;

    @FXML ScrollPane scrollPane;

    @Override
    @FXML public void close(ActionEvent event){
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.DISABLE);
        stage.close();
        new MainView().view();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            scrollPane.setFitToWidth(true);
            VBox rootBox = new VBox();
            rootBox.setFillWidth(true);
            rootBox.setAlignment(Pos.CENTER);
            rootBox.setStyle("-fx-background-color: transparent; -fx-fill-height: true; -fx-fill-width: true");
            rootBox.setPadding(new Insets(0, 0, 10, 0));
            rootBox.setId("additionalDataBox");
            scrollPane.setContent(rootBox);
            MenuItem categoryMenuItem;
            MenuItem elementMenuItem;
            for(String key : MainController.model.keySet()) {
                categoryMenuItem = new MenuItem("C::" + key);
                categoryMenuItem.setOnAction(event -> addTargetSelect(event));
                willAddTarget.getItems().add(categoryMenuItem);
                for(String element : MainController.model.get(key)) {
                    elementMenuItem = new MenuItem("E::" + key + "::" + element);
                    elementMenuItem.setOnAction(event -> addTargetSelect(event));
                    willAddTarget.getItems().add(elementMenuItem);
                }
            }
            MenuItem pointMenuItem;
            for(String point : MainController.point) {
                pointMenuItem = new MenuItem("P::" + point);
                pointMenuItem.setOnAction(event -> addTargetSelect(event));
                willAddTarget.getItems().add(pointMenuItem);
            }

            if(willAddTarget.getItems().size() > 0) {
                willAddTarget.setText(willAddTarget.getItems().get(0).getText());
            } else {
                new AlarmAPI().emptyCategory();
            }
        VisualizationObject vo2;
        for(int count = 0; count < MainController.visualizationTarget.size(); count++) {
            vo2 = MainController.visualizationTarget.get(count);
            addTargetToPane(vo2.type, vo2.type.code+"::"+vo2.name.replace(Main.SPLIT_SYMBOL, "::"));
        }
        Platform.runLater(() -> {
            VisualizationObject vo;
            for(int count = 0; count < MainController.visualizationTarget.size(); count++) {
                vo = MainController.visualizationTarget.get(count);
                if(!vo.subQueryList.isEmpty()) {
                    for(SubQueryObject sqo : vo.subQueryList) {
                        if(((VBox)rootBox.getChildren().get(count)).getChildren().size() > 2) {

                            addSubObject((TableView)((VBox)rootBox.getChildren().get(count)).getChildren().get(2), sqo.name, sqo.operator.symbol, sqo.value, sqo.logic.name());
                        }
                    }
                }
            }
        });
    }

    private void addTargetSelect(ActionEvent event) {
        MenuItem mi = (MenuItem)event.getSource();
        willAddTarget.setText(mi.getText());
    }

    @FXML private void addTarget(){
        String[] splitted = willAddTarget.getText().split("::");
        DataType dataType = null;
        String name = null;

        if(splitted[0].equals("C")) {
            dataType = DataType.CATEGORY;
            name = splitted[1];
        }
        else if(splitted[0].equals("E")) {
            dataType = DataType.ELEMENT;
            name = splitted[1] + Main.SPLIT_SYMBOL + splitted[2];
        } else if(splitted[0].equals("P")) {
            dataType = DataType.POINT;
            name = splitted[1];
        }

        if(dataType != null && name != null) {
            boolean exist = false;
            if(MainController.visualizationTarget != null && ! MainController.visualizationTarget.isEmpty()) {
                for(VisualizationObject vo : MainController.visualizationTarget) {
                    if(vo.type == dataType && vo.name.equals(name)) {
                        exist = true;
                        break;
                    }
                }
            }
            if( ! exist) {
                addTargetToPane(dataType, willAddTarget.getText());
                MainController.visualizationTarget.add(new VisualizationObject(dataType, name));
            } else {
                new AlarmAPI().existTarget();
            }
        }
    }

    private void addTargetToPane(DataType dataType, String name){
        Platform.runLater(() -> {
            VBox vbox = new VBox(10);
            vbox.setUserData(name);
            vbox.setFillWidth(true);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(10, 10, 10, 10));
            vbox.setStyle("-fx-background-color: white; -fx-fill-height: true; -fx-fill-width: true");
            HBox.setHgrow(vbox, Priority.ALWAYS);

            GridPane gp = new GridPane();
            Label label = new Label(name);
            label.setMaxWidth(Double.MAX_VALUE);
            Button delete = new Button();
            delete.setId("deleteButton");
            delete.setOnAction(event -> deleteTarget(event));

            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100);
            rc.setValignment(VPos.CENTER);
            gp.getRowConstraints().add(rc);
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHalignment(HPos.LEFT);
            cc.setPercentWidth(80);
            gp.getColumnConstraints().add(cc);
            ColumnConstraints cc2 = new ColumnConstraints();
            cc2.setHalignment(HPos.RIGHT);
            cc2.setPercentWidth(20);
            gp.getColumnConstraints().add(cc2);
            gp.add(label, 0, 0);
            gp.add(delete, 1, 0);
            vbox.getChildren().add(gp);

            if(dataType != DataType.POINT) {
                List<AdditionalDataColumnObject> additionalDataColumnList = MainController.dataDb.getAdditionalDataColumns(name.split("::")[1]);
                if(!additionalDataColumnList.isEmpty()) {

                    HBox subQueryCreator = new HBox(3);
                    subQueryCreator.setAlignment(Pos.CENTER_LEFT);
                    MenuButton subQueryTargetSelector = new MenuButton();
                    MenuItem targetMi;
                    String firstType = null;
                    for(AdditionalDataColumnObject column : additionalDataColumnList) {
                        targetMi = new MenuItem(column.getName().getValue());
                        targetMi.setOnAction(event -> setParentName(event, subQueryTargetSelector));
                        targetMi.setUserData(column.getType().getValue());
                        subQueryTargetSelector.getItems().add(targetMi);
                        if(firstType == null)
                            firstType = column.getType().getValue();
                    }
                    subQueryTargetSelector.setText(subQueryTargetSelector.getItems().get(0).getText());
                    subQueryTargetSelector.setUserData(firstType);
                    MenuButton operatorSelector = new MenuButton();
                    MenuItem operatorMi;
                    for(OperatorType ot : OperatorType.values()) {
                        operatorMi = new MenuItem(ot.symbol);
                        operatorMi.setUserData(name);
                        operatorMi.setOnAction(event -> setParentName(event, operatorSelector));
                        operatorSelector.getItems().add(operatorMi);
                    }
                    operatorSelector.setText("==");
                    operatorSelector.setUserData(name);
                    TextField valueField = new TextField();
                    valueField.setPromptText("비교값 입력");
                    valueField.setMaxWidth(100);
                    MenuButton logicSelector = new MenuButton();
                    MenuItem logicMi;
                    for(LogicType lt : LogicType.values()) {
                        logicMi = new MenuItem(lt.name());
                        logicMi.setOnAction(event -> setParentName(event, logicSelector));
                        logicSelector.getItems().add(logicMi);
                    }
                    logicSelector.setText("AND");
                    Label space = new Label("  ");
                    Button subQueryAddButton = new Button("+");
                    subQueryAddButton.setOnAction(event -> addSubObject(event));
                    subQueryCreator.getChildren().addAll(subQueryTargetSelector, operatorSelector, valueField, logicSelector, space, subQueryAddButton);
                    vbox.getChildren().add(subQueryCreator);

                    TableView<SubQueryTableObject> table = new TableView();
                    table.setMaxHeight(130);
                    table.setEditable(false);
                    table.setPlaceholder(new Label("추가된 서브 쿼리가 없습니다!"));
                    TableColumn<SubQueryTableObject,String> targetNameColumn = new TableColumn("대상");
                    TableColumn<SubQueryTableObject,String> operatorColumn = new TableColumn("연산자");
                    TableColumn<SubQueryTableObject,String> valueColumn = new TableColumn("비교값");
                    TableColumn<SubQueryTableObject,String> logicColumn = new TableColumn("논리");
                    TableColumn<SubQueryTableObject,String> deleteColumn = new TableColumn("제거");
                    targetNameColumn.setCellValueFactory(cellData -> cellData.getValue().getTarget());
                    operatorColumn.setCellValueFactory(cellData -> cellData.getValue().getSymbol());
                    valueColumn.setCellValueFactory(cellData -> cellData.getValue().getCompareValue());
                    logicColumn.setCellValueFactory(cellData -> cellData.getValue().getLogicalOperator());
                    deleteColumn.setCellFactory(tc -> new TableCell<SubQueryTableObject, String>() {
                        final Button btn = new Button();
                        @Override public void updateItem(String value ,boolean empty){
                            btn.setId("deleteButton");
                            btn.setOnAction(event -> {
                                int index = getIndex();
                                String targetColumn = table.getItems().get(index).getTarget().getValue();
                                String symbol = table.getItems().get(index).getSymbol().getValue();
                                String compareValue = table.getItems().get(index).getCompareValue().getValue();
                                String logicalOperator = table.getItems().get(index).getLogicalOperator().getValue();
                                deleteSubQuery(name, targetColumn, symbol, compareValue, logicalOperator);
                                table.getItems().remove(index);
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
                    targetNameColumn.setStyle("-fx-alignment: CENTER;");
                    operatorColumn.setStyle("-fx-alignment: CENTER;");
                    valueColumn.setStyle("-fx-alignment: CENTER;");
                    logicColumn.setStyle("-fx-alignment: CENTER;");
                    deleteColumn.setStyle("-fx-alignment: CENTER;");
                    table.getColumns().addAll(targetNameColumn, operatorColumn, valueColumn, logicColumn, deleteColumn);
                    vbox.getChildren().add(table);
                }
            }
            ((VBox)scrollPane.getContent()).getChildren().add(vbox);
        });
    }

    private void deleteTarget(ActionEvent event){
        VBox parent = (VBox)((Button)event.getSource()).getParent().getParent();
        Platform.runLater(() -> {
            String[] splitted = parent.getUserData().toString().split("::");
            DataType dataType = null;
            String name = null;
            if(splitted[0].equals("C")) {
                dataType = DataType.CATEGORY;
                name = splitted[1];
            }
            else if(splitted[0].equals("E")) {
                dataType = DataType.ELEMENT;
                name = splitted[1] + Main.SPLIT_SYMBOL + splitted[2];
            } else if(splitted[0].equals("P")) {
                dataType = DataType.POINT;
                name = splitted[1];
            }
            for(VisualizationObject vo : MainController.visualizationTarget) {
                if(vo.type == dataType && vo.name.equals(name)) {
                    MainController.visualizationTarget.remove(vo);
                    break;
                }
            }
            ((VBox)scrollPane.getContent()).getChildren().remove(parent);
        });
    }

    private void setParentName(ActionEvent event, MenuButton mb) {
        Platform.runLater(() -> {
            MenuItem mi = ((MenuItem)event.getSource());
            mb.setUserData(mi.getUserData());
            mb.setText(mi.getText());
        });
    }

    private void addSubObject(TableView tv, String target, String symbol, String compareValue, String logicalOperator){
        tv.getItems().add(new SubQueryTableObject(target, symbol, compareValue, logicalOperator));
    }

    private void addSubObject(ActionEvent event) {
        Button addButton = (Button)event.getSource();
        HBox parent = ((HBox)addButton.getParent());
        String column = ((MenuButton)parent.getChildren().get(0)).getText();
        String columnType = ((MenuButton)parent.getChildren().get(0)).getUserData().toString();
        String operator = ((MenuButton)parent.getChildren().get(1)).getText();
        String name = ((MenuButton)parent.getChildren().get(1)).getUserData().toString();
        String value = ((TextField)parent.getChildren().get(2)).getText();
        String logic = ((MenuButton)parent.getChildren().get(3)).getText();

        if(columnType.equals("TEXT")) {
            if(value == null || value.length() < 1){
                return;
            }
        } else if(columnType.equals("INTEGER") || columnType.equals("LONG")) {
            try {
                if(!(Integer.parseInt(value) >= Integer.MIN_VALUE && Integer.parseInt(value) <= Integer.MAX_VALUE)) {
                    value = "0";
                }
            }
            catch(NumberFormatException e) {
                value = "0";
            }
        } else if(columnType.equals("REAL")) {
            try {
                if(!(Double.parseDouble(value) >= Double.MIN_VALUE&& Double.parseDouble(value) <= Double.MAX_VALUE)) {
                    value = "0.0";
                }
            } catch(NumberFormatException e) {
                value = "0.0";
            }
        } else if(columnType.equals("BOOLEAN")) {
            try {
                value = Boolean.toString(Boolean.parseBoolean(value));
            } catch(NumberFormatException e) {
                value = "true";
            }
        }
        addSubQuery(name, column, columnType, operator, value, logic);
    }

    private void addSubQuery(String name, String column, String columnType, String operator, String value, String logic){
        List<Node> nodes = ((VBox)scrollPane.getContent()).getChildren();
        VBox mainPanel;
        TableView<SubQueryTableObject> table;
        for(Node n : nodes) {
            mainPanel = (VBox)n;
            if(n.getUserData().toString().equals(name) && mainPanel.getChildren().size() > 2){
                String[] splitted = name.split("::");
                String splittedName;
                DataType dataType;
                if(splitted[0].equals("C")) {
                    dataType = DataType.CATEGORY;
                    splittedName = splitted[1];
                }
                else if(splitted[0].equals("E")) {
                    dataType = DataType.ELEMENT;
                    splittedName = splitted[1] + Main.SPLIT_SYMBOL + splitted[2];
                } else {
                    return;
                }
                for(int count = 0; count < MainController.visualizationTarget.size(); count++) {
                    if(MainController.visualizationTarget.get(count).type == dataType &&
                        MainController.visualizationTarget.get(count).name.equals(splittedName)) {
                        ValueType valueType = null;
                        for(ValueType vt : ValueType.values()) {
                            if(vt.name().equals(columnType)) {
                                valueType = vt;
                                break;
                            }
                        }
                        OperatorType operatorType = null;
                        for(OperatorType ot : OperatorType.values()) {
                            if(ot.symbol.equals(operator)) {
                                operatorType = ot;
                                break;
                            }
                        }
                        LogicType logicType = null;
                        for(LogicType lt : LogicType.values()) {
                            if(lt.name().equals(logic)) {
                                logicType = lt;
                                break;
                            }
                        }
                        if(valueType != null && operatorType != null && logicType != null) {
                            MainController.visualizationTarget.get(count).addSubQuery(valueType, column, operatorType, value, logicType);
                            table = (TableView)mainPanel.getChildren().get(2);
                            table.getItems().add(new SubQueryTableObject(column, operator, value, logic));
                        }
                        return;
                    }
                }
                break;
            }
        }
    }

    private void deleteSubQuery(String name, String column, String operator, String value, String logic){
        String[] splitted = name.split("::");
        String splittedName;
        DataType dataType;
        if(splitted[0].equals("C")) {
            dataType = DataType.CATEGORY;
            splittedName = splitted[1];
        }
        else if(splitted[0].equals("E")) {
            dataType = DataType.ELEMENT;
            splittedName = splitted[1] + Main.SPLIT_SYMBOL + splitted[2];
        } else {
            return;
        }
        for(int count = 0; count < MainController.visualizationTarget.size(); count++) {
            if(MainController.visualizationTarget.get(count).type == dataType &&
                    MainController.visualizationTarget.get(count).name.equals(splittedName)) {
                OperatorType operatorType = null;
                for(OperatorType ot : OperatorType.values()) {
                    if(ot.symbol.equals(operator)) {
                        operatorType = ot;
                        break;
                    }
                }
                LogicType logicType = null;
                for(LogicType lt : LogicType.values()) {
                    if(lt.name().equals(logic)) {
                        logicType = lt;
                        break;
                    }
                }
                if(operatorType != null && logicType != null) {
                    MainController.visualizationTarget.get(count).removeSubQuery(column, operatorType, value, logicType);
                }
                return;
            }
        }
    }
}
