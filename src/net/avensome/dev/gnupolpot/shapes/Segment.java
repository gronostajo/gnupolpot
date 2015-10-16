package net.avensome.dev.gnupolpot.shapes;

import com.google.common.collect.ImmutableList;
import net.avensome.dev.gnupolpot.geometry.Point;

public class Segment extends Shape {
    private final Point a;
    private final Point b;

    public Segment(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Shape.PointSequence getSequence() {
        return new PointSequence(ImmutableList.of(a, b));
    }
}
