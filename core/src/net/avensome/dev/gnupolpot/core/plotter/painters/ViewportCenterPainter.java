package net.avensome.dev.gnupolpot.core.plotter.painters;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.core.plotter.Viewport;

public class ViewportCenterPainter extends RebindablePainter {
    private final ReadOnlyBooleanProperty enabled;
    private final Color color;
    private final double[] dashesStyle;


    public ViewportCenterPainter(ReadOnlyBooleanProperty enabled) {
        this(enabled, Color.DODGERBLUE, new double[] {5, 3, 2, 3});
    }

    public ViewportCenterPainter(ReadOnlyBooleanProperty enabled, Color color, double[] dashesStyle) {
        super();
        this.enabled = enabled;
        this.color = color;
        this.dashesStyle = dashesStyle;
    }

    @Override
    public void paint(Viewport viewport) {
        if (!enabled.get()) {
            return;
        }

        Canvas canvas = ctx.getCanvas();
        int x = (int) canvas.getWidth() / 2;
        int y = (int) canvas.getHeight() / 2;

        ctx.setStroke(color);
        ctx.setLineWidth(1);
        ctx.setLineDashes(dashesStyle);

        ctx.strokeLine(x + 0.5, 0, x + 0.5, canvas.getHeight());
        ctx.strokeLine(0, y + 0.5, canvas.getWidth(), y + 0.5);

        ctx.setLineDashes();
    }
}
