package net.avensome.dev.gnupolpot.api.action;

import javafx.scene.input.MouseEvent;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;

public class Moving {

    private static Point mouseAnchor;
    private static PlotPoint movingPoint = null;

    public static void start(Api api, MouseEvent event) {
        movingPoint = api.getPlotter().focusedPointProperty().get();
        mouseAnchor = new Point(event.getX(), event.getY());
    }

    public static void update(Api api, MouseEvent event) {
        if (movingPoint == null) {
            return;
        }

        Point newAnchor = new Point(event.getX(), event.getY());
        Point delta = mouseAnchor.minus(newAnchor);

        IPlotter plotter = api.getPlotter();

        double scale = plotter.getViewport().getScale();
        double x = movingPoint.getX() - delta.getX() / scale;
        double y = movingPoint.getY() + delta.getY() / scale;
        movingPoint.moveTo(x, y);
        plotter.requestRepaint();
        mouseAnchor = newAnchor;
    }

    public static void stop() {
        if (movingPoint != null) {
            movingPoint = null;
            mouseAnchor = null;
        }
    }

    public static boolean isMoving() {
        return movingPoint != null;
    }
}
