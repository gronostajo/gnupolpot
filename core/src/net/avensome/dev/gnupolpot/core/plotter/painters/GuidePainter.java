package net.avensome.dev.gnupolpot.core.plotter.painters;

import javafx.scene.canvas.GraphicsContext;
import net.avensome.dev.gnupolpot.api.plotter.Guide;
import net.avensome.dev.gnupolpot.core.plotter.Viewport;

import java.util.Set;

public class GuidePainter extends Painter {
    private final Set<Guide> guides;

    public GuidePainter(GraphicsContext ctx, Set<Guide> guides) {
        super(ctx);
        this.guides = guides;
    }

    @Override
    public void paint(Viewport viewport) {
        for (Guide guide : guides) {
            guide.paint(ctx, viewport);
        }
    }
}
