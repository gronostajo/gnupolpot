package net.avensome.dev.gnupolpot.plotter.util;

import net.avensome.dev.gnupolpot.geometry.Point;
import net.avensome.dev.gnupolpot.geometry.Rect;
import net.avensome.dev.gnupolpot.plotter.shapes.PlotPoint;

import java.util.List;
import java.util.stream.Collectors;

public class GeometryTools {
    public static List<PlotPoint> pointsRelativeToRect(List<PlotPoint> points, Rect viewportRect) {
        Point topLeftCorner = viewportRect.getTopLeftCorner();
        return points.stream()
                .filter(plotPoint -> viewportRect.contains(plotPoint.getX(), plotPoint.getY()))
                .map(plotPoint -> plotPoint.movedBy(topLeftCorner.scaled(-1)))
                .collect(Collectors.toList());
    }
}
