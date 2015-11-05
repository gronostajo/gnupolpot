package net.avensome.dev.gnupolpot.api.plotter;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class PlotPoint implements Serializable {
    private double x;
    private double y;
    private Color color;

    public PlotPoint(double x, double y) {
        this.x = x;
        this.y = y;
        this.color = Color.BLACK;
    }

    public PlotPoint(double x, double y, Color color) {
        this(x, y);
        this.color = color;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double distanceFrom(double x, double y) {
        double dX = Math.abs(this.x - x);
        double dY = Math.abs(this.y - y);
        return Math.sqrt(dX * dX + dY * dY);
    }

    public boolean hasEqualPoint(PlotPoint another) {
        return (another != null) && (x == another.x) && (y == another.y);
    }
}
