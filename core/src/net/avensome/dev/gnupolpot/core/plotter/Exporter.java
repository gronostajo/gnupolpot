package net.avensome.dev.gnupolpot.core.plotter;

import net.avensome.dev.gnupolpot.api.plotter.PlotData;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.plotter.Shape;
import net.avensome.dev.gnupolpot.api.util.FxUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Exporter {
    public static String toString(PlotData plotData) {
        Map<PlotPoint, String> pointIds = new HashMap<>();

        StringBuilder finalBuilder = new StringBuilder();
        int pointCounter = 0;
        for (PlotPoint point : plotData.getPoints()) {
            String color = FxUtils.colorToHex(point.getColor());
            String id = String.format("*point_%d", pointCounter);
            String spec = String.format(Locale.ENGLISH, "%.20f %.20f %s %s\n", point.getX(), point.getY(), color, id);

            finalBuilder.append(spec);
            pointIds.put(point, id);

            pointCounter++;
        }

        for (Shape shape : plotData.getShapes()) {
            String color = FxUtils.colorToHex(shape.getColor());
            String type = shape.getType().toString().toLowerCase();
            String stub = String.format("$ %s _%s", color, type);

            StringBuilder builder = new StringBuilder(stub);
            for (PlotPoint point : shape.getPoints()) {
                builder.append(" ").append(pointIds.get(point));
            }
            builder.append("\n");

            finalBuilder.append(builder);
        }

        return finalBuilder.toString();
    }
}
