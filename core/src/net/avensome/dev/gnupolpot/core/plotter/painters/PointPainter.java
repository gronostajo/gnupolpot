package net.avensome.dev.gnupolpot.core.plotter.painters;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.plotter.Viewport;

import java.util.Collection;
import java.util.List;

public class PointPainter extends Painter {
    public static final double POINT_RADIUS = 2;
    public static final double POINT_RADIUS_FOCUSED = 3;
    private final GraphicsContext ctx;
    private final List<PlotPoint> points;
    private final SimpleObjectProperty<PlotPoint> focusedPoint;

    public PointPainter(GraphicsContext ctx, List<PlotPoint> points, SimpleObjectProperty<PlotPoint> focusedPoint) {
        super(ctx);
        this.ctx = ctx;
        this.points = points;
        this.focusedPoint = focusedPoint;
    }

    @Override
    public void paint(Viewport viewport) {
        Collection<PlotPoint> pointsInViewport = viewport.visiblePoints(points);
        PlotPoint focusedPoint = this.focusedPoint.get();

        for (PlotPoint point : pointsInViewport) {
            ctx.setFill(point.getColor());
            double radius = point.equals(focusedPoint) ? POINT_RADIUS_FOCUSED : POINT_RADIUS;
            Point screenPoint = viewport.toScreenCoords(point);
            ctx.fillOval(screenPoint.getX() - radius, screenPoint.getY() - radius, radius * 2, radius * 2);
        }
    }
}
