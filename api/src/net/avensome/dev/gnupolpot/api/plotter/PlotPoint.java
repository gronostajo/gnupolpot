package net.avensome.dev.gnupolpot.api.plotter;

import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.util.FxUtils;

import java.io.Serializable;

public final class PlotPoint implements Serializable {
    /*
    This class is final because extending it is prone to introducing bugs.
    For example consider this case: you want to have some extra fields on PlotPoints. You create a class PointExt that
    extends PlotPoint and adds some fields. Then you play with it a bit and finally you change color of the point. But
    you accidentally change color the PointExt point instead of PlotPoint and it has no effect.
    The solution is to add original PlotPoint as PointExt's field, but then what's the point of extending PlotPoint
    if you can simply manipulate the underlying point?
     */

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

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void moveTo(double x, double y) {
        setX(x);
        setY(y);
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
    public String toString() {
        return String.format("%f : %f (%s)", x, y, FxUtils.colorToHex(color));
    }
}
