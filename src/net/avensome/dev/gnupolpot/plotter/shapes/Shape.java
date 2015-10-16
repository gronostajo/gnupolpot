package net.avensome.dev.gnupolpot.plotter.shapes;

import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.geometry.Point;

import java.util.List;

public abstract class Shape {
    protected final Color color;

    public Shape(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public abstract PointSequence getSequence();

    public abstract Shape movedBy(Point delta);

    public class PointSequence {
        private final double[] x;
        private final double[] y;

        private final int length;

        protected PointSequence(List<PlotPoint> points) {
            length = points.size();
            x = new double[length];
            y = new double[length];
            for (int i = 0; i < length; i++) {
                PlotPoint point = points.get(i);
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

        public int getLength() {
            return length;
        }
    }
}
