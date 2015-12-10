package net.avensome.dev.gnupolpot.api.plotter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.mouse.Point;

/**
 * An orthonormal guide that is visible at any coordinates.
 */
public class Guide {
    private final Orientation orientation;
    private final double coord;
    private final Color color;
    private final OrientedGuidePainter painter;

    /**
     * <p>Creates a guide.
     * @param orientation guide orientation
     * @param coord X coordinate for vertical guides, or Y coordinate for horizontal guides
     * @param color guide color
     */
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

    /**
     * Paints guide on a canvas. Intended for internal use.
     * @param ctx graphics context to paint on
     * @param viewport current viewport
     */
    public void paint(GraphicsContext ctx, IViewport viewport) {
        painter.paint(ctx, viewport, coord, color);
    }

    /**
     * Guide orientation.
     */
    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    private interface OrientedGuidePainter {
        void paint(GraphicsContext ctx, IViewport viewport, double coord, Color color);
    }

    private class HorizontalGuidePainter implements OrientedGuidePainter {
        @Override
        public void paint(GraphicsContext ctx, IViewport viewport, double coord, Color color) {
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
        public void paint(GraphicsContext ctx, IViewport viewport, double coord, Color color) {
            if (!viewport.containsHorizontalCoord(coord)) {
                return;
            }

            Point point = viewport.toScreenCoords(coord, 0);

            ctx.setStroke(color);
            ctx.setLineWidth(1);
            ctx.strokeLine(point.getX() + 0.5, 0, point.getX() + 0.5, viewport.getHeight());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Guide guide = (Guide) o;

        return Double.compare(guide.coord, coord) == 0 && orientation == guide.orientation;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = orientation.hashCode();
        temp = Double.doubleToLongBits(coord);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
