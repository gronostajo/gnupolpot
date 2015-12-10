package net.avensome.dev.gnupolpot.api.comparator;

import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;

/**
 * Lexicographical PlotPoint comparator sorting by Y, then by X if Ys are equal.
 */
public class YxPointComparator extends CoordinateBasedPointComparator {
    @Override
    protected Double[] getDeltas(PlotPoint p1, PlotPoint p2) {
        return new Double[]{
                p1.getY() - p2.getY(),
                p1.getX() - p2.getX()
        };
    }
}
