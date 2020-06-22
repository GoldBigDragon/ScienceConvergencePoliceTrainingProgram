package goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.view;

import goldbigdragon.github.io.function.BaseView;

public class VisualizationMainView extends BaseView {
    public void view(){
        super.view("/resources/fxml/criminalstatisticsanalysis/visualization/visualizationMain.fxml", "SCPTP - Visualization", 1000, 800, true, false);
    }
}