package net.avensome.dev.gnupolpot.api.ui;

import javafx.scene.control.TextField;
import net.avensome.dev.gnupolpot.api.util.Validator;

public class DoubleTextField extends TextField {
    private final Validator<Double> validator;
    private String lastValidValue;

    public DoubleTextField(double initialValue) {
        this(initialValue, value -> true);
    }

    public DoubleTextField(double initialValue, Validator<Double> validator) {
        super(String.valueOf(initialValue));

        if (!validator.validate(initialValue)) {
            throw new IllegalArgumentException("Invalid initial value");
        }
        this.validator = validator;
        lastValidValue = String.valueOf(initialValue);

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
            double doubleValue = Double.parseDouble(replaced);
            return validator.validate(doubleValue);
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
