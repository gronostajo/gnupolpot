package net.avensome.dev.gnupolpot.plotter.painters;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import net.avensome.dev.gnupolpot.geometry.Viewport;

public class BackgroundPainter extends Painter {
    private final Paint backgroundColor;

    public BackgroundPainter(GraphicsContext ctx, Paint backgroundColor) {
        super(ctx);
        if (!backgroundColor.isOpaque()) {
            throw new RuntimeException("Background color not opaque - would cause glitches");
        }
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void paint(Viewport viewport) {
        ctx.setFill(backgroundColor);
        ctx.fillRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
    }
}
