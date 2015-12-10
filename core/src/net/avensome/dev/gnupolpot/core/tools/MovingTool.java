package net.avensome.dev.gnupolpot.core.tools;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.Tool;
import net.avensome.dev.gnupolpot.api.action.Moving;
import net.avensome.dev.gnupolpot.api.action.Panning;
import net.avensome.dev.gnupolpot.api.mouse.Buttons;
import net.avensome.dev.gnupolpot.api.mouse.MouseEventType;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;

public class MovingTool extends Tool {

    public static final Buttons PANNING_BUTTONS = Buttons.SECONDARY;
    public static final Buttons MOVING_BUTTONS = Buttons.PRIMARY;

    private static MovingTool instance = new MovingTool();
    private ChangeListener<PlotPoint> focusedPointChangeListener;

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
        Panning.stop();
        Moving.stop();
    }

    @Override
    public void receiveMouseEvent(Api api, MouseEventType eventType, MouseEvent event, boolean focusedPointChanged) {
        Buttons buttons = Buttons.fromMouseEvent(event);
        SimpleObjectProperty<PlotPoint> focusedPoint = api.getPlotter().focusedPointProperty();
        if (eventType == MouseEventType.DRAGGED) {
            if (Moving.isMoving()) {
                api.getPlotter().setCursor(Cursor.CLOSED_HAND);
                Moving.update(api, event);
            } else if (Panning.isPanning()) {
                api.getPlotter().setCursor(Cursor.MOVE);
                Panning.update(api, event);
            }
        } else if (eventType == MouseEventType.PRESSED) {
            if (buttons.equals(MOVING_BUTTONS) && focusedPoint.get() != null) {
                Moving.start(api, event);
            } else if (buttons.equals(PANNING_BUTTONS)) {
                Panning.start(event);
            }
        } else if (eventType == MouseEventType.RELEASED) {
            Moving.stop();
            Panning.stop();
            boolean pointFocused = (api.getPlotter().focusedPointProperty().get() != null);
            api.getPlotter().setCursor(getCursor(pointFocused));
        }

    }

    private void updateCursor(Api api, boolean pointFocused) {
        api.getPlotter().setCursor(getCursor(pointFocused));
    }

    private Cursor getCursor(boolean pointFocused) {
        return pointFocused ? Cursor.OPEN_HAND : Cursor.DEFAULT;
    }
}
