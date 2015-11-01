package net.avensome.dev.gnupolpot.plugin.generator;

import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.control.DoubleTextField;
import net.avensome.dev.gnupolpot.api.control.IntTextField;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RectangleGeneratorFeature implements Feature {
    RectangleGeneratorParams lastParams = new RectangleGeneratorParams();

    @Override
    public String getMenuItem() {
        return "Generate points\\In rectangle...";
    }

    @Override
    public String getDescription() {
        return "Generate points randomly distributed in a bounded rectangle.";
    }

    @Override
    public String execute(IPlotter plotter) {
        Dialog<RectangleGeneratorParams> dialog = new Dialog<>();
        dialog.setTitle("Generate points in rectangle");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        IntTextField countField = new IntTextField(lastParams.getCount(), value -> value > 0);
        DoubleTextField leftField = new DoubleTextField(lastParams.getLeft());
        DoubleTextField topField = new DoubleTextField(lastParams.getTop());
        DoubleTextField rightField = new DoubleTextField(lastParams.getRight());
        DoubleTextField bottomField = new DoubleTextField(lastParams.getBottom());
        ColorPicker colorPicker = new ColorPicker(lastParams.getColor());
        IntTextField seedField = new IntTextField(lastParams.getSeed());

        countField.setPrefWidth(120);
        leftField.setPrefWidth(120);
        topField.setPrefWidth(120);
        rightField.setPrefWidth(120);
        bottomField.setPrefWidth(120);
        colorPicker.setPrefWidth(120);
        seedField.setPrefWidth(120);

        Label countLabel = new Label("Count");
        Label leftLabel = new Label("Left");
        Label topLabel = new Label("Top");
        Label rightLabel = new Label("Right");
        Label bottomLabel = new Label("Bottom");
        Label colorLabel = new Label("Color");
        Label seedLabel = new Label("Seed (0 = random)");

        countLabel.setPrefWidth(120);
        leftLabel.setPrefWidth(120);
        topLabel.setPrefWidth(120);
        rightLabel.setPrefWidth(120);
        bottomLabel.setPrefWidth(120);
        colorLabel.setPrefWidth(120);
        seedLabel.setPrefWidth(120);

        grid.add(countLabel, 0, 0);
        grid.add(countField, 1, 0);
        grid.add(leftLabel, 0, 1);
        grid.add(leftField, 1, 1);
        grid.add(topLabel, 0, 2);
        grid.add(topField, 1, 2);
        grid.add(rightLabel, 0, 3);
        grid.add(rightField, 1, 3);
        grid.add(bottomLabel, 0, 4);
        grid.add(bottomField, 1, 4);
        grid.add(colorLabel, 0, 5);
        grid.add(colorPicker, 1, 5);
        grid.add(seedLabel, 0, 6);
        grid.add(seedField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                int count = countField.getValue();
                double left = leftField.getValue();
                double top = topField.getValue();
                double right = rightField.getValue();
                double bottom = bottomField.getValue();
                Color color = colorPicker.getValue();
                int seed = seedField.getValue();
                return new RectangleGeneratorParams(count, left, top, right, bottom, color, seed);
            } else {
                return null;
            }
        });

        Optional<RectangleGeneratorParams> result = dialog.showAndWait();

        if (result.isPresent()) {
            RectangleGeneratorParams params = result.get();
            lastParams = params;

            Random random = (params.getSeed() == 0) ? new Random() : new Random(params.getSeed());
            List<PlotPoint> points = new ArrayList<>(params.getCount());
            for (int i = 0; i < params.getCount(); i++) {
                double x = params.getLeft() + random.nextDouble() * params.getWidth();
                double y = params.getBottom() + random.nextDouble() * params.getHeight();
                points.add(new PlotPoint(x, y, params.getColor()));
            }

            plotter.getPoints().addAll(points);
            plotter.requestRepaint();
            
            return String.format("%d points generated", points.size());
        } else {

            return null;
        }
    }
}
