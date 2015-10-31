package net.avensome.dev.gnupolpot.core.geometry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class Point {
    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("%f : %f", x, y);
    }

    public Point minus(Point point) {
        return new Point(x - point.getX(), y - point.getY());
    }

    public Point scaled(double s) {
        return new Point(x * s, y * s);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(x)
                .append(y)
                .build();
    }

    @Override
    public boolean equals(Object another) {
        if (!(another instanceof Point)) {
            return false;
        } else if (another == this) {
            return true;
        }

        Point that = (Point) another;
        return new EqualsBuilder()
                .append(this.x, that.x)
                .append(this.y, that.y)
                .build();
    }
}
