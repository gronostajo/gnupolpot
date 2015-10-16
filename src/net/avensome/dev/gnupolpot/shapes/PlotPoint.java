package net.avensome.dev.gnupolpot.shapes;

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
}
