package net.avensome.dev.gnupolpot.api.control;

import javafx.scene.control.TextField;
import net.avensome.dev.gnupolpot.api.util.Validator;

public class IntTextField extends TextField {
    private final Validator<Integer> validator;
    private String lastValidValue;

    public IntTextField(int initialValue) {
        this(initialValue, value -> true);
    }

    public IntTextField(int initialValue, Validator<Integer> validator) {
        super(String.valueOf(initialValue));

        if (!validator.validate(initialValue)) {
            throw new IllegalArgumentException("Invalid initial value");
        }
        this.validator = validator;
        lastValidValue = String.valueOf(initialValue);

        textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidValue(newValue)) {
                lastValidValue = newValue;
            } else if (!newValue.equals("-")) {
                setText(oldValue);
            }
        });
    }

    public int getValue() {
        return Integer.valueOf(lastValidValue);
    }

    private boolean isValidValue(String value) {
        try {
            int intValue = Integer.parseInt(value);
            return validator.validate(intValue) && (intValue < Integer.MAX_VALUE);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
