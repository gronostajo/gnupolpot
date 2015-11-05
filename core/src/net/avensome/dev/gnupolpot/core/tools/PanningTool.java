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

public class PanningTool extends Tool {

    public static final int POINT_FOCUS_RADIUS = 3;

    private static PanningTool instance = new PanningTool();

    private IPlotter plotter;
    private Viewport viewport;

    private Point mouseAnchor;

    private PanningTool() {
    }

    public static PanningTool getInstance() {
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
        Buttons buttons = Buttons.fromMouseEvent(event);

        switch (eventType) {
            case MOVED:
                handleMouseMoved(event);
                break;
            case PRESSED:
                handleMousePressed(event);
                break;
            case DRAGGED:
                if (buttons.equals(Buttons.PRIMARY) || buttons.equals(Buttons.SECONDARY)) {
                    handleMouseDragged(event);
                }
                break;
            case RELEASED:
                handleMouseReleased();
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

    public void handleMouseMoved(MouseEvent event) {
        Point plotCoords = viewport.fromScreenCoords(event.getX(), event.getY());
        double x = plotCoords.getX();
        double y = plotCoords.getY();

        PlotPoint focusedPoint = viewport.pointAtPlotCoords(x, y, POINT_FOCUS_RADIUS, plotter.getPoints());

        boolean focusChanged = (plotter.focusedPointProperty().get() != focusedPoint);
        plotter.focusedPointProperty().set(focusedPoint);

        if (focusChanged) {
            plotter.requestRepaint();
        }
    }

    public void handleMousePressed(MouseEvent event) {
        mouseAnchor = new Point(event.getX(), event.getY());
    }

    public void handleMouseDragged(MouseEvent event) {
        Buttons actual = Buttons.fromMouseEvent(event);

        if (actual.equals(Buttons.PRIMARY) || actual.equals(Buttons.SECONDARY)) {
            plotter.setCursor(Cursor.MOVE);

            Point newAnchor = new Point(event.getX(), event.getY());
            Point delta = mouseAnchor.minus(newAnchor);

            viewport.moveBy(delta.getX(), -delta.getY());
            plotter.requestRepaint();
            mouseAnchor = newAnchor;

            handleMouseMoved(event);
        }
    }

    public void handleMouseReleased() {
        plotter.setCursor(Cursor.DEFAULT);
        mouseAnchor = null;
    }

}
