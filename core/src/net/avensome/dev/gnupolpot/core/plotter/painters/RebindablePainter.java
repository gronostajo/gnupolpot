package net.avensome.dev.gnupolpot.core.plotter.painters;

import javafx.scene.canvas.GraphicsContext;

public abstract class RebindablePainter extends Painter {
    public RebindablePainter() {
        super(null);
    }

    public void setContext(GraphicsContext ctx) {
        this.ctx = ctx;
    }
}
