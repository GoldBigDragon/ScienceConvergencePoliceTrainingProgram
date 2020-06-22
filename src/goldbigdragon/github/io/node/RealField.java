package goldbigdragon.github.io.node;

import javafx.scene.control.TextField;

public class RealField extends TextField {

    @Override
    public void replaceText(int i, int i1, String string) {
        if(string.equals("-") && super.getText().indexOf("-") == -1)
            super.setText("-"+super.getText());
        else if(string.equals("+") && super.getText().indexOf("-") != -1)
            super.setText("0");
        else if(string.matches("[0-9]") || (string.matches("\\.") && super.getText().indexOf(".") == -1 )|| string.isEmpty()) {
            super.replaceText(i, i1, string);
        }
        if(super.getText().contains("-") && super.getText().charAt(0) != '-') {
            super.setText("-"+super.getText().replace("-", ""));
        }
    }

    @Override
    public void replaceSelection(String string) {
        super.replaceSelection(string);
    }

    public double getReal() {
        String value = super.getText().trim();
        if(value == null || value.length() < 1 || value.isEmpty())
            return 0;
        else
            return Double.parseDouble(value);
    }
}
