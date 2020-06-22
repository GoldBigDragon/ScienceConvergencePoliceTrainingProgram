package goldbigdragon.github.io.function;

import goldbigdragon.github.io.enums.SoundType;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

public class BaseController implements Initializable {
    @FXML protected GridPane mainPane;
    @FXML private Button alwaysTop;

    private double xoffset = 0;
    private double yoffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML private void alwaysTop(ActionEvent event){
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.CHECK);
        if(stage.isAlwaysOnTop()) {
            alwaysTop.setStyle("-fx-background-image: url(\"/resources/img/common/pin.png\");");
            alwaysTop.setId("alwaysTopActive");
            alwaysTop.getTooltip().setText("항상 위");
        } else {
            alwaysTop.setStyle("-fx-background-image: url(\"/resources/img/common/pin_active.png\");");
            alwaysTop.setId("alwaysTop");
            alwaysTop.getTooltip().setText("항상 위 해제");
        }
        stage.setAlwaysOnTop(!stage.isAlwaysOnTop());
    }

    @FXML public void screenshot(ActionEvent event){
        SoundUtil.playSound(SoundType.SCREENSHOT);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("스크린샷 저장");
        fileChooser.setInitialFileName("SCPTP_" + Calendar.getInstance().getTimeInMillis());
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("이미지 파일", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(mainPane.getScene().getWindow());
        if (file != null) {
            try
            {
                if(!file.exists())
                    file.createNewFile();
                WritableImage image = mainPane.snapshot(new SnapshotParameters(), null);
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                } catch (IOException e) {
                }
            }
            catch(IOException e1)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

    @FXML public void mouseDragged(MouseEvent event){
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setX(event.getScreenX() + xoffset);
        stage.setY(event.getScreenY() + yoffset);
    }

    @FXML public void mousePressed(MouseEvent event){
        Stage stage = (Stage) mainPane.getScene().getWindow();
        xoffset = stage.getX() - event.getScreenX();
        yoffset = stage.getY() - event.getScreenY();
    }

    @FXML public void minimize(ActionEvent event){
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.CHECK);
        stage.setIconified(true);
    }

    @FXML public void close(ActionEvent event){
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.DISABLE);
        stage.close();
    }
}
