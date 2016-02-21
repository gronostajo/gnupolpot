package net.avensome.dev.gnupolpot.api.plotter;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import net.avensome.dev.gnupolpot.api.mouse.Point;

import java.util.Collection;

public interface IViewport {
    double getScale();

    ReadOnlyDoubleProperty scaleProperty();

    double getLeft();

    double getBottom();

    double getWidth();

    double getHeight();

    double getRight();

    double getTop();

    double getCenterX();

    double getCenterY();

    Point2D getCenter();

    ReadOnlyObjectProperty<Point2D> centerProperty();

    /**
     * Changes viewport center.
     *
     * @param x new X coordinate of center
     * @param y new Y coordinate of center
     */
    void centerAt(double x, double y);

    /**
     * Moves viewport by a provided amount in both dimensions.
     *
     * @param deltaX amount to move right
     * @param deltaY amount to move up
     */
    void moveBy(double deltaX, double deltaY);

    /**
     * Change viewport zoom.
     *
     * @param factor to multiply scale by
     */
    // FIXME Either quantize this or make setScalePower accept floats
    void zoom(double factor);

    /**
     * Set scale to an absolute value
     *
     * @param power Scale power. 0 is 1:1 scale, increasing by 1 doubles scale.
     */
    void setScalePower(int power);

    /**
     * @param xCoord X coordinate
     * @return Whether this coordinate is contained in viewport.
     */
    boolean containsHorizontalCoord(double xCoord);

    /**
     * @param yCoord Y coordinate
     * @return Whether this coordinate is contained in viewport.
     */
    boolean containsVerticalCoord(double yCoord);

    /**
     * @param plotX X coordinate
     * @param plotY Y coordinate
     * @return Whether viewport contains point at these coordinates.
     */
    boolean contains(double plotX, double plotY);

    /**
     * <p>Translate point from screen coordinates to plot coordinates.
     * <p>Screen uses down-pointing Y axis and its (0,0) coordinate is in the top left corner. Plot, on the other hand,
     * has up-pointing Y axis and its (0,0) point isn't fixed. This method converts screen coordinates (eg. coordinates
     * of a mouse click) to plot coordinates.
     *
     * @param screenX X coordinate in screen coordinate system
     * @param screenY Y coordinate in screen coordinate system
     * @return A point in plot coordinate system.
     */
    Point fromScreenCoords(double screenX, double screenY);

    /**
     * Method opposite to {@link #fromScreenCoords(double, double)}. See {@link #fromScreenCoords(double, double)}
     * for details.
     *
     * @param plotX X coordinate in plot coordinate system
     * @param plotY Y coordinate in plot coordinate system
     * @return A point in screen coordinate system.
     */
    Point toScreenCoords(double plotX, double plotY);

    /**
     * Convenience method that calls {@link #toScreenCoords(double, double)}.
     *
     * @param point a plot point
     * @return Point's position in screen coordinate system
     */
    Point toScreenCoords(PlotPoint point);

    /**
     * Filters points by visibility.
     *
     * @param points a collection of points
     * @return Points from provided collection that are contained in this viewport.
     */
    Collection<PlotPoint> visiblePoints(Collection<PlotPoint> points);

    /**
     * <p>Returns a point closest to provided coordinates within provided radius.
     * <p>This method uses plot coordinates, use {@link #fromScreenCoords(double, double)} to convert from screen
     * (mouse) coordinates.
     *
     * @param x      X plot coordinate to look at
     * @param y      Y plot coordinate to look at
     * @param radius maximum allowed distance from (x,y) point
     * @param points Collection of points to search
     * @return {@link PlotPoint} from collection <i>points</i> closest to (x,y), or null if there are no points within radius
     */
    PlotPoint pointAtPlotCoords(double x, double y, double radius, Collection<PlotPoint> points);
}
