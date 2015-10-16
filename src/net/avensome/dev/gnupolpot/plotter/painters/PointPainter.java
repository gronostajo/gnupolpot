package net.avensome.dev.gnupolpot.plotter.painters;

import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.geometry.Point;
import net.avensome.dev.gnupolpot.geometry.Rect;
import net.avensome.dev.gnupolpot.plotter.shapes.PlotPoint;

import java.util.List;
import java.util.stream.Collectors;

public class PointPainter extends Painter {
    public static final double POINT_RADIUS = 1.5;
    private final GraphicsContext ctx;
    private final List<PlotPoint> points;

    public PointPainter(GraphicsContext ctx, List<PlotPoint> points) {
        super(ctx);
        this.ctx = ctx;
        this.points = points;
    }

    @Override
    public void paint(Rect viewportRect) {
        Point topLeftCorner = viewportRect.getTopLeftCorner();
        List<PlotPoint> pointsInViewport = points.stream()
                .filter(plotPoint -> viewportRect.contains(plotPoint.getX(), plotPoint.getY()))
                .map(plotPoint -> plotPoint.movedBy(topLeftCorner.scaled(-1)))
                .collect(Collectors.toList());

        for (PlotPoint point : pointsInViewport) {
            ctx.setFill(point.getColor());
            ctx.fillOval(point.getX() - POINT_RADIUS, point.getY() - POINT_RADIUS, POINT_RADIUS * 2, POINT_RADIUS * 2);
        }
    }
}
