package goldbigdragon.github.io.function.menu.option;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.SoundType;
import goldbigdragon.github.io.function.BaseController;
import goldbigdragon.github.io.function.menu.main.MainController;
import goldbigdragon.github.io.util.ConfigUtil;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class OptionController extends BaseController {
    @FXML private Button soundOnOff;
    @FXML private Button languageChange;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            if(Main.mainVariables.playSound) {
                soundOnOff.setId("soundOn");
            } else {
                soundOnOff.setId("soundOff");
            }
            if(Main.mainVariables.language == 'K') {
                languageChange.setId("languageKo");
            } else if(Main.mainVariables.language == 'E') {
                languageChange.setId("languageEn");
            }
        });
    }

    @Override
    @FXML public void close(ActionEvent event){
        MainController.optionView = null;
        super.close(event);
    }

    @FXML private void turnSound(){
        if(Main.mainVariables.playSound) {
            soundOnOff.setId("soundOff");
            Main.mainVariables.playSound = false;
        } else {
            soundOnOff.setId("soundOn");
            Main.mainVariables.playSound = true;
            SoundUtil.playSound(SoundType.CHECK);
        }
        saveSettings();
    }

    @FXML private void changeLanguage(){
        SoundUtil.playSound(SoundType.CHECK);
        if(Main.mainVariables.language == 'K') {
            languageChange.setId("languageEn");
            Main.mainVariables.language = 'E';
        } else {
            languageChange.setId("languageKo");
            Main.mainVariables.language = 'K';
        }
        saveSettings();
    }
    private void saveSettings() {
        new ConfigUtil().save();
    }
}
