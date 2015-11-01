package net.avensome.dev.gnupolpot.core.geometry;

public class Viewport {

    private double centerX;
    private double centerY;
    private double width;
    private double height;
    private double scale;

    public Viewport(double centerX, double centerY, double width, double height, double scale) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.scale = scale;
    }

    public double getScale() {
        return scale;
    }

    public double getLeft() {
        return centerX - width / 2;
    }

    public double getBottom() {
        return centerY - height / 2;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getRight() {
        return centerX + width / 2;
    }

    public double getTop() {
        return centerY + height / 2;
    }

    public Point getBottomLeftCorner() {
        return new Point(getLeft(), getBottom());
    }

    public void centerAt(double x, double y) {
        centerX = x;
        centerY = y;
    }

    public void moveBy(double deltaX, double deltaY) {
        centerX += deltaX / scale;
        centerY += deltaY / scale;
    }

    public void resize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void zoom(double factor) {
        scale *= factor;
    }

    public void setScalePower(int power) {
        scale = Math.pow(2, power);
    }

    public Viewport applyScale() {
        return new Viewport(centerX, centerY, width / scale, height / scale, 1);
    }

    public boolean contains(double x, double y) {
        return getLeft() <= x && getRight() >= x && getBottom() <= y && getTop() >= y;
    }

    @Override
    public String toString() {
        return String.format("%f:%f -- %f:%f (%f x %f)",
                getLeft(), getBottom(), getRight(), getTop(), getWidth(), getHeight());
    }
}
