package goldbigdragon.github.io.function.menu.main;

import goldbigdragon.github.io.function.alarm.enums.AfterWorkType;
import goldbigdragon.github.io.function.alarm.enums.BeforeWorkType;
import goldbigdragon.github.io.enums.SoundType;
import goldbigdragon.github.io.function.BaseController;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import goldbigdragon.github.io.function.alarm.view.ProgressView;
import goldbigdragon.github.io.function.menu.information.InformationView;
import goldbigdragon.github.io.function.menu.option.OptionView;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainController extends BaseController {
    @FXML private Label title;
    @FXML private Label description;

    public static InformationView informationView = null;
    public static OptionView optionView = null;

    @FXML private void criminalStatisticsAnalysisMouseEnter(){
        setDescription("[범죄 통계 분석]", "범죄 자료와 도시 구역이 매핑된 자료들을 바탕으로,\r\n제한된 자원을 이용하여 도시 치안을 최대한 향상시키는 것이 목표입니다.");
    }

    @FXML private void routeExpectingMouseEnter(){
        setDescription("[이동 경로 예상]","추적 대상의 평상시 이동 경로 자료를 바탕으로, 최적의 매복 지점을 선정하는 것이 목표입니다.");
    }

    @FXML private void blockEscapeRouteMouseEnter(){
        setDescription("[도주 경로 차단 시뮬레이션]","제한된 자원을 이용하여 범인의 도주 경로를 사전에 차단하는 것이 목표입니다.");
    }

    @FXML private void illegalJudgmentQuizMouseEnter(){
        setDescription("[위법 판단 퀴즈]","임의의 사건에 대한 위법 여부를 논리적으로 생각하며 사건에 대한 판단력을 기르는 것이 목표입니다.");
    }

    @FXML private void mouseExited(){
        setDescription("아이콘 위에 마우스를 올려 보세요!","학습 목표 및 간단한 설명을 볼 수 있습니다!");
    }

    @FXML private void criminalStatisticsAnalysisOpen(){
        if(informationView != null) {
            informationView.stage.close();
            informationView = null;
        }
        if(optionView != null) {
            optionView.stage.close();
            optionView = null;
        }
        Stage stage = (Stage) mainPane.getScene().getWindow();
        SoundUtil.playSound(SoundType.DISABLE);
        stage.close();

        List<BeforeWorkType> beforeWork = new ArrayList<>();
        beforeWork.add(BeforeWorkType.CREATE_CRIMINALSTATISTICSANALYSIS_DEFAULT_MODEL);
        List<AfterWorkType> afterWork = new ArrayList<>();
        afterWork.add(AfterWorkType.OPEN_CRIMINALSTATISTICSANALYSIS_MAIN);
        goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController.visualizationTarget = new ArrayList<>();
        new ProgressView().view("기본 모델을 생성 중입니다..!", beforeWork, afterWork);
    }

    @FXML private void shutdown(){
        SoundUtil.playSound(SoundType.DISABLE);
        System.exit(0);
    }

    @FXML private void github(){
        try {
            SoundUtil.playSound(SoundType.ENABLE);
            Desktop.getDesktop().browse(new URI("http://goldbigdragon.github.io/"));
        } catch(Exception e) {
            new AlarmAPI().connectionError();
        }
    }

    @FXML private void informationOpen(){
        if(informationView == null) {
            SoundUtil.playSound(SoundType.ENABLE);
            informationView = new InformationView();
            informationView.view();
        } else {
            informationView.stage.toFront();
        }
    }

    @FXML private void settingsOpen(){
        if(optionView == null) {
            SoundUtil.playSound(SoundType.ENABLE);
            optionView = new OptionView();
            optionView.view();
        } else {
            optionView.stage.toFront();
        }
    }

    private void setDescription(String title, String description) {
        this.title.setText(title);
        this.description.setText(description);
    }
}
