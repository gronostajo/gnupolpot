package net.avensome.dev.gnupolpot.api.util;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LabeledControlGridBuilder {
    private final Integer labelWidth;
    private final Integer controlWidth;
    private final List<Pair<String, Control>> pairs;

    public LabeledControlGridBuilder() {
        this(null, null);
    }

    public LabeledControlGridBuilder(Integer labelWidth, Integer controlWidth) {
        this(labelWidth, controlWidth, new LinkedList<>());
    }

    public LabeledControlGridBuilder(Integer labelWidth, Integer controlWidth, List<Pair<String, Control>> pairs) {
        this.labelWidth = labelWidth;
        this.controlWidth = controlWidth;
        this.pairs = pairs;
    }

    public LabeledControlGridBuilder add(String fieldLabel, Control control) {
        List<Pair<String, Control>> newPairs = new ArrayList<>(pairs.size() + 1);
        newPairs.addAll(pairs);
        newPairs.add(new Pair<>(fieldLabel, control));
        return new LabeledControlGridBuilder(labelWidth, controlWidth, newPairs);
    }

    public GridPane build() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        int rowIndex = 0;
        for (Pair<String, Control> pair : pairs) {
            Label label = new Label(pair.getKey());
            if (labelWidth != null) {
                label.setPrefWidth(labelWidth);
            }
            Control control = pair.getValue();
            if (controlWidth != null) {
                control.setPrefWidth(controlWidth);
            }

            grid.add(label, 0, rowIndex);
            grid.add(control, 1, rowIndex);
            rowIndex++;
        }

        return grid;
    }
}
