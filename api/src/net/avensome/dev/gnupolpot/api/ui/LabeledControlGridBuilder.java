package net.avensome.dev.gnupolpot.api.ui;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Makes building multiple row label-field pairs easier. First prepare labels and fields, then add them. Builder takes
 * care of the rest and returns a single control containing all labels and fields nicely laid out.
 */
public class LabeledControlGridBuilder {
    private final Integer labelWidth;
    private final Integer controlWidth;
    private final List<Pair<String, Control>> pairs;

    /**
     * Initialize the builder with automatic column widths.
     */
    public LabeledControlGridBuilder() {
        this(null, null);
    }

    /**
     * Initialize the builder with defined column widths.
     * @param labelWidth first column's width
     * @param controlWidth second column's width
     */
    public LabeledControlGridBuilder(Integer labelWidth, Integer controlWidth) {
        this(labelWidth, controlWidth, new LinkedList<>());
    }

    private LabeledControlGridBuilder(Integer labelWidth, Integer controlWidth, List<Pair<String, Control>> pairs) {
        this.labelWidth = labelWidth;
        this.controlWidth = controlWidth;
        this.pairs = pairs;
    }

    /**
     * Add a next row.
     * @param label a control in the first column (usually a label)
     * @param field a control in the second column (usually a field)
     * @return This builder object. (for chaining)
     */
    public LabeledControlGridBuilder append(String label, Control field) {
        List<Pair<String, Control>> newPairs = new ArrayList<>(pairs.size() + 1);
        newPairs.addAll(pairs);
        newPairs.add(new Pair<>(label, field));
        return new LabeledControlGridBuilder(labelWidth, controlWidth, newPairs);
    }

    /**
     * @return A pane containing all appended labels and fields.
     */
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
