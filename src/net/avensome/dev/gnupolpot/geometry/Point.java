package net.avensome.dev.gnupolpot.geometry;

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
}
