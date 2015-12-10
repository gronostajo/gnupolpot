package net.avensome.dev.gnupolpot.api.plotter;

import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.util.FxUtils;

import java.io.Serializable;

/**
 * <p>Represents a single point on the plot.
 *
 * <p>This class is final because extending it is prone to introducing bugs.
 *
 * <p> For example consider this case: you want to have some extra fields on PlotPoints. You create a class PointExt
 * that extends PlotPoint and adds some fields. Then you play with it a bit and finally you change color of the point.
 * But you accidentally change color the PointExt point instead of PlotPoint and it has no effect.
 *
 * <p>The solution is to add original PlotPoint as PointExt's field, but then what's the point of extending PlotPoint if
 * you can simply manipulate the underlying point?
 */
public final class PlotPoint implements Serializable {
    private double x;
    private double y;
    private Color color;

    /**
     * Creates point with default color.
     * @param x X coordinate
     * @param y Y coordinate
     */
    public PlotPoint(double x, double y) {
        this(x, y, Color.BLACK);
    }

    /**
     * @param x X coordinate
     * @param y Y coordinate
     * @param color point color
     */
    public PlotPoint(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    /**
     * @return X coordinate of this point.
     */
    public double getX() {
        return x;
    }

    /**
     * Change X coordinate of this point.
     * @param x new X coordinate value
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return Y coordinate of this point.
     */
    public double getY() {
        return y;
    }

    /**
     * Change Y coordinate of this point.
     * @param y new Y coordinate value
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Change both coordinates of this point at once.
     * @param x new X coordinate value
     * @param y new Y coordinate value
     */
    public void moveTo(double x, double y) {
        setX(x);
        setY(y);
    }

    /**
     * @return Displayed color of this point.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Change this point's color.
     * @param color new color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Calculate distance from this point to provided coordinate.
     * @param x X coordinate of the second point
     * @param y Y coordinate of the second point
     * @return Distance between this point and provided coordinates.
     */
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
