package net.avensome.dev.gnupolpot.core.tools;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.Tool;
import net.avensome.dev.gnupolpot.api.mouse.Buttons;
import net.avensome.dev.gnupolpot.api.mouse.MouseEventType;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.plotter.Viewport;
import net.avensome.dev.gnupolpot.core.plotter.util.GeometryTools;

public class DefaultTool extends Tool {

    public static final int POINT_FOCUS_RADIUS = 3;

    private static DefaultTool instance = new DefaultTool();

    private IPlotter plotter;
    private Viewport viewport;

    private Point mouseAnchor;

    private DefaultTool() {
    }

    public static DefaultTool getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return "Panning";
    }

    @Override
    public Image getButtonImage() {
        return new Image("/net/avensome/dev/gnupolpot/core/icons/tools/input-mouse.png");
    }

    @Override
    public void receiveMouseEvent(Api api, MouseEventType eventType, MouseEvent event) {
        plotter = api.getPlotter();
        viewport = plotter.getViewport();

        switch (eventType) {
            case MOVED:
                handleMouseMoved(event);
                break;
            case PRESSED:
                handleMousePressed(event);
                break;
            case DRAGGED:
                handleMouseDragged(event);
                break;
            case RELEASED:
                handleMouseReleased(event);
                break;
        }
    }

    @Override
    public void receiveScrollEvent(Api api, ScrollEvent event) {
        if (event.getDeltaY() < 0) {
            viewport.zoom(0.5);
        } else if (event.getDeltaY() > 0) {
            viewport.zoom(2);
        } else {
            return;
        }

        plotter.requestRepaint();
    }

    private void handleMouseMoved(MouseEvent mouseEvent) {
        double scale = viewport.getScale();
        Viewport scaledViewport = viewport.applyScale();
        double x = scaledViewport.getLeft() + mouseEvent.getX() / scale;
        double y = scaledViewport.getTop() - mouseEvent.getY() / scale;

        PlotPoint focusedPoint = GeometryTools
                .pointsInRect(plotter.getPoints(), this.viewport).stream()
                .filter(point -> point.distanceFrom(x, y) * scale < POINT_FOCUS_RADIUS)
                .sorted((o1, o2) -> Double.compare(o1.distanceFrom(x, y), o2.distanceFrom(x, y)))
                .reduce(null, (point1, point2) -> point1 == null ? point2 : point1);

        boolean focusChanged = (plotter.focusedPointProperty().get() != focusedPoint);
        plotter.focusedPointProperty().set(focusedPoint);

        if (focusChanged) {
            plotter.requestRepaint();
        }
    }

    private void handleMousePressed(MouseEvent mouseEvent) {
        mouseAnchor = new Point(mouseEvent.getX(), mouseEvent.getY());
    }

    private void handleMouseDragged(MouseEvent mouseEvent) {
        Buttons actual = Buttons.fromMouseEvent(mouseEvent);

        if (actual.equals(Buttons.PRIMARY) || actual.equals(Buttons.SECONDARY)) {
            plotter.setCursor(Cursor.MOVE);

            Point newAnchor = new Point(mouseEvent.getX(), mouseEvent.getY());
            Point delta = mouseAnchor.minus(newAnchor);

            viewport.moveBy(delta.getX(), -delta.getY());
            plotter.requestRepaint();
            mouseAnchor = newAnchor;

            handleMouseMoved(mouseEvent);
        }
    }

    private void handleMouseReleased(MouseEvent mouseEvent) {
        plotter.setCursor(Cursor.DEFAULT);
        mouseAnchor = null;
    }

}
