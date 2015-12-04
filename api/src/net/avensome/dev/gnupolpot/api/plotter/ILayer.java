package net.avensome.dev.gnupolpot.api.plotter;

import java.util.Set;

public interface ILayer {
    String getLabel();

    void setLabel(String label);

    Set<PlotPoint> getPoints();

    Set<Shape> getShapes();
}
