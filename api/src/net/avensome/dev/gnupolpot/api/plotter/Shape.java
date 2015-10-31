package net.avensome.dev.gnupolpot.api.plotter;

import com.sun.istack.internal.Nullable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

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

    public Shape zoomed(double offsetX, double offsetY, double factor) {
        List<PlotPoint> newPoints = points.stream()
                .map(plotPoint -> plotPoint.zoomed(offsetX, offsetY, factor))
                .collect(Collectors.toList());
        return new Shape(newPoints, color, type);
    }

    public void paint(GraphicsContext ctx, double viewportHeight) {
        ctx.setFill(applyOpacity(getColor(), (type == Type.FILLED) ? OPACITY_FILL : 0));
        ctx.setStroke(applyOpacity(getColor(), OPACITY_STROKE));

        if (points.size() > 2) {
            paintPolygon(ctx, viewportHeight);
        } else if (points.size() == 2) {
            paintLine(ctx, viewportHeight);
        }
    }

    private void paintPolygon(GraphicsContext ctx, double viewportHeight) {
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            PlotPoint point = points.get(i);
            x[i] = point.getX();
            y[i] = viewportHeight - point.getY();
        }

        if (type != Type.LINE) {
            ctx.fillPolygon(x, y, points.size());
            ctx.strokePolygon(x, y, points.size());
        } else {
            ctx.strokePolyline(x, y, points.size());
        }
    }

    private void paintLine(GraphicsContext ctx, double viewportHeight) {
        PlotPoint a = points.get(0);
        PlotPoint b = points.get(1);
        ctx.strokeLine(a.getX(), viewportHeight - a.getY(), b.getX(), viewportHeight - b.getY());
    }

    private Color applyOpacity(Color color, double opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity * color.getOpacity());
    }

    public enum Type {
        FILLED,
        EMPTY,
        LINE
    }
}
