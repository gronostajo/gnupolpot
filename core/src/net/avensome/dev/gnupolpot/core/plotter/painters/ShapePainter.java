package net.avensome.dev.gnupolpot.core.plotter.painters;

import javafx.collections.ObservableSet;
import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.api.plotter.Shape;
import net.avensome.dev.gnupolpot.core.plotter.Viewport;

public class ShapePainter extends Painter {
    private final ObservableSet<Shape> shapes;

    public ShapePainter(GraphicsContext ctx, ObservableSet<Shape> layers) {
        super(ctx);
        this.shapes = layers;
    }

    @Override
    public void paint(Viewport viewport) {
        ctx.setLineWidth(2);
        for (Shape shape : shapes) {
            shape.paint(ctx, viewport);
        }
    }
}
