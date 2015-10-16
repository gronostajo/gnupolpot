package net.avensome.dev.gnupolpot.shapes;

import net.avensome.dev.gnupolpot.geometry.Point;

import java.util.List;

public abstract class Shape {
    public abstract PointSequence getSequence();

    protected class PointSequence {
        private final double[] x;
        private final double[] y;
        private final int length;

        public PointSequence(List<Point> points) {
            length = points.size();
            x = new double[length];
            y = new double[length];
            for (int i = 0; i < length; i++) {
                Point point = points.get(i);
                x[i] = point.getX();
                y[i] = point.getY();
            }
        }

        public double[] getX() {
            return x;
        }

        public double[] getY() {
            return y;
        }
    }
}
