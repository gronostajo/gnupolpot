package net.avensome.dev.gnupolpot.plotter.painters;

import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.geometry.Point;
import net.avensome.dev.gnupolpot.geometry.Rect;
import net.avensome.dev.gnupolpot.plotter.shapes.Shape;

import java.util.List;
import java.util.stream.Collectors;

public class ShapePainter extends Painter {
    private final List<Shape> shapes;

    public ShapePainter(GraphicsContext ctx, List<Shape> shapes) {
        super(ctx);
        this.shapes = shapes;
    }

    @Override
    public void paint(Rect viewportRect) {
        Point topLeftCorner = viewportRect.getTopLeftCorner();
        List<Shape> movedShapes = shapes.stream()
                .map(shape -> shape.movedBy(topLeftCorner.scaled(-1)))
                .collect(Collectors.toList());

        ctx.setLineWidth(2);
        for (Shape shape : movedShapes) {
            shape.paint(ctx);
        }
    }
}
