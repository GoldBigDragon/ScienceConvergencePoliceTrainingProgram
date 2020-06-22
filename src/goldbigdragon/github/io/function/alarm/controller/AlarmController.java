package goldbigdragon.github.io.function.alarm.controller;

import goldbigdragon.github.io.function.BaseController;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AlarmController extends BaseController {
    @FXML private Label title;

    public void setText(String title) {
        Platform.runLater(()->{
            this.title.textProperty().setValue(title);
        });
    }


    @Override
    @FXML public void close(ActionEvent event){
        AlarmAPI.alarmMap.remove(title.getText());
        super.close(event);
    }
}
