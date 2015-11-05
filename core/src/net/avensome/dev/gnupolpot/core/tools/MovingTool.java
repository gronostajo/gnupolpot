package net.avensome.dev.gnupolpot.core.tools;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
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

public class MovingTool extends Tool {

    public static final Buttons PANNING_BUTTONS = Buttons.SECONDARY;
    public static final Buttons MOVING_BUTTONS = Buttons.PRIMARY;

    private static MovingTool instance = new MovingTool();
    private ChangeListener<PlotPoint> focusedPointChangeListener;

    private Point mouseAnchor;
    private PlotPoint movingPoint = null;

    private MovingTool() {
    }

    public static MovingTool getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return "Move points";
    }

    @Override
    public Image getButtonImage() {
        return new Image("/net/avensome/dev/gnupolpot/core/icons/tools/moving.png");
    }

    @Override
    public void activated(Api api) {
        api.getPlotter().setCursor(Cursor.DEFAULT);
        focusedPointChangeListener = (observable, oldValue, newValue) -> updateCursor(api, newValue != null);
        api.getPlotter().focusedPointProperty().addListener(focusedPointChangeListener);
    }

    @Override
    public void deactivated(Api api) {
        api.getPlotter().focusedPointProperty().removeListener(focusedPointChangeListener);
    }

    @Override
    public void receiveMouseEvent(Api api, MouseEventType eventType, MouseEvent event) {
        Buttons buttons = Buttons.fromMouseEvent(event);
        SimpleObjectProperty<PlotPoint> focusedPoint = api.getPlotter().focusedPointProperty();

        if (eventType == MouseEventType.MOVED) {
            PanningTool.getInstance().updateFocus(api, event);
        } else if (eventType == MouseEventType.DRAGGED) {
            move(api, event);
            PanningTool.getInstance().pan(api, event);
        } else if (eventType == MouseEventType.PRESSED) {
            if (buttons.equals(MOVING_BUTTONS) && focusedPoint.get() != null) {
                startMoving(api, event);
            } else if (buttons.equals(PANNING_BUTTONS)) {
                PanningTool.getInstance().startPanning(event);
            }
        } else if (eventType == MouseEventType.RELEASED) {
            stopMoving();
            boolean pointFocused = (api.getPlotter().focusedPointProperty().get() != null);
            PanningTool.getInstance().stopPanning(api, getCursor(pointFocused));
        }
    }

    @Override
    public void receiveScrollEvent(Api api, ScrollEvent event) {
        PanningTool.getInstance().receiveScrollEvent(api, event);
    }

    private void startMoving(Api api, MouseEvent event) {
        movingPoint = api.getPlotter().focusedPointProperty().get();
        mouseAnchor = new Point(event.getX(), event.getY());
    }

    private void move(Api api, MouseEvent event) {
        if (movingPoint == null) {
            return;
        }

        IPlotter plotter = api.getPlotter();
        plotter.setCursor(Cursor.CLOSED_HAND);

        Point newAnchor = new Point(event.getX(), event.getY());
        Point delta = mouseAnchor.minus(newAnchor);

        double scale = plotter.getViewport().getScale();
        double x = movingPoint.getX() - delta.getX() / scale;
        double y = movingPoint.getY() + delta.getY() / scale;
        movingPoint.moveTo(x, y);
        plotter.requestRepaint();
        mouseAnchor = newAnchor;

        PanningTool.getInstance().updateFocus(api, event);
    }

    private void stopMoving() {
        if (movingPoint != null) {
            movingPoint = null;
            mouseAnchor = null;
        }
    }

    private void updateCursor(Api api, boolean pointFocused) {
        api.getPlotter().setCursor(getCursor(pointFocused));
    }

    private Cursor getCursor(boolean pointFocused) {
        return pointFocused ? Cursor.OPEN_HAND : Cursor.DEFAULT;
    }
}
