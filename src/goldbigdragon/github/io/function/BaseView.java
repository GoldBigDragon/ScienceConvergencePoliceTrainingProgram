package goldbigdragon.github.io.function;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class BaseView {

    public Stage stage = null;

    public void view(String fxmlPath, String title, int width, int height,  boolean isTransparent, boolean isResizeable) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            root.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");

            stage = new Stage();
            // 아이콘 설정
            stage.getIcons().add(new Image("resources/img/ysu.png"));
            stage.setTitle(title);

            // 윈도우 창 숨기기
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root, width, height));
            // 투명 색상 적용
            if (isTransparent) {
                stage.getScene().setFill(null);
                stage.initStyle(StageStyle.TRANSPARENT);
            }
            stage.setResizable(isResizeable);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
