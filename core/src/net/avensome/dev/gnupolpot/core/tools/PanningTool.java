package net.avensome.dev.gnupolpot.core.tools;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.Tool;
import net.avensome.dev.gnupolpot.api.mouse.MouseEventType;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;

public class PanningTool extends Tool {

    private static PanningTool instance = new PanningTool();

    private Point mouseAnchor;
    private boolean panning = false;

    private PanningTool() {
    }

    public static PanningTool getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return "Pan and zoom";
    }

    @Override
    public Image getButtonImage() {
        return new Image("/net/avensome/dev/gnupolpot/core/icons/tools/panning.png");
    }

    @Override
    public void activated(Api api) {
        api.getPlotter().setCursor(Cursor.DEFAULT);
    }

    @Override
    public void receiveMouseEvent(Api api, MouseEventType eventType, MouseEvent event, boolean focusedPointChanged) {
        switch (eventType) {
            case PRESSED:
                startPanning(event);
                break;
            case DRAGGED:
                pan(api, event);
                break;
            case RELEASED:
                stopPanning(api, Cursor.DEFAULT);
                break;
        }
    }

    @Override
    public void receiveScrollEvent(Api api, ScrollEvent event) {
        IPlotter plotter = api.getPlotter();

        if (event.getDeltaY() < 0) {
            plotter.getViewport().zoom(0.5);
        } else if (event.getDeltaY() > 0) {
            plotter.getViewport().zoom(2);
        } else {
            return;
        }

        plotter.requestRepaint();
    }

    public void startPanning(MouseEvent event) {
        panning = true;
        mouseAnchor = new Point(event.getX(), event.getY());
    }

    public void pan(Api api, MouseEvent event) {
        if (!panning) {
            return;
        }

        IPlotter plotter = api.getPlotter();

        plotter.setCursor(Cursor.MOVE);

        Point newAnchor = new Point(event.getX(), event.getY());
        Point delta = mouseAnchor.minus(newAnchor);

        plotter.getViewport().moveBy(delta.getX(), -delta.getY());
        plotter.requestRepaint();
        mouseAnchor = newAnchor;
    }

    public void stopPanning(Api api, Cursor cursor) {
        if (panning) {
            panning = false;
            api.getPlotter().setCursor(cursor);
            mouseAnchor = null;
        }
    }

}
