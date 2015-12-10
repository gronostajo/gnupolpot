package net.avensome.dev.gnupolpot.api.comparator;

import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;

/**
 * Lexicographical PlotPoint comparator sorting by X, then by Y if Xs are equal.
 */
public class XyPointComparator extends CoordinateBasedPointComparator {
    @Override
    protected Double[] getDeltas(PlotPoint p1, PlotPoint p2) {
        return new Double[]{
                p1.getX() - p2.getX(),
                p1.getY() - p2.getY()
        };
    }
}
