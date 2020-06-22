package goldbigdragon.github.io.function.menu.information;

import goldbigdragon.github.io.function.BaseView;

public class InformationView extends BaseView {
    public void view(){
        super.view("/resources/fxml/menu/information.fxml", "도움말", 400, 600, true, false);
    }
}
