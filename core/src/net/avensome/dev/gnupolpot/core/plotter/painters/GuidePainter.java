package net.avensome.dev.gnupolpot.core.plotter.painters;

import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.plotter.Guide;
import net.avensome.dev.gnupolpot.core.plotter.Viewport;

import java.util.Set;

import static net.avensome.dev.gnupolpot.api.plotter.Guide.Orientation.*;

public class GuidePainter extends Painter {
    private final Set<Guide> guides;

    public GuidePainter(GraphicsContext ctx, Set<Guide> guides) {
        super(ctx);
        this.guides = guides;
    }

    @Override
    public void paint(Viewport viewport) {
        for (Guide guide : guides) {
            Point start, end;

            if (guide.getOrientation() == HORIZONTAL) {
                Point point = viewport.toScreenCoords(0, guide.getCoord());
                if (point.getY() < 0 || point.getY() >= ctx.getCanvas().getHeight()) {
                    return;
                }
                start = new Point(0, point.getY() + 0.5);
                end = new Point(viewport.getWidth(), point.getY() + 0.5);
            } else {
                Point point = viewport.toScreenCoords(guide.getCoord(), 0);
                if (point.getX() < 0 || point.getX() >= ctx.getCanvas().getWidth()) {
                    return;
                }
                start = new Point(point.getX() + 0.5, 0);
                end = new Point(point.getX() + 0.5, viewport.getHeight());
            }

            ctx.setStroke(guide.getColor());
            ctx.setLineWidth(1);
            ctx.setLineDashes(guide.getDashesStyle());

            ctx.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());

            ctx.setLineDashes();
        }
    }
}
