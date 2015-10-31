package net.avensome.dev.gnupolpot.api.plotter;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlotData {
    private List<PlotPoint> points;
    private List<Shape> shapes;

    public PlotData(List<PlotPoint> points, List<Shape> shapes) {
        this.points = new ImmutableList<>(points);
        this.shapes = new ImmutableList<>(shapes);
    }

    public List<PlotPoint> getPoints() {
        return points;
    }

    public List<Shape> getShapes() {
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
