package net.avensome.dev.gnupolpot.api.action;

import javafx.scene.input.MouseEvent;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;

/**
 * Helper class for implementing panning the viewport.
 */
public final class Panning {
    private static Point mouseAnchor;
    private static boolean panning = false;

    /**
     * Should be called when panning starts.
     * @param event associated mouse event
     */
    public static void start(MouseEvent event) {
        panning = true;
        mouseAnchor = new Point(event.getX(), event.getY());
    }

    /**
     * Should be called when mouse pointer is moved.
     * @param api API object
     * @param event associated mouse event
     */
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

    /**
     * Should be called when panning ends.
     */
    public static void stop() {
        if (panning) {
            panning = false;
            mouseAnchor = null;
        }
    }

    /**
     * @return Whether panning is in progress.
     */
    public static boolean isPanning() {
        return panning;
    }
}
