package net.avensome.dev.gnupolpot.api.plotter;

import java.util.List;

public interface ILayer {
    List<PlotPoint> getPoints();

    List<Shape> getShapes();
}
