package net.avensome.dev.gnupolpot.plotter.shapes;

import com.sun.istack.internal.Nullable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.geometry.Point;

import java.util.List;
import java.util.stream.Collectors;

public class Shape {
    public static final double OPACITY_STROKE = 0.8;
    public static final double OPACITY_FILL = 0.5;

    public static final Color DEFAULT_SEGMENT_COLOR = Color.web("#222222");
    public static final Color DEFAULT_POLYGON_COLOR = Color.web("#888888");

    private final List<PlotPoint> points;

    @Nullable
    private final Color color;

    public Shape(List<PlotPoint> points, Color color) {
        this.points = points;
        this.color = color;
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

    public Shape zoomed(Point offset, double factor) {
        List<PlotPoint> newPoints = points.stream()
                .map(plotPoint -> plotPoint.zoomed(offset, factor))
                .collect(Collectors.toList());
        return new Shape(newPoints, color);
    }

    public void paint(GraphicsContext ctx) {
        ctx.setFill(applyOpacity(getColor(), OPACITY_FILL));
        ctx.setStroke(applyOpacity(getColor(), OPACITY_STROKE));

        if (points.size() > 2) {
            paintPolygon(ctx);
        } else {
            paintLine(ctx);
        }
    }

    private void paintPolygon(GraphicsContext ctx) {
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            PlotPoint point = points.get(i);
            x[i] = point.getX();
            y[i] = point.getY();
        }

        ctx.fillPolygon(x, y, points.size());
        ctx.strokePolygon(x, y, points.size());
    }

    private void paintLine(GraphicsContext ctx) {
        PlotPoint a = points.get(0);
        PlotPoint b = points.get(1);
        ctx.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
    }

    private Color applyOpacity(Color color, double opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity * color.getOpacity());
    }
}
