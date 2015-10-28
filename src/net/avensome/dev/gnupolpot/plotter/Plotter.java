package net.avensome.dev.gnupolpot.plotter;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.geometry.Point;
import net.avensome.dev.gnupolpot.geometry.Viewport;
import net.avensome.dev.gnupolpot.plotter.mouse.EventButtons;
import net.avensome.dev.gnupolpot.plotter.mouse.EventButtonsBuilder;
import net.avensome.dev.gnupolpot.plotter.painters.BackgroundPainter;
import net.avensome.dev.gnupolpot.plotter.painters.Painter;
import net.avensome.dev.gnupolpot.plotter.painters.PointPainter;
import net.avensome.dev.gnupolpot.plotter.painters.ShapePainter;
import net.avensome.dev.gnupolpot.plotter.shapes.PlotPoint;
import net.avensome.dev.gnupolpot.plotter.shapes.Shape;
import net.avensome.dev.gnupolpot.plotter.util.GeometryTools;
import net.avensome.dev.gnupolpot.plotter.util.Wrapper;

import java.util.LinkedList;
import java.util.List;

public class Plotter extends Pane {
    public static final int POINT_FOCUS_RADIUS = 3;
    private final Canvas canvas = new Canvas();
    private boolean requiresRepaint;

    private List<PlotPoint> points = FXCollections.observableArrayList();
    private List<Shape> shapes = FXCollections.observableArrayList();

    private Viewport viewport;

    private List<Painter> painters = new LinkedList<>();

    private MouseState mouseState = MouseState.NOT_INTERACTING;
    private Point mouseAnchor;
    private Wrapper<PlotPoint> focusedPoint = new Wrapper<>();

    public Plotter() {
        super();

        getChildren().add(canvas);

        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        viewport = new Viewport(0, 0, getWidth(), getHeight(), 1);

        ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
            viewport.resize(getWidth(), getHeight());
            requestRepaint();
        };
        widthProperty().addListener(resizeListener);
        heightProperty().addListener(resizeListener);

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMouseMoved);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        canvas.addEventHandler(ScrollEvent.SCROLL, this::handleScrolling);

        createPaintingPipeline();
        requestRepaint();
    }

    private void handleMouseMoved(MouseEvent mouseEvent) {
        double scale = viewport.getScale();
        Viewport scaledViewport = viewport.applyScale();
        double x = scaledViewport.getLeft() + mouseEvent.getX() / scale;
        double y = scaledViewport.getTop() + mouseEvent.getY() / scale;

        PlotPoint focusedPoint = GeometryTools
                .pointsInRect(points, this.viewport).stream()
                .filter(point -> point.distanceFrom(x, y) * scale < POINT_FOCUS_RADIUS)
                .sorted((o1, o2) -> Double.compare(o1.distanceFrom(x, y), o2.distanceFrom(x, y)))
                .reduce(null, (point1, point2) -> point1 == null ? point2 : point1);

        boolean focusChanged = this.focusedPoint.set(focusedPoint);
        if (focusChanged) {
            requestRepaint();
        }
    }

    private void handleMousePressed(MouseEvent mouseEvent) {
        mouseAnchor = new Point(mouseEvent.getX(), mouseEvent.getY());
    }

    private void handleMouseDragged(MouseEvent mouseEvent) {
        EventButtons primaryOnly = new EventButtonsBuilder().withPrimary().build();
        EventButtons secondaryOnly = new EventButtonsBuilder().withSecondary().build();
        EventButtons actual = EventButtons.fromMouseEvent(mouseEvent);

        if (actual.equals(primaryOnly) || actual.equals(secondaryOnly)) {
            mouseState = MouseState.PANNING;
            canvas.setCursor(Cursor.MOVE);

            Point newAnchor = new Point(mouseEvent.getX(), mouseEvent.getY());
            Point delta = mouseAnchor.minus(newAnchor);

            viewport.moveBy(delta.getX(), delta.getY());
            requestRepaint();
            mouseAnchor = newAnchor;

            handleMouseMoved(mouseEvent);
        }
    }

    private void handleMouseReleased(MouseEvent mouseEvent) {
        mouseState = MouseState.NOT_INTERACTING;
        canvas.setCursor(Cursor.DEFAULT);
        mouseAnchor = null;
    }

    private void handleScrolling(ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaY() < 0) {
            viewport.zoom(0.5);
        } else if (scrollEvent.getDeltaY() > 0) {
            viewport.zoom(2);
        } else {
            return;
        }

        requestRepaint();
    }

    private void createPaintingPipeline() {
        GraphicsContext ctx = canvas.getGraphicsContext2D();

        BackgroundPainter backgroundPainter = new BackgroundPainter(ctx, Color.WHITE);
        painters.add(backgroundPainter);

        ShapePainter shapePainter = new ShapePainter(ctx, shapes);
        painters.add(shapePainter);

        PointPainter pointPainter = new PointPainter(ctx, points, focusedPoint);
        painters.add(pointPainter);
    }

    public void requestRepaint() {
        synchronized (canvas) {
            requiresRepaint = true;
        }
        Platform.runLater(() -> {
            synchronized (canvas) {
                if (requiresRepaint) {
                    requiresRepaint = false;
                    repaint();
                }
            }
        });
    }

    private void repaint() {
        for (Painter painter : painters) {
            painter.paint(viewport);
        }
    }

    public void clear() {
        points.clear();
        shapes.clear();
        requestRepaint();
    }

    public void importPlot(Importer.PlotData data) throws DataFormatException {
        points.addAll(data.getPoints());
        shapes.addAll(data.getShapes());
        requestRepaint();
    }

    public void zoomAll(boolean instantRepaint) {
        if (points.size() == 0) {
            viewport.centerAt(0, 0);
            if (instantRepaint) {
                repaint();
            } else {
                requestRepaint();
            }
            return;
        } else if (points.size() == 1) {
            PlotPoint point = points.get(0);
            viewport.centerAt(point.getX(), point.getY());
            if (instantRepaint) {
                repaint();
            } else {
                requestRepaint();
            }
            return;
        }

        double minX = points.stream().map(PlotPoint::getX).reduce(Math::min).get();
        double maxX = points.stream().map(PlotPoint::getX).reduce(Math::max).get();
        double minY = points.stream().map(PlotPoint::getY).reduce(Math::min).get();
        double maxY = points.stream().map(PlotPoint::getY).reduce(Math::max).get();

        double spreadX = (maxX - minX);
        double spreadY = (maxY - minY);

        double viewportSpread = Math.max(spreadX / viewport.getWidth(), spreadY / viewport.getHeight());
        double scale = Math.log(viewportSpread) / Math.log(2);  // log_e(x) / log_e(2) == log_2(x)

        double centerX = (minX + maxX) / 2;
        double centerY = (minY + maxY) / 2;

        viewport.setScalePower((int) -Math.ceil(scale));
        viewport.centerAt(centerX, centerY);
        if (instantRepaint) {
            repaint();
        } else {
            requestRepaint();
        }
    }

    public WritableImage snapshot() {
        return canvas.snapshot(null, null);
    }

    public List<PlotPoint> getPoints() {
        return points;
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public Viewport getViewport() {
        return viewport;
    }

    private enum MouseState {
        NOT_INTERACTING,
        PANNING
    }
}
