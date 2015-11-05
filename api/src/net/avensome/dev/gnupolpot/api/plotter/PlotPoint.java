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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlotPoint point = (PlotPoint) o;

        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;

    }

    @SuppressWarnings("Duplicates")
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
