package net.avensome.dev.gnupolpot.api.comparator;

import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;

import java.util.Comparator;

abstract class CoordinateBasedPointComparator implements Comparator<PlotPoint> {
    protected abstract Double[] getDeltas(PlotPoint p1, PlotPoint p2);

    @Override
    public int compare(PlotPoint p1, PlotPoint p2) {
        Double[] deltas = getDeltas(p1, p2);
        double delta1 = deltas[0];
        double delta2 = deltas[1];
        if (delta1 != 0) {
            return sgn(delta1);
        } else {
            return sgn(delta2);
        }
    }

    protected int sgn(double v) {
        if (v < 0) {
            return -1;
        } else if (v > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
