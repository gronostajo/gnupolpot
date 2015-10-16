package net.avensome.dev.gnupolpot.shapes;

import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.geometry.Point;

public class PlotPoint extends Point {
    private Color color;

    public PlotPoint(double x, double y) {
        super(x, y);
        this.color = Color.BLACK;
    }

    public PlotPoint(double x, double y, Color color) {
        this(x, y);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public PlotPoint movedBy(Point point) {
        return new PlotPoint(x + point.getX(), y + point.getY(), color);
    }
}
