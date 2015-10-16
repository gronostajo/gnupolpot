package net.avensome.dev.gnupolpot.shapes;

import net.avensome.dev.gnupolpot.geometry.Point;

import java.util.List;

public class Polygon extends Shape {
    private final List<Point> points;

    public Polygon(List<Point> points) {
        this.points = points;
    }

    @Override
    public PointSequence getSequence() {
        return new PointSequence(points);
    }
}
