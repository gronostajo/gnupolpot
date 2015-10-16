package net.avensome.dev.gnupolpot.painters;

import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.geometry.Point;
import net.avensome.dev.gnupolpot.geometry.Rect;
import net.avensome.dev.gnupolpot.shapes.PlotPoint;

import java.util.List;
import java.util.stream.Collectors;

public class PointPainter extends Painter {
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
                .map(plotPoint -> plotPoint.movedBy(topLeftCorner))
                .collect(Collectors.toList());

        for (PlotPoint point : pointsInViewport) {
            ctx.setFill(point.getColor());
            ctx.fillOval(point.getX() - 2, point.getY() - 2, 4, 4);
        }
    }
}
