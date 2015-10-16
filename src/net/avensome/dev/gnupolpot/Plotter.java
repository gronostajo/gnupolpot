package net.avensome.dev.gnupolpot;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.geometry.Rect;
import net.avensome.dev.gnupolpot.painters.BackgroundPainter;
import net.avensome.dev.gnupolpot.painters.Painter;
import net.avensome.dev.gnupolpot.painters.PointPainter;
import net.avensome.dev.gnupolpot.shapes.PlotPoint;
import net.avensome.dev.gnupolpot.shapes.Shape;

import java.util.LinkedList;
import java.util.List;

public class Plotter extends Pane {
    private final Canvas canvas = new Canvas();
    private boolean requiresRepaint;

    private List<PlotPoint> points = FXCollections.observableArrayList();
    private List<Shape> shapes = FXCollections.observableArrayList();

    private SimpleObjectProperty<Rect> viewportRect = new SimpleObjectProperty<>();

    private List<Painter> painters = new LinkedList<>();

    public Plotter() {
        super();

        getChildren().add(canvas);

        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        viewportRect.set(new Rect(0, 0, widthProperty().doubleValue(), heightProperty().doubleValue()));

        ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
            viewportRect.set(new Rect(0, 0, widthProperty().doubleValue(), heightProperty().doubleValue()));
            requestRepaint();
        };
        widthProperty().addListener(resizeListener);
        heightProperty().addListener(resizeListener);

        createPaintingPipeline();
        repaint();
    }

    private void createPaintingPipeline() {
        BackgroundPainter backgroundPainter = new BackgroundPainter(canvas.getGraphicsContext2D(), Color.WHITE);
        painters.add(backgroundPainter);

        PointPainter pointPainter = new PointPainter(canvas.getGraphicsContext2D(), points);
        painters.add(pointPainter);
    }

    private void requestRepaint() {
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
            painter.paint(viewportRect.get());
        }
    }

    public void clear() {
        shapes.clear();
        points.clear();
        requestRepaint();
    }

    public List<PlotPoint> getPoints() {
        return points;
    }

    public List<Shape> getShapes() {
        return shapes;
    }
}
