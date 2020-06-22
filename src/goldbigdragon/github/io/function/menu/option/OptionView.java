package goldbigdragon.github.io.function.menu.option;

import goldbigdragon.github.io.function.BaseView;

public class OptionView extends BaseView {
    public void view(){
        super.view("/resources/fxml/menu/option.fxml", "설정", 400, 500, true, false);
    }
}
