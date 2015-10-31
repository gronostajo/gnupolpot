package net.avensome.dev.gnupolpot.core.control;

import javafx.scene.control.TextField;

public class NumberTextField extends TextField {
    private String lastValidValue = "0";

    public NumberTextField() {
        super("0");
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidValue(newValue)) {
                lastValidValue = newValue;
                setIfNeeded(newValue);
            } else if (!newValue.equals("-")) {
                setText(oldValue);
            }
        });
    }

    public double getValue() {
        return Double.valueOf(lastValidValue);
    }

    private boolean isValidValue(String value) {
        String replaced = normalizeDecimalPoint(value);
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(replaced);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void setIfNeeded(String value) {
        String replaced = normalizeDecimalPoint(value);
        if (!value.equals(replaced)) {
            setText(replaced);
        }
    }

    private String normalizeDecimalPoint(String value) {
        return value.replace(',', '.');
    }
}
