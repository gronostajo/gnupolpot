package net.avensome.dev.gnupolpot.api.plotter;

import java.util.Set;

/**
 * Interface provided by layers on plot's layer stack.
 *
 * @see IPlotter
 */
public interface ILayer {
    /**
     * Get displayed label of this layer.
     * @return Layer label.
     */
    String getLabel();

    /**
     * Change label of this layer. Labels are useful only for users, they don't affect program.
     * @param label to be assigned
     */
    void setLabel(String label);

    /**
     * @return Modifiable set of points that belong to this layer.
     */
    Set<PlotPoint> getPoints();

    /**
     * @return Modifiable set of shapes that belong to this layer.
     */
    Set<Shape> getShapes();
}
