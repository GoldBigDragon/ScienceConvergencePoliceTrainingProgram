package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.view;

import goldbigdragon.github.io.function.BaseView;

public class ModelEditorView extends BaseView {
    public void view(){
        super.view("/resources/fxml/criminalstatisticsanalysis/modelEditor/modelEditorMain.fxml", "모델 수정", 800, 800, true, false);
    }
}
