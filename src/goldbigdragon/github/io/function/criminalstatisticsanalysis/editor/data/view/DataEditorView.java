package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.view;

import goldbigdragon.github.io.function.BaseView;

public class DataEditorView extends BaseView {
    public void view(){
        super.view("/resources/fxml/criminalstatisticsanalysis/dataEditor/dataEditorMain.fxml", "데이터 편집", 800, 800, true, false);
    }
}
