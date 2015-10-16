package net.avensome.dev.gnupolpot.geometry;

public class Rect {

    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public Rect(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getLeft() {
        return x;
    }

    public double getTop() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getRight() {
        return x + width;
    }

    public double getBottom() {
        return y + height;
    }

    public Point getTopLeftCorner() {
        return new Point(x, y);
    }

    public Rect resized(double newWidth, double newHeight) {
        return new Rect(x, y, newWidth, newHeight);
    }

    public Rect movedBy(double deltaX, double deltaY) {
        return new Rect(x + deltaX, y + deltaY, width, height);
    }

    public boolean overlapsHorizontally(Rect that) {
        return !((this.getLeft() > that.getRight())
                || (this.getRight() < that.getLeft()));
    }

    public boolean overlapsVertically(Rect that) {
        return !((this.getTop() > that.getBottom())
                || (this.getBottom() < that.getTop()));
    }

    public boolean overlaps(Rect that) {
        return !((this.getLeft() > that.getRight())
                || (this.getRight() < that.getLeft())
                || (this.getTop() > that.getBottom())
                || (this.getBottom() < that.getTop()));
    }

    public boolean contains(double x, double y) {
        return getLeft() <= x && getRight() >= x && getTop() <= y && getBottom() >= y;
    }

    @Override
    public String toString() {
        return String.format("%f:%f-%f:%f (%fx%f)",
                getLeft(), getTop(), getRight(), getBottom(), getWidth(), getHeight());
    }
}
