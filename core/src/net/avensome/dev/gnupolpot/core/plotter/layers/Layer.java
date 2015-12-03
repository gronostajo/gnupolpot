package net.avensome.dev.gnupolpot.core.plotter.layers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import net.avensome.dev.gnupolpot.api.plotter.ILayer;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.plotter.Shape;

public final class Layer implements ILayer {
    private ObservableSet<PlotPoint> points = FXCollections.observableSet();
    private ObservableSet<Shape> shapes = FXCollections.observableSet();

    private boolean invalid = false;

    Layer() {
        SetChangeListener<Shape> shapesChangeListener = change -> {
            if (change.wasAdded()) {
                points.addAll(change.getElementAdded().getPoints());
            }
        };
        shapes.addListener(shapesChangeListener);
    }

    public void invalidate() {
        this.invalid = true;
    }

    @Override
    public ObservableSet<PlotPoint> getPoints() {
        if (invalid) {
            throw new LayerInvalidatedException();
        }
        return points;
    }

    @Override
    public ObservableSet<Shape> getShapes() {
        return shapes;
    }

    private class LayerInvalidatedException extends RuntimeException {
        public LayerInvalidatedException() {
            super("Layer was deleted from plot");
        }
    }
}
