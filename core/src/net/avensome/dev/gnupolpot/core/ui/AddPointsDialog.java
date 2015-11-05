package net.avensome.dev.gnupolpot.core.ui;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.control.DoubleTextField;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;

import java.util.Optional;

public class AddPointsDialog {
    public PlotPoint show() {
        Dialog<PlotPoint> dialog = new Dialog<>();
        dialog.setTitle("Add point");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label xLabel = new Label("X");
        Label yLabel = new Label("Y");
        Label colorLabel = new Label("Color");

        DoubleTextField xField = new DoubleTextField(0);
        DoubleTextField yField = new DoubleTextField(0);
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);

        xLabel.setPrefWidth(60);
        yLabel.setPrefWidth(60);
        colorLabel.setPrefWidth(60);
        xField.setPrefWidth(120);
        yField.setPrefWidth(120);
        colorPicker.setPrefWidth(120);

        grid.add(xLabel, 0, 0);
        grid.add(xField, 1, 0);
        grid.add(yLabel, 0, 1);
        grid.add(yField, 1, 1);
        grid.add(colorLabel, 0, 2);
        grid.add(colorPicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(xField::requestFocus);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                double x = xField.getValue();
                double y = yField.getValue();
                Color color = colorPicker.getValue();
                return new PlotPoint(x, y, color);
            } else {
                return null;
            }
        });

        Optional<PlotPoint> result = dialog.showAndWait();
        return result.isPresent() ? result.get() : null;
    }
}
