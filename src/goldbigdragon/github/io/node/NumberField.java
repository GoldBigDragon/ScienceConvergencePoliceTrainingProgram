package goldbigdragon.github.io.node;

import javafx.scene.control.TextField;

public class NumberField extends TextField {

    private int intMin = Integer.MIN_VALUE;
    private int intMax = Integer.MAX_VALUE;
    private long longMin = Long.MIN_VALUE;
    private long longMax = Long.MAX_VALUE;

    @Override
    public void replaceText(int i, int i1, String inputted) {
        String oldText = super.getText();
        if(inputted.equals("-") && oldText.indexOf("-") == -1) {
            super.setText("-" + super.getText());
        }
        else if(inputted.equals("+") && oldText.indexOf("-") != -1) {
            super.setText("0");
        }
        else if(inputted.matches("[0-9]") || inputted.isEmpty()) {
            super.replaceText(i, i1, inputted);
        }
        if(super.getText().contains("-") && oldText.charAt(0) != '-') {
            super.setText("-"+oldText.replace("-", ""));
        }
        oldText = super.getText();
        if(oldText != null && oldText.length() > 0) {
            if(Integer.parseInt(super.getText()) < intMin) {
                super.setText(Integer.toString(intMin));
            }
            if(Integer.parseInt(super.getText()) > intMax) {
                super.setText(Integer.toString(intMax));
            }
            if(Long.parseLong(super.getText()) < longMin) {
                super.setText(Long.toString(longMin));
            }
            if(Long.parseLong(super.getText()) > longMax) {
                super.setText(Long.toString(longMax));
            }
        }
    }

    @Override
    public void replaceSelection(String string) {
        super.replaceSelection(string);
    }

    public int getNumber() {
        String value = super.getText().trim();
        if(value == null || value.length() < 1 || value.isEmpty())
            return intMin;
        else {
            if(value.indexOf(0) == '-') {
                if(value.length() > Integer.toString(intMin).length()) {
                    return intMin;
                } else {
                    if(Long.parseLong(value) > intMin) {
                        return Integer.parseInt(value);
                    } else {
                        return intMin;
                    }
                }
            } else {
                if(value.length() > Integer.toString(intMax).length()) {
                    return intMax;
                } else {
                    if(Long.parseLong(value) < intMax) {
                        return Integer.parseInt(value);
                    } else {
                        return intMax;
                    }
                }
            }
        }
    }

    public long getNumberLong() {
        String value = super.getText().trim();
        if(value == null || value.length() < 1 || value.isEmpty())
            return longMin;
        else {
            if(value.indexOf(0) == '-') {
                if(value.length() > Long.toString(longMin).length()) {
                    return longMin;
                } else {
                    if(Long.parseLong(value) > longMin) {
                        return Long.parseLong(value);
                    } else {
                        return longMin;
                    }
                }
            } else {
                if(value.length() > Long.toString(longMax).length()) {
                    return longMax;
                } else {
                    if(Long.parseLong(value) < longMax) {
                        return Long.parseLong(value);
                    } else {
                        return longMax;
                    }
                }
            }
        }
    }

    public void setMinMax(int intMin, int intMax, long longMin, long longMax){
        this.intMin = intMin;
        this.intMax = intMax;
        this.longMin = longMin;
        this.longMax = longMax;
    }

}
