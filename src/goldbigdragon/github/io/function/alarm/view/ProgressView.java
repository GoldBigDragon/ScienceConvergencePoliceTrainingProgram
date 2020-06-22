package goldbigdragon.github.io.function.alarm.view;

import goldbigdragon.github.io.function.alarm.enums.AfterWorkType;
import goldbigdragon.github.io.function.alarm.enums.BeforeWorkType;
import goldbigdragon.github.io.enums.SoundType;
import goldbigdragon.github.io.function.alarm.controller.ProgressController;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class ProgressView {

    public void view(String title, List<BeforeWorkType> beforeWork, List<AfterWorkType> afterWork){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/alarm/progress.fxml"));
            Parent root = loader.load();
            ProgressController controller = loader.getController();
            controller.setText(title);
            controller.setProces(beforeWork, afterWork);
            root.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
            Stage stage = new Stage();
            stage.getIcons().add(new Image("resources/img/ysu.png"));
            stage.setTitle("Progress");
            stage.setScene(new Scene(root, 400, 200));
            stage.getScene().setFill(null);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setResizable(false);
            stage.show();
            SoundUtil.playSound(SoundType.DISABLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
