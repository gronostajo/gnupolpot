package net.avensome.dev.gnupolpot.core.plotter;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import net.avensome.dev.gnupolpot.api.plotter.ILayer;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.plotter.Shape;
import net.avensome.dev.gnupolpot.core.util.Callback0;

import java.util.Set;

public final class Layer implements ILayer {
    private ObservableList<PlotPoint> points = FXCollections.observableArrayList();
    private ObservableList<Shape> shapes = FXCollections.observableArrayList();

    private boolean invalid = false;

    private Layer() {
        ListChangeListener<Shape> shapesChangeListener = change -> {
            change.next();
            for (Shape shape : change.getAddedSubList()) {
                points.addAll(shape.getPoints());
            }
        };
        shapes.addListener(shapesChangeListener);
    }

    static Layer create(Set<PlotPoint> pointsView, Set<Shape> shapesView, Callback0 rebuildViewsCallback) {
        Layer instance = new Layer();

        // TODO Add batch manipulation, with single rebuild after more complex operation (eg. removing multiple items one by one will result in multiple rebuilds)
        instance.points.addListener(new ViewChangeListener<>(pointsView, rebuildViewsCallback));
        instance.shapes.addListener(new ViewChangeListener<>(shapesView, rebuildViewsCallback));

        return instance;
    }

    public void invalidate() {
        this.invalid = true;
    }

    @Override
    public ObservableList<PlotPoint> getPoints() {
        if (invalid) {
            throw new LayerInvalidatedException();
        }
        return points;
    }

    @Override
    public ObservableList<Shape> getShapes() {
        return shapes;
    }

    private class LayerInvalidatedException extends RuntimeException {
        public LayerInvalidatedException() {
            super("Layer was deleted from plot");
        }
    }

    private static class ViewChangeListener<T> implements ListChangeListener<T> {
        private final Set<T> view;
        private final Callback0 rebuildRequiredCallback;

        public ViewChangeListener(Set<T> view, Callback0 rebuildRequiredCallback) {
            this.view = view;
            this.rebuildRequiredCallback = rebuildRequiredCallback;
        }

        @Override
        public void onChanged(Change<? extends T> change) {
            boolean rebuildRequired = false;
            change.next();
            if (change.wasRemoved() || change.wasReplaced()) {
                rebuildRequired = true;
            } else if (change.wasAdded()) {
                view.addAll(change.getAddedSubList());
            }
            if (rebuildRequired) {
                rebuildRequiredCallback.call();
            }
        }
    }
}
