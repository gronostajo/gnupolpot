package net.avensome.dev.gnupolpot.core.plotter.layers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import net.avensome.dev.gnupolpot.api.plotter.ILayer;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.plotter.Shape;

public final class Layer implements ILayer {
    private String label;
    private boolean invalid = false;

    private final ObservableSet<PlotPoint> points = FXCollections.observableSet();
    private final ObservableSet<Shape> shapes = FXCollections.observableSet();

    Layer(String label) {
        this.label = label;

        SetChangeListener<Shape> shapesChangeListener = change -> {
            if (change.wasAdded()) {
                points.addAll(change.getElementAdded().getPoints());
            }
        };
        shapes.addListener(shapesChangeListener);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
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
