package goldbigdragon.github.io.function.menu.information;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.SoundType;
import goldbigdragon.github.io.function.BaseController;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import goldbigdragon.github.io.function.menu.main.MainController;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class InformationController extends BaseController {

    @FXML private Label version;
    @FXML private Label updateDate;

    @FXML private Label image;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
//            try {
//                InputStreamReader inputStreamReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("resources/guide.json"), "UTF8");
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String readedLine;
//                StringBuilder sb = new StringBuilder();
//                while((readedLine=bufferedReader.readLine())!=null)
//                    sb.append(readedLine);
//                bufferedReader.close();
//                inputStreamReader.close();
//                JsonParser jp = new JsonParser();
//                JsonArray ja = jp.parse(sb.toString()).getAsJsonArray();
//                JsonObject jo = null;
//                for(int count = 0; count < ja.size(); count++) {
//                    jo = ja.get(count).getAsJsonObject();
//                    titleList.add(jo.get("title").getAsString());
//                    descriptionList.add(jo.get("description").getAsString());
//                    imageUrlList.add(jo.get("image").getAsString());
//                }
//            } catch(IOException e) {
//                e.printStackTrace();
//                System.out.println("[도움말] : 도움말 파일을 불러오는데 실패하였습니다!");
//            }
            version.textProperty().setValue(Main.version);
            updateDate.textProperty().setValue(Main.lastUpdate);
        });
    }

    @FXML
    private void openYsu() {
        try {
            SoundUtil.playSound(SoundType.ENABLE);
            Desktop.getDesktop().browse(new URI("http://ysu.ac.kr/"));
        } catch(Exception e) {
            new AlarmAPI().connectionError();
        }
    }

    @FXML
    private void openCafe() {
        try {
            SoundUtil.playSound(SoundType.ENABLE);
            Desktop.getDesktop().browse(new URI("https://cafe.naver.com/goldbigdragon"));
        } catch(Exception e) {
            new AlarmAPI().connectionError();
        }
    }

    @Override
    @FXML public void close(ActionEvent event){
        super.close(event);
        MainController.informationView = null;
    }
}
