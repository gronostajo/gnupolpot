package net.avensome.dev.gnupolpot.core.plotter.layers;

import javafx.collections.SetChangeListener;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.plotter.Shape;
import net.avensome.dev.gnupolpot.core.util.Callback1R;

import java.util.List;
import java.util.Set;

public final class LayerBinder {
    private LayerBinder() {
        throw new AssertionError("Utility class - don't instantiate!");
    }

    public static Layer createAndBindLayer(Set<PlotPoint> pointsView, Set<Shape> shapesView, List<Layer> layers) {
        Layer instance = new Layer();

        // TODO Add batch manipulation, with single rebuild after more complex operation (eg. removing multiple items one by one will result in multiple rebuilds)
        instance.getPoints().addListener(new ViewChangeListener<>(pointsView, point -> layers.stream()
                .map(layer -> layer.getPoints().contains(point))
                .reduce(false, Boolean::logicalOr)));
        instance.getShapes().addListener(new ViewChangeListener<>(shapesView, shape -> layers.stream()
                .map(layer -> layer.getShapes().contains(shape))
                .reduce(false, Boolean::logicalOr)));

        return instance;
    }

    private static class ViewChangeListener<T> implements SetChangeListener<T> {
        private final Set<T> view;
        private final Callback1R<T, Boolean> isStillUsedCallback;

        public ViewChangeListener(Set<T> view, Callback1R<T, Boolean> isStillUsedCallback) {
            this.view = view;
            this.isStillUsedCallback = isStillUsedCallback;
        }

        @Override
        public void onChanged(Change<? extends T> change) {
            if (change.wasAdded()) {
                view.add(change.getElementAdded());
            }
            if (change.wasRemoved()) {
                T removedItem = change.getElementRemoved();
                if (!isStillUsedCallback.call(removedItem)) {
                    view.remove(removedItem);
                }
            }
        }
    }
}
