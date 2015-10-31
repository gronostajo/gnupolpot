package net.avensome.dev.gnupolpot.core.plotter.painters;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.core.geometry.Point;
import net.avensome.dev.gnupolpot.core.geometry.Viewport;
import net.avensome.dev.gnupolpot.core.plotter.util.GeometryTools;

import java.util.List;

public class PointPainter extends Painter {
    public static final double POINT_RADIUS = 1.5;
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
        Viewport actualViewport = viewport.applyScale();
        List<PlotPoint> pointsInViewport = GeometryTools.pointsInRect(points, actualViewport);
        Point topLeftCorner = actualViewport.getTopLeftCorner();
        PlotPoint focusedPoint = this.focusedPoint.get();

        for (PlotPoint point : pointsInViewport) {
            ctx.setFill(point.getColor());
            double radius = point.hasEqualPoint(focusedPoint) ? POINT_RADIUS_FOCUSED : POINT_RADIUS;
            PlotPoint movedPoint = point.zoomed(topLeftCorner.getX(), topLeftCorner.getY(), viewport.getScale());
            ctx.fillOval(movedPoint.getX() - radius, viewport.getHeight() - movedPoint.getY() - radius, radius * 2, radius * 2);
        }
    }
}
