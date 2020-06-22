package goldbigdragon.github.io.function.criminalstatisticsanalysis.visualization.view;

import goldbigdragon.github.io.function.BaseView;

public class VisualizationTargetView extends BaseView {
    public void view(){
        super.view("/resources/fxml/criminalstatisticsanalysis/visualization/visualizationTargetSelect.fxml", "SCPTP - Visualization target select", 550, 600, true, false);
    }
}