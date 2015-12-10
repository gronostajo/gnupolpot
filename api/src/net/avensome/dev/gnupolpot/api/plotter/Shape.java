package net.avensome.dev.gnupolpot.api.plotter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.util.FxUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A segment, polygon or polyline (series of connected segments).
 *
 * This class is final for the same reason as {@link PlotPoint}.
 */
public final class Shape implements Serializable {
    private static final double OPACITY_STROKE = 0.8;
    private static final double OPACITY_FILL = 0.5;

    private static final Color DEFAULT_SEGMENT_COLOR = Color.web("#222222");
    private static final Color DEFAULT_POLYGON_COLOR = Color.web("#888888");

    private final List<PlotPoint> points;

    private Color color;

    private Style style;

    /**
     * Create a shape from list of points, with given color and display style.
     * @param points list of shape points
     * @param color for line and fill (if filled)
     * @param style display style
     */
    public Shape(List<PlotPoint> points, Color color, Style style) {
        this.points = Collections.unmodifiableList(points);
        this.color = color;
        this.style = style;
    }

    /**
     * Convenience constructor that accepts points entered manually, not as a list.
     * @param color for line and fill (if filled)
     * @param style display style
     * @param points points to create shape from
     */
    public Shape(Color color, Style style, PlotPoint... points) {
        this(Arrays.asList(points), color, style);
    }

    /**
     * @return List of points in this polygon.
     */
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

    /**
     * See {@link Shape.Style}.
     * @return Display style.
     */
    public Style getStyle() {
        return style;
    }

    /**
     * Change display style. See {@link Shape.Style}.
     * @param style new style
     */
    public void setStyle(Style style) {
        this.style = style;
    }

    /**
     * Paints shape on a canvas. Used internally.
     * @param ctx graphics context to paint on
     * @param viewport current viewport
     */
    public void paint(GraphicsContext ctx, IViewport viewport) {
        ctx.setFill(FxUtils.applyOpacity(getColor(), (style == Style.FILLED) ? OPACITY_FILL : 0));
        ctx.setStroke(FxUtils.applyOpacity(getColor(), OPACITY_STROKE));

        if (points.size() > 2) {
            paintPolygon(ctx, viewport);
        } else if (points.size() == 2) {
            paintLine(ctx, viewport);
        }
    }

    private void paintPolygon(GraphicsContext ctx, IViewport viewport) {
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            Point screenPoint = viewport.toScreenCoords(points.get(i));
            x[i] = screenPoint.getX() + 0.5;
            y[i] = screenPoint.getY() + 0.5;
        }

        if (style != Style.LINE) {
            ctx.fillPolygon(x, y, points.size());
            ctx.strokePolygon(x, y, points.size());
        } else {
            ctx.strokePolyline(x, y, points.size());
        }
    }

    private void paintLine(GraphicsContext ctx, IViewport viewport) {
        Point a = viewport.toScreenCoords(points.get(0));
        Point b = viewport.toScreenCoords(points.get(1));
        ctx.strokeLine(a.getX() + 0.5, a.getY() + 0.5, b.getX() + 0.5, b.getY() + 0.5);
    }

    /**
     * Shape style.
     */
    public enum Style {
        /**
         * Polygon filled with a color.
         */
        FILLED,

        /**
         * Polygon without fill.
         */
        EMPTY,

        /**
         * Polyline, ie. an open chain of segments
         */
        LINE
    }
}
