package net.avensome.dev.gnupolpot.plotter.shapes;

import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.geometry.Point;

import java.util.List;
import java.util.stream.Collectors;

public class Polygon extends Shape {
    private final List<PlotPoint> points;

    public Polygon(List<PlotPoint> points, Color color) {
        super(color);
        this.points = points;
    }

    @Override
    public PointSequence getSequence() {
        return new PointSequence(points);
    }

    @Override
    public Shape movedBy(Point delta) {
        List<PlotPoint> newPoints = points.stream()
                .map(plotPoint -> plotPoint.movedBy(delta))
                .collect(Collectors.toList());
        return new Polygon(newPoints, color);
    }
}
