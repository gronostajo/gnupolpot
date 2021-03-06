package net.avensome.dev.gnupolpot.core.plotter;

import javafx.beans.property.*;
import javafx.geometry.Point2D;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.plotter.IViewport;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <p>Represents plot viewport. Determines which part of the plot is painted.
 * <p>Viewport is a rectangle in scale. When scale equals 1, distances between plot points exactly match screen distance
 * measured in pixels. Increasing scale makes stuff appear bigger.
 */
public class Viewport implements IViewport {
    private final ObjectProperty<Point2D> center = new SimpleObjectProperty<>(null);
    private double width;
    private double height;
    private SimpleDoubleProperty scale = new SimpleDoubleProperty();

    /**
     * @param centerX X coordinate of center point
     * @param centerY Y coordinate of center point
     * @param width   actual width of the viewport
     * @param height  actual height of the viewport
     * @param scale   plot scale
     */
    public Viewport(double centerX, double centerY, double width, double height, double scale) {
        this.center.set(new Point2D(centerX, centerY));
        this.width = width;
        this.height = height;
        this.scale.set(scale);
    }

    @Override
    public double getScale() {
        return scale.get();
    }

    @Override
    public ReadOnlyDoubleProperty scaleProperty() {
        return scale;
    }

    @Override
    public double getLeft() {
        return center.get().getX() - Math.floor(width / 2);
    }

    @Override
    public double getBottom() {
        return center.get().getY() - Math.floor(height / 2);
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public double getRight() {
        return getLeft() + width;
    }

    @Override
    public double getTop() {
        return getBottom() + height;
    }

    @Override
    public double getCenterX() {
        return center.get().getX();
    }

    @Override
    public double getCenterY() {
        return center.get().getY();
    }

    @Override
    public Point2D getCenter() {
        return center.get();
    }

    @Override
    public ReadOnlyObjectProperty<Point2D> centerProperty() {
        return center;
    }

    @Override
    public void centerAt(double x, double y) {
        center.set(new Point2D(x, y));
    }

    @Override
    public void moveBy(double deltaX, double deltaY) {
        center.set(new Point2D(getCenterX() + deltaX / scale.get(), getCenterY() + deltaY / scale.get()));
    }

    public void resize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void zoom(double factor) {
        scale.set(scale.get() * factor);
    }

    @Override
    public void setScalePower(int power) {
        scale.set(Math.pow(2, power));
    }

    private Viewport applyScale() {
        return new Viewport(getCenterX(), getCenterY(), width / scale.get(), height / scale.get(), 1);
    }

    @Override
    public boolean containsHorizontalCoord(double xCoord) {
        return getLeft() <= xCoord
                && getRight() >= xCoord;
    }

    @Override
    public boolean containsVerticalCoord(double yCoord) {
        return getBottom() <= yCoord
                && getTop() >= yCoord;
    }

    @Override
    public boolean contains(double plotX, double plotY) {
        return containsHorizontalCoord(plotX) && containsVerticalCoord(plotY);
    }

    @Override
    public Point fromScreenCoords(double screenX, double screenY) {
        Viewport scaledViewport = applyScale();
        double x = scaledViewport.getLeft() + screenX / scale.get();
        double y = scaledViewport.getTop() - screenY / scale.get();
        return new Point(x, y);
    }

    @Override
    public Point toScreenCoords(double plotX, double plotY) {
        Viewport scaledViewport = applyScale();
        double x = (plotX - scaledViewport.getLeft()) * scale.get();
        double y = -(plotY - scaledViewport.getTop()) * scale.get();
        return new Point(x, y);
    }

    @Override
    public Point toScreenCoords(PlotPoint point) {
        return toScreenCoords(point.getX(), point.getY());
    }

    @Override
    public Collection<PlotPoint> visiblePoints(Collection<PlotPoint> points) {
        Viewport scaledViewpoint = applyScale();
        return points.stream()
                .filter(plotPoint -> scaledViewpoint.contains(plotPoint.getX(), plotPoint.getY()))
                .collect(Collectors.toList());
    }

    @Override
    public PlotPoint pointAtPlotCoords(double x, double y, double radius, Collection<PlotPoint> points) {
        return visiblePoints(points).stream()
                .filter(point -> point.distanceFrom(x, y) * scale.get() < radius)
                .sorted((o1, o2) -> Double.compare(o1.distanceFrom(x, y), o2.distanceFrom(x, y)))
                .reduce(null, (point1, point2) -> point1 == null ? point2 : point1);
    }

    @Override
    public String toString() {
        return String.format("%f:%f -- %f:%f (%f x %f)",
                getLeft(), getBottom(), getRight(), getTop(), getWidth(), getHeight());
    }
}
