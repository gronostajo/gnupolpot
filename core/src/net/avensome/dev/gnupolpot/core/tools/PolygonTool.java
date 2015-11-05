package net.avensome.dev.gnupolpot.core.tools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
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
    private Shape shape;
    private List<PlotPoint> chain;
    private Collection<PlotPoint> newPoints;

    @Override
    public String getName() {
        return "Create polygon";
    }

    @Override
    public Image getButtonImage() {
        return new Image("/net/avensome/dev/gnupolpot/core/icons/tools/insert-object.png");
    }

    @Override
    public void activated(Api api) {
        shape = null;
        chain = new LinkedList<>();
        newPoints = new LinkedList<>();
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
    public void receiveMouseEvent(Api api, MouseEventType eventType, MouseEvent event) {
        Buttons actual = Buttons.fromMouseEvent(event);
        if (eventType == MouseEventType.MOVED) {
            DefaultTool.getInstance().handleMouseMoved(event);
        } else if (eventType == MouseEventType.PRESSED && actual.equals(Buttons.PRIMARY)) {
            handleMousePressed(api, event);
        }
    }

    private void handleMousePressed(Api api, MouseEvent event) {
        IPlotter plotter = api.getPlotter();
        Viewport viewport = plotter.getViewport();

        double x = event.getX();
        double y = event.getY();

        Point plotCoords = viewport.fromScreenCoords(x, y);
        double plotX = plotCoords.getX();
        double plotY = plotCoords.getY();

        PlotPoint clickedPoint = viewport.pointAtPlotCoords(plotX, plotY, DefaultTool.POINT_FOCUS_RADIUS, plotter.getPoints());

        if (clickedPoint == null) {
            Point pt = viewport.fromScreenCoords(x, y);
            clickedPoint = new PlotPoint(pt.getX(), pt.getY());
            newPoints.add(clickedPoint);
            plotter.getPoints().add(clickedPoint);
        } else if (clickedPoint.equals(chain.get(0))) {
            if (chain.size() <= 2) {
                return;
            } else {
                shape.setColor(Color.BLACK);
                shape.setType(Shape.Type.FILLED);
                plotter.requestRepaint();

                shape = null;
                newPoints.clear();

                api.selectDefaultTool();
                return;
            }
        }

        chain.add(clickedPoint);
        if (chain.size() > 1) {
            if (shape != null) {
                plotter.getShapes().remove(shape);
            }
            shape = new Shape(chain, Color.RED, Shape.Type.LINE);
            plotter.getShapes().add(shape);
        }

        plotter.requestRepaint();
    }
}
