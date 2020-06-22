package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.view;

import goldbigdragon.github.io.function.BaseView;

public class DataCreatorView extends BaseView {
    public void view(){
        super.view("/resources/fxml/criminalstatisticsanalysis/dataEditor/dataCreatorMain.fxml", "임의 데이터 생성", 800, 800, true, false);
    }
}
