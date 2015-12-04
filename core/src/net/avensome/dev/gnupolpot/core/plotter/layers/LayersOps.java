package net.avensome.dev.gnupolpot.core.plotter.layers;

import javafx.collections.ObservableList;
import net.avensome.dev.gnupolpot.api.plotter.LayerException;

public final class LayersOps {
    private LayersOps() {
        throw new AssertionError("Utility class - don't instantiate!");
    }

    public static void moveUp(ObservableList<Layer> layers, Layer layer) throws LayerException {
        int index = layers.indexOf(layer);
        if (index + 1 >= layers.size()) {
            throw new LayerException("Already on top");
        }
        layers.remove(layer);
        layers.add(index + 1, layer);
    }

    public static void moveDown(ObservableList<Layer> layers, Layer layer) throws LayerException {
        int index = layers.indexOf(layer);
        if (index - 1 < 0) {
            throw new LayerException("Already on bottom");
        }
        layers.remove(layer);
        layers.add(index - 1, layer);
    }

    public static void moveToTop(ObservableList<Layer> layers, Layer layer) {
        int index = layers.indexOf(layer);
        if (index == layers.size() - 1) {
            return;
        }
        layers.remove(layer);
        layers.add(layer);
    }

    public static void moveToBottom(ObservableList<Layer> layers, Layer layer) {
        int index = layers.indexOf(layer);
        if (index == 0) {
            return;
        }
        layers.remove(layer);
        layers.add(0, layer);
    }

    public static void merge(ObservableList<Layer> layersList, Layer... toBeMerged) {
        // TODO Implement
    }

    public static void duplicate(ObservableList<Layer> layers, Layer layer) {
        // TODO Implement
    }
}
