package net.avensome.dev.gnupolpot.api.plotter;

import com.sun.istack.internal.Nullable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.util.FxUtils;

import java.io.Serializable;
import java.util.List;

public class Shape implements Serializable {
    public static final double OPACITY_STROKE = 0.8;
    public static final double OPACITY_FILL = 0.5;

    public static final Color DEFAULT_SEGMENT_COLOR = Color.web("#222222");
    public static final Color DEFAULT_POLYGON_COLOR = Color.web("#888888");

    private final List<PlotPoint> points;


    @Nullable
    private Color color;

    private Type type;

    public Shape(List<PlotPoint> points, Color color, Type type) {
        this.points = points;
        this.color = color;
        this.type = type;
    }

    public List<PlotPoint> getPoints() {
        return points;
    }

    public Color getColor() {
        if (color != null) {
            return color;
        } else if (points.size() == 2) {
            return DEFAULT_SEGMENT_COLOR;
        } else {
            return DEFAULT_POLYGON_COLOR;
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void paint(GraphicsContext ctx, Viewport viewport) {
        ctx.setFill(FxUtils.applyOpacity(getColor(), (type == Type.FILLED) ? OPACITY_FILL : 0));
        ctx.setStroke(FxUtils.applyOpacity(getColor(), OPACITY_STROKE));

        if (points.size() > 2) {
            paintPolygon(ctx, viewport);
        } else if (points.size() == 2) {
            paintLine(ctx, viewport);
        }
    }

    private void paintPolygon(GraphicsContext ctx, Viewport viewport) {
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            Point screenPoint = viewport.toScreenCoords(points.get(i));
            x[i] = screenPoint.getX();
            y[i] = screenPoint.getY();
        }

        if (type != Type.LINE) {
            ctx.fillPolygon(x, y, points.size());
            ctx.strokePolygon(x, y, points.size());
        } else {
            ctx.strokePolyline(x, y, points.size());
        }
    }

    private void paintLine(GraphicsContext ctx, Viewport viewport) {
        Point a = viewport.toScreenCoords(points.get(0));
        Point b = viewport.toScreenCoords(points.get(1));
        ctx.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
    }

    public enum Type {
        FILLED,
        EMPTY,
        LINE
    }
}
