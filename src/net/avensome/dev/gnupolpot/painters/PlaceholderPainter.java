package net.avensome.dev.gnupolpot.painters;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.geometry.Rect;

public class PlaceholderPainter extends Painter {
    public PlaceholderPainter(GraphicsContext ctx) {
        super(ctx);
    }

    @Override
    public void paint(Rect viewportRect) {
        ctx.setStroke(Color.RED.interpolate(Color.TRANSPARENT, 0.3));
        ctx.setLineWidth(3);
        ctx.strokeLine(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
        ctx.strokeLine(0, ctx.getCanvas().getHeight(), ctx.getCanvas().getWidth(), 0);
    }
}
