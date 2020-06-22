package goldbigdragon.github.io.function.alarm.controller;

import goldbigdragon.github.io.function.alarm.enums.AfterWorkType;
import goldbigdragon.github.io.function.alarm.enums.BeforeWorkType;
import goldbigdragon.github.io.enums.SoundType;
import goldbigdragon.github.io.function.BaseController;
import goldbigdragon.github.io.util.SoundUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class ProgressController extends BaseController {
    @FXML private Label title;

    private String titleText;
    private List<BeforeWorkType> beforeWorkList;
    private List<AfterWorkType> afterWorkList;

    public static String param;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Thread t = new Thread(){
            @Override
            public void run() {
                for(BeforeWorkType bwt : beforeWorkList) {
                    if(bwt == BeforeWorkType.CREATE_CRIMINALSTATISTICSANALYSIS_DEFAULT_MODEL) {
                        new CriminalStatisticsAnalysisProgress().createDefaultModel();
                    } else if(bwt == BeforeWorkType.OVERWRITE_CRIMINALSTATISTICSANALYSIS_MODEL) {
                        new CriminalStatisticsAnalysisProgress().overwriteModel();
                    } else if(bwt == BeforeWorkType.LOAD_CRIMINALSTATISTICSANALYSIS_DATABASE) {
                        new CriminalStatisticsAnalysisProgress().loadData(param);
                    } else if(bwt == BeforeWorkType.CREATE_FLUCTUATED_DATA) {
                        new CriminalStatisticsAnalysisProgress().createFluctuatedData(param);
                    } else if(bwt == BeforeWorkType.CALCULATE_CRIMINALSTATISTICSANALYSIS) {
                        new CriminalStatisticsAnalysisProgress().calculateCriminalStatisticsAnalysisVisualization(param);
                        closeProgressWindow();
                    }
                }
                afterr();
                interrupt();
            }
        };
        Platform.runLater(()->{
            this.title.textProperty().setValue(titleText);
            t.start();
        });
    }

    public void setText(String title) {
        this.titleText = title;
    }

    public void setProces(List<BeforeWorkType> beforeWorkList, List<AfterWorkType> afterWorkList) {
        this.beforeWorkList = beforeWorkList;
        this.afterWorkList = afterWorkList;
    }

    public void afterr(){
        for(AfterWorkType awt : afterWorkList) {
            if(awt == AfterWorkType.OPEN_CRIMINALSTATISTICSANALYSIS_MAIN) {
                new CriminalStatisticsAnalysisProgress().openCriminalStatisticsAnalysisMain();
                closeProgressWindow();
            } else if(awt == AfterWorkType.OPEN_CRIMINALSTATISTICSANALYSIS_DATA_EDITOR) {
                new CriminalStatisticsAnalysisProgress().openCriminalStatisticsAnalysisDataEditor();
                closeProgressWindow();
            }
        }
        param = null;
    }

    public void closeProgressWindow(){
        Platform.runLater(()->{
            Stage stage = (Stage) mainPane.getScene().getWindow();
            SoundUtil.playSound(SoundType.DISABLE);
            stage.close();
        });
    }
}
