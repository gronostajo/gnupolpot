package net.avensome.dev.gnupolpot.core.plotter.painters;

import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.api.plotter.Shape;
import net.avensome.dev.gnupolpot.api.plotter.Viewport;

import java.util.List;

public class ShapePainter extends Painter {
    private final List<Shape> shapes;

    public ShapePainter(GraphicsContext ctx, List<Shape> shapes) {
        super(ctx);
        this.shapes = shapes;
    }

    @Override
    public void paint(Viewport viewport) {
        ctx.setLineWidth(2);
        for (Shape shape : shapes) {
            shape.paint(ctx, viewport);
        }
    }
}
