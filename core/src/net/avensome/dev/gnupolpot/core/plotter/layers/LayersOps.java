package net.avensome.dev.gnupolpot.core.plotter.layers;

import javafx.collections.ObservableList;
import net.avensome.dev.gnupolpot.api.plotter.LayerException;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.plotter.Shape;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static void merge(ObservableList<Layer> layersList, Layer masterLayer, Layer[] toBeMerged) {
        for (Layer subjectLayer : toBeMerged) {
            masterLayer.getPoints().addAll(subjectLayer.getPoints());
            masterLayer.getShapes().addAll(subjectLayer.getShapes());
        }
        layersList.removeAll(toBeMerged);
    }

    public static void duplicate(Layer source, Layer target) {
        Map<PlotPoint, PlotPoint> newPoints = source.getPoints().stream()
                .collect(Collectors.toMap(
                        point -> point,
                        point -> new PlotPoint(point.getX(), point.getY())
                ));
        Collection<Shape> newShapes = source.getShapes().stream()
                .map(shape -> {
                    List<PlotPoint> shapePoints = shape.getPoints().stream()
                            .map(newPoints::get)
                            .collect(Collectors.toList());
                    return new Shape(shapePoints, shape.getColor(), shape.getType());
                })
                .collect(Collectors.toList());

        target.getPoints().addAll(newPoints.values());
        target.getShapes().addAll(newShapes);
    }
}
