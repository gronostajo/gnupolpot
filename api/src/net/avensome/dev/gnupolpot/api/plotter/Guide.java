package net.avensome.dev.gnupolpot.api.plotter;

import javafx.scene.paint.Color;

import java.util.Arrays;

/**
 * An orthonormal guide that is visible at any coordinates.
 */
public class Guide {
    private final Orientation orientation;
    private final double coord;
    private final Color color;
    private final double[] dashesStyle;

    /**
     * <p>Creates a guide.
     *
     * @param orientation guide orientation
     * @param coord       X coordinate for vertical guides, or Y coordinate for horizontal guides
     * @param color       guide color
     */
    public Guide(Orientation orientation, double coord, Color color) {
        this(orientation, coord, color, null);
    }

    /**
     * <p>Creates a guide.
     *
     * @param orientation guide orientation
     * @param coord       X coordinate for vertical guides, or Y coordinate for horizontal guides
     * @param color       guide color
     * @param dashesStyle dashes style, identical as for GraphicsContext:setLineDashes()
     */
    public Guide(Orientation orientation, double coord, Color color, double[] dashesStyle) {
        this.orientation = orientation;
        this.coord = coord;
        this.color = color;
        this.dashesStyle = dashesStyle;

    }

    public Orientation getOrientation() {
        return orientation;
    }

    public double getCoord() {
        return coord;
    }

    public Color getColor() {
        return color;
    }

    public double[] getDashesStyle() {
        return dashesStyle;
    }

    /**
     * Guide orientation.
     */
    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Guide guide = (Guide) o;

        return Double.compare(guide.coord, coord) == 0 && orientation == guide.orientation
                && color.equals(guide.color) && Arrays.equals(dashesStyle, guide.dashesStyle);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = orientation.hashCode();
        temp = Double.doubleToLongBits(coord);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + color.hashCode();
        result = 31 * result + Arrays.hashCode(dashesStyle);
        return result;
    }
}
