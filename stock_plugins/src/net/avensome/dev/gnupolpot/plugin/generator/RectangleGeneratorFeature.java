package net.avensome.dev.gnupolpot.plugin.generator;

import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.ui.DoubleTextField;
import net.avensome.dev.gnupolpot.api.ui.IntTextField;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.ui.LabeledControlGridBuilder;

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
    public void execute(Api api) {
        Dialog<RectangleGeneratorParams> dialog = new Dialog<>();
        dialog.setTitle("Generate points in rectangle");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        IntTextField countField = new IntTextField(lastParams.getCount(), value -> value > 0);
        DoubleTextField leftField = new DoubleTextField(lastParams.getLeft());
        DoubleTextField topField = new DoubleTextField(lastParams.getTop());
        DoubleTextField rightField = new DoubleTextField(lastParams.getRight());
        DoubleTextField bottomField = new DoubleTextField(lastParams.getBottom());
        ColorPicker colorPicker = new ColorPicker(lastParams.getColor());
        IntTextField seedField = new IntTextField(lastParams.getSeed());

        GridPane grid = new LabeledControlGridBuilder(120, 160)
                .add("Count", countField)
                .add("Left", leftField)
                .add("Top", topField)
                .add("Right", rightField)
                .add("Bottom", bottomField)
                .add("Color", colorPicker)
                .add("Seed (0 = random)", seedField)
                .build();

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

            IPlotter plotter = api.getPlotter();
            plotter.getActiveLayer().getPoints().addAll(points);
            plotter.requestRepaint();
            
            api.setStatus(String.format("%d points generated", points.size()));
        }
    }
}
