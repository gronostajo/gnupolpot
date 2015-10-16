package net.avensome.dev.gnupolpot.plotter.shapes;

import com.google.common.collect.ImmutableList;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.geometry.Point;

public class Segment extends Shape {
    private final PlotPoint a;
    private final PlotPoint b;

    public Segment(PlotPoint a, PlotPoint b, Color color) {
        super(color);
        this.a = a;
        this.b = b;
    }

    @Override
    public Shape.PointSequence getSequence() {
        return new PointSequence(ImmutableList.of(a, b));
    }

    @Override
    public Shape movedBy(Point delta) {
        return new Segment(a.movedBy(delta), b.movedBy(delta), getColor());
    }
}
