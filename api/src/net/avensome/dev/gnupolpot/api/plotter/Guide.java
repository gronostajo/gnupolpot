package net.avensome.dev.gnupolpot.api.plotter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.mouse.Point;

public class Guide {
    private final Orientation orientation;
    private final double coord;
    private final Color color;
    private final OrientedGuidePainter painter;

    public Guide(Orientation orientation, double coord, Color color) {
        this.orientation = orientation;
        this.coord = coord;
        this.color = color;

        this.painter = (orientation == Orientation.HORIZONTAL)
                ? new HorizontalGuidePainter()
                : new VerticalGuidePainter();
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public double getCoord() {
        return coord;
    }

    public Color getColor() {
        return color;
    }

    public void paint(GraphicsContext ctx, Viewport viewport) {
        painter.paint(ctx, viewport, coord, color);
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    private interface OrientedGuidePainter {
        void paint(GraphicsContext ctx, Viewport viewport, double coord, Color color);
    }

    private class HorizontalGuidePainter implements OrientedGuidePainter {
        @Override
        public void paint(GraphicsContext ctx, Viewport viewport, double coord, Color color) {
            if (!viewport.containsVerticalCoord(coord)) {
                return;
            }

            Point point = viewport.toScreenCoords(0, coord);

            ctx.setStroke(color);
            ctx.setLineWidth(1);
            ctx.strokeLine(0, point.getY() + 0.5, viewport.getWidth(), point.getY() + 0.5);
        }
    }

    private class VerticalGuidePainter implements OrientedGuidePainter {
        @Override
        public void paint(GraphicsContext ctx, Viewport viewport, double coord, Color color) {
            if (!viewport.containsHorizontalCoord(coord)) {
                return;
            }

            Point point = viewport.toScreenCoords(coord, 0);

            ctx.setStroke(color);
            ctx.setLineWidth(1);
            ctx.strokeLine(point.getX() + 0.5, 0, point.getX() + 0.5, viewport.getHeight());
        }
    }
}
