package net.avensome.dev.gnupolpot.plotter.util;

import net.avensome.dev.gnupolpot.geometry.Rect;
import net.avensome.dev.gnupolpot.plotter.shapes.PlotPoint;

import java.util.List;
import java.util.stream.Collectors;

public class GeometryTools {
    public static List<PlotPoint> pointsInRect(List<PlotPoint> points, Rect rect) {
        return points.stream()
                .filter(plotPoint -> rect.contains(plotPoint.getX(), plotPoint.getY()))
                .collect(Collectors.toList());
    }
}
