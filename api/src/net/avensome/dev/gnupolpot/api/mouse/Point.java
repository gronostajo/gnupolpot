package net.avensome.dev.gnupolpot.api.mouse;

/**
 * A 2-dimensional point with basic operations.
 */
public final class Point {
    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("%f : %f", x, y);
    }

    /**
     * Subtracts another point from this one.
     * @param point a point to subtract
     * @return A point representing delta vector between this point and provided one.
     */
    public Point minus(Point point) {
        return new Point(x - point.getX(), y - point.getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;

    }

    @SuppressWarnings("Duplicates")
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
