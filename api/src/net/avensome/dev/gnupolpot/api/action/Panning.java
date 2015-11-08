package net.avensome.dev.gnupolpot.api.action;

import javafx.scene.input.MouseEvent;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;

public class Panning {

    private static Point mouseAnchor;

    private static boolean panning = false;

    public static void start(MouseEvent event) {
        panning = true;
        mouseAnchor = new Point(event.getX(), event.getY());
    }

    public static void update(Api api, MouseEvent event) {
        if (!panning) {
            return;
        }

        IPlotter plotter = api.getPlotter();

        Point newAnchor = new Point(event.getX(), event.getY());
        Point delta = mouseAnchor.minus(newAnchor);

        plotter.getViewport().moveBy(delta.getX(), -delta.getY());
        plotter.requestRepaint();
        mouseAnchor = newAnchor;
    }

    public static void stop() {
        if (panning) {
            panning = false;
            mouseAnchor = null;
        }
    }

    public static boolean isPanning() {
        return panning;
    }
}
