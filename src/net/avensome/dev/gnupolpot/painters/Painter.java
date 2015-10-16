package net.avensome.dev.gnupolpot.painters;

import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.geometry.Rect;

public abstract class Painter {
    protected final GraphicsContext ctx;

    protected Painter(GraphicsContext ctx) {
        this.ctx = ctx;
    }

    public abstract void paint(Rect viewportRect);
}
