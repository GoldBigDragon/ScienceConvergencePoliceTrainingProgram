package goldbigdragon.github.io.function.criminalstatisticsanalysis.main.view;

import goldbigdragon.github.io.function.BaseView;

public class MainView extends BaseView {
    public void view(){
        super.view("/resources/fxml/criminalstatisticsanalysis/main.fxml", "SCPTP - Criminal Statistics Analysis", 1000, 800, true, false);
    }
}