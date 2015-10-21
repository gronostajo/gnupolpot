package net.avensome.dev.gnupolpot.plotter.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.geometry.Point;

import java.util.List;
import java.util.stream.Collectors;

public class Shape {
    public static final double OPACITY_STROKE = 0.8;
    public static final double OPACITY_FILL = 0.5;
    private final List<PlotPoint> points;
    private final Color color;

    public Shape(List<PlotPoint> points, Color color) {
        this.points = points;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public Shape movedBy(Point delta) {
        List<PlotPoint> newPoints = points.stream()
                .map(plotPoint -> plotPoint.movedBy(delta))
                .collect(Collectors.toList());
        return new Shape(newPoints, color);
    }

    public Shape zoomed(Point offset, double factor) {
        List<PlotPoint> newPoints = points.stream()
                .map(plotPoint -> plotPoint.zoomed(offset, factor))
                .collect(Collectors.toList());
        return new Shape(newPoints, color);
    }

    public void paint(GraphicsContext ctx) {
        ctx.setFill(applyOpacity(color, OPACITY_FILL));
        ctx.setStroke(applyOpacity(color, OPACITY_STROKE));

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
