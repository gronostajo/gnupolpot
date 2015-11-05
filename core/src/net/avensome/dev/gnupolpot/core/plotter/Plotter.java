package net.avensome.dev.gnupolpot.core.plotter;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.plotter.*;
import net.avensome.dev.gnupolpot.core.plotter.painters.BackgroundPainter;
import net.avensome.dev.gnupolpot.core.plotter.painters.Painter;
import net.avensome.dev.gnupolpot.core.plotter.painters.PointPainter;
import net.avensome.dev.gnupolpot.core.plotter.painters.ShapePainter;

import java.util.LinkedList;
import java.util.List;

public class Plotter extends Pane implements IPlotter {
    private final Canvas canvas = new Canvas();
    private boolean requiresRepaint;

    private ObservableList<PlotPoint> points = FXCollections.observableArrayList();
    private ObservableList<Shape> shapes = FXCollections.observableArrayList();

    private Viewport viewport;

    private List<Painter> painters = new LinkedList<>();

    private SimpleObjectProperty<PlotPoint> focusedPoint = new SimpleObjectProperty<>();

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

        createPaintingPipeline();
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

    @Override
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

    @Override
    public void clear() {
        points.clear();
        shapes.clear();
        requestRepaint();
    }

    @Override
    public void importPlot(PlotData data) throws DataFormatException {
        points.addAll(data.getPoints());
        shapes.addAll(data.getShapes());
        requestRepaint();
    }

    @Override
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

    @Override
    public WritableImage snapshot() {
        return canvas.snapshot(null, null);
    }

    @Override
    public ObservableList<PlotPoint> getPoints() {
        return points;
    }

    @Override
    public ObservableList<Shape> getShapes() {
        return shapes;
    }

    @Override
    public Viewport getViewport() {
        return viewport;
    }

    public void registerEventHandler(EventHandler eventHandler) {
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, eventHandler::mouseMoved);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, eventHandler::mousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, eventHandler::mouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, eventHandler::mouseReleased);
        canvas.addEventHandler(ScrollEvent.SCROLL, eventHandler::scrolled);
    }

    public SimpleObjectProperty<PlotPoint> focusedPointProperty() {
        return focusedPoint;
    }
}
