package net.avensome.dev.gnupolpot.api.plotter;

import java.util.Set;

public interface ILayer {
    Set<PlotPoint> getPoints();

    Set<Shape> getShapes();
}
