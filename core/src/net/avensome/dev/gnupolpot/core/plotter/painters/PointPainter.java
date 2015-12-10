package net.avensome.dev.gnupolpot.core.plotter.painters;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.core.plotter.Viewport;

import java.util.Collection;

public class PointPainter extends Painter {
    public static final double POINT_RADIUS = 2;
    public static final double POINT_RADIUS_FOCUSED = 3;
    private final GraphicsContext ctx;
    private final ObservableSet<PlotPoint> points;
    private final SimpleObjectProperty<PlotPoint> focusedPoint;

    public PointPainter(GraphicsContext ctx, ObservableSet<PlotPoint> layers, SimpleObjectProperty<PlotPoint> focusedPoint) {
        super(ctx);
        this.ctx = ctx;
        this.points = layers;
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
            ctx.fillOval(screenPoint.getX() - radius + 0.5, screenPoint.getY() - radius + 0.5, radius * 2, radius * 2);
        }
    }
}
