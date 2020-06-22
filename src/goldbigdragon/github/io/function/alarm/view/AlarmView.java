package goldbigdragon.github.io.function.alarm.view;

import goldbigdragon.github.io.enums.SoundType;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import goldbigdragon.github.io.function.alarm.controller.AlarmController;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AlarmView {

    public void view(String title){
        if(AlarmAPI.alarmMap.containsKey(title)) {
            AlarmAPI.alarmMap.get(title).toFront();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/alarm/alarm.fxml"));
                Parent root = loader.load();
                root.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
                AlarmController controller = loader.getController();
                controller.setText(title);
                Stage stage = new Stage();
                stage.getIcons().add(new Image("resources/img/ysu.png"));
                stage.setTitle("Alarm");
                stage.setScene(new Scene(root, 400, 200));
                stage.getScene().setFill(null);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setResizable(false);
                stage.show();
                SoundUtil.playSound(SoundType.DISABLE);
                AlarmAPI.alarmMap.put(title, stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
