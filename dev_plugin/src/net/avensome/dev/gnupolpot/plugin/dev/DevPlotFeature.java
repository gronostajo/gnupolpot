package net.avensome.dev.gnupolpot.plugin.dev;

import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.plotter.Shape;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DevPlotFeature implements Feature {
    @Override
    public String getMenuItem() {
        return "Add dev points";
    }

    @Override
    public String getDescription() {
        return "Inserts some points and shapes";
    }

    @Override
    public void execute(Api api) {
        IPlotter plotter = api.getPlotter();

        Collection<PlotPoint> points = plotter.getActiveLayer().getPoints();
        Collection<Shape> shapes = plotter.getActiveLayer().getShapes();

        points.add(new PlotPoint(0, 0));
        points.add(new PlotPoint(5, 5, Color.RED));

        List<PlotPoint> filledShapePoints = Arrays.asList(
                new PlotPoint(30, 20),
                new PlotPoint(40, 30),
                new PlotPoint(35, 50),
                new PlotPoint(20, 50),
                new PlotPoint(15, 40)
        );
        points.addAll(filledShapePoints);
        shapes.add(new Shape(filledShapePoints, Color.GREEN, Shape.Style.FILLED));

        List<PlotPoint> emptyShapePoints = Arrays.asList(
                new PlotPoint(60, 20),
                new PlotPoint(70, 50),
                new PlotPoint(50, 80),
                new PlotPoint(40, 60)
        );
        points.addAll(emptyShapePoints);
        shapes.add(new Shape(emptyShapePoints, Color.BLUE, Shape.Style.EMPTY));

        List<PlotPoint> lineShapePoints = Arrays.asList(
                new PlotPoint(140, 20),
                new PlotPoint(180, 70),
                new PlotPoint(150, 80)
        );
        points.addAll(lineShapePoints);
        shapes.add(new Shape(lineShapePoints, Color.HOTPINK, Shape.Style.LINE));

        List<PlotPoint> linePoints = Arrays.asList(
                new PlotPoint(80, 70),
                new PlotPoint(90, 90)
        );
        points.addAll(linePoints);
        shapes.add(new Shape(linePoints, Color.PINK, Shape.Style.FILLED));

        plotter.requestRepaint();
    }
}
