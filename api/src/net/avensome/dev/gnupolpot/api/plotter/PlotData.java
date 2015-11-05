package net.avensome.dev.gnupolpot.api.plotter;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;

public class PlotData {
    private Collection<PlotPoint> points;
    private Collection<Shape> shapes;

    public PlotData(Collection<PlotPoint> points, Collection<Shape> shapes) {
        this.points = new ImmutableList<>(points);
        this.shapes = new ImmutableList<>(shapes);
    }

    public Collection<PlotPoint> getPoints() {
        return points;
    }

    public Collection<Shape> getShapes() {
        return shapes;
    }

    private static class ImmutableList<E> extends AbstractList<E> {

        private final ArrayList<E> items;

        public ImmutableList(Collection<E> items) {
            this.items = new ArrayList<>(items);
        }

        @Override
        public E get(int index) {
            return items.get(index);
        }

        @Override
        public int size() {
            return items.size();
        }
    }
}
