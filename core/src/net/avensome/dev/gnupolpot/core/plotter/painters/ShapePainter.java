package net.avensome.dev.gnupolpot.core.plotter.painters;

import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.core.geometry.Point;
import net.avensome.dev.gnupolpot.core.geometry.Viewport;
import net.avensome.dev.gnupolpot.api.plotter.Shape;

import java.util.List;
import java.util.stream.Collectors;

public class ShapePainter extends Painter {
    private final List<Shape> shapes;

    public ShapePainter(GraphicsContext ctx, List<Shape> shapes) {
        super(ctx);
        this.shapes = shapes;
    }

    @Override
    public void paint(Viewport viewport) {
        Point topLeftCorner = viewport.applyScale().getTopLeftCorner();
        List<Shape> movedShapes = shapes.stream()
                .map(shape -> shape.zoomed(topLeftCorner.getX(), topLeftCorner.getY(), viewport.getScale()))
                .collect(Collectors.toList());

        ctx.setLineWidth(2);
        for (Shape shape : movedShapes) {
            shape.paint(ctx, viewport.getHeight());
        }
    }
}
