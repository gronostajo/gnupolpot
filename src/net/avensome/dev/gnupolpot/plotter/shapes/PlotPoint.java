package net.avensome.dev.gnupolpot.plotter.shapes;

import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.geometry.Point;

public class PlotPoint {
    private Point point;
    private Color color;

    public PlotPoint(double x, double y) {
        point = new Point(x, y);
        this.color = Color.BLACK;
    }

    public PlotPoint(double x, double y, Color color) {
        this(x, y);
        this.color = color;
    }

    public double getX() {
        return point.getX();
    }

    public double getY() {
        return point.getY();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public PlotPoint movedBy(Point delta) {
        return new PlotPoint(getX() + delta.getX(), getY() + delta.getY(), color);
    }

    public PlotPoint zoomed(Point offset, double factor) {
        double x = getX() + offset.getX();
        double y = getY() + offset.getY();
        return new PlotPoint(x * factor, y * factor);
    }

    public double distanceFrom(double x, double y) {
        double dX = Math.abs(point.getX() - x);
        double dY = Math.abs(point.getY() - y);
        return Math.sqrt(dX * dX + dY * dY);
    }

    public boolean hasEqualPoint(PlotPoint another) {
        return (another != null) && point.equals(another.point);
    }
}
