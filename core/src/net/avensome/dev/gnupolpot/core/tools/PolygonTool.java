package net.avensome.dev.gnupolpot.core.tools;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.Tool;
import net.avensome.dev.gnupolpot.api.mouse.Buttons;
import net.avensome.dev.gnupolpot.api.mouse.MouseEventType;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.plotter.Shape;
import net.avensome.dev.gnupolpot.api.plotter.Viewport;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PolygonTool extends Tool {

    public static final Buttons PANNING_BUTTONS = Buttons.SECONDARY;
    public static final Buttons ADDING_BUTTONS = Buttons.PRIMARY;

    public static final Color COLOR_ADDED = Color.RED;
    public static final Color COLOR_FINAL = Color.BLACK;

    private static PolygonTool instance = new PolygonTool();

    private Shape shape;
    private List<PlotPoint> chain;
    private Collection<PlotPoint> newPoints;

    private PolygonTool() {
    }

    public static PolygonTool getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return "Create polygon";
    }

    @Override
    public Image getButtonImage() {
        return new Image("/net/avensome/dev/gnupolpot/core/icons/tools/polygon.png");
    }

    @Override
    public void activated(Api api) {
        shape = null;
        chain = new LinkedList<>();
        newPoints = new LinkedList<>();

        api.getPlotter().setCursor(Cursor.CROSSHAIR);
    }

    @Override
    public void deactivated(Api api) {
        IPlotter plotter = api.getPlotter();
        if (shape != null) {
            plotter.getShapes().remove(shape);
            plotter.requestRepaint();
            shape = null;
        }
        for (PlotPoint point : newPoints) {
            plotter.getPoints().remove(point);
        }
    }

    @Override
    public void receiveMouseEvent(Api api, MouseEventType eventType, MouseEvent event, boolean focusedPointChanged) {
        Buttons buttons = Buttons.fromMouseEvent(event);

        if (eventType == MouseEventType.DRAGGED) {
            PanningTool.getInstance().pan(api, event);
        } else if (eventType == MouseEventType.PRESSED) {
            if (buttons.equals(ADDING_BUTTONS)) {
                addPoint(api, event);
            } else if (buttons.equals(PANNING_BUTTONS)) {
                PanningTool.getInstance().startPanning(event);
            }
        } else if (eventType == MouseEventType.RELEASED) {
            PanningTool.getInstance().stopPanning(api, Cursor.CROSSHAIR);
        }
    }

    @Override
    public void receiveScrollEvent(Api api, ScrollEvent event) {
        PanningTool.getInstance().receiveScrollEvent(api, event);
    }

    private void addPoint(Api api, MouseEvent event) {
        IPlotter plotter = api.getPlotter();
        Viewport viewport = plotter.getViewport();

        double x = event.getX();
        double y = event.getY();

        PlotPoint clickedPoint = plotter.focusedPointProperty().get();

        if (clickedPoint == null) {
            Point pt = viewport.fromScreenCoords(x, y);
            clickedPoint = new PlotPoint(pt.getX(), pt.getY());
            newPoints.add(clickedPoint);
            plotter.getPoints().add(clickedPoint);
        } else if (chain.size() > 0 && clickedPoint.equals(chain.get(0))) {
            if (chain.size() <= 2) {
                return;
            } else {
                shape.setColor(COLOR_FINAL);
                shape.setType(Shape.Type.FILLED);
                plotter.requestRepaint();

                shape = null;
                chain = new LinkedList<>();
                newPoints.clear();

                return;
            }
        }

        chain.add(clickedPoint);
        if (chain.size() > 1) {
            if (shape != null) {
                plotter.getShapes().remove(shape);
            }
            shape = new Shape(chain, COLOR_ADDED, Shape.Type.LINE);
            plotter.getShapes().add(shape);
        }

        plotter.requestRepaint();
    }
}
