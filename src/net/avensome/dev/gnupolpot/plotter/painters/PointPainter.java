package net.avensome.dev.gnupolpot.plotter.painters;

import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.geometry.Point;
import net.avensome.dev.gnupolpot.geometry.Rect;
import net.avensome.dev.gnupolpot.plotter.shapes.PlotPoint;
import net.avensome.dev.gnupolpot.plotter.util.GeometryTools;
import net.avensome.dev.gnupolpot.plotter.util.Wrapper;

import java.util.List;

public class PointPainter extends Painter {
    public static final double POINT_RADIUS = 1.5;
    public static final double POINT_RADIUS_FOCUSED = 3;
    private final GraphicsContext ctx;
    private final List<PlotPoint> points;
    private final Wrapper<PlotPoint> focusedPoint;

    public PointPainter(GraphicsContext ctx, List<PlotPoint> points, Wrapper<PlotPoint> focusedPoint) {
        super(ctx);
        this.ctx = ctx;
        this.points = points;
        this.focusedPoint = focusedPoint;
    }

    @Override
    public void paint(Rect viewportRect) {
        List<PlotPoint> pointsInViewport = GeometryTools.pointsInRect(points, viewportRect);
        Point viewportDelta = viewportRect.getTopLeftCorner().scaled(-1);
        PlotPoint focusedPoint = this.focusedPoint.get();

        for (PlotPoint point : pointsInViewport) {
            ctx.setFill(point.getColor());
            double radius = point.hasEqualPoint(focusedPoint) ? POINT_RADIUS_FOCUSED : POINT_RADIUS;
            PlotPoint movedPoint = point.movedBy(viewportDelta);
            ctx.fillOval(movedPoint.getX() - radius, movedPoint.getY() - radius, radius * 2, radius * 2);
        }
    }
}
