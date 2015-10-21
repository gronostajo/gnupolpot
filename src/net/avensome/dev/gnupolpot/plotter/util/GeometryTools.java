package net.avensome.dev.gnupolpot.plotter.util;

import net.avensome.dev.gnupolpot.geometry.Viewport;
import net.avensome.dev.gnupolpot.plotter.shapes.PlotPoint;

import java.util.List;
import java.util.stream.Collectors;

public class GeometryTools {
    public static List<PlotPoint> pointsInRect(List<PlotPoint> points, Viewport viewport) {
        return points.stream()
                .filter(plotPoint -> viewport.contains(plotPoint.getX(), plotPoint.getY()))
                .collect(Collectors.toList());
    }
}
