package net.avensome.dev.gnupolpot.plugin.generator;

import javafx.scene.paint.Color;

public class RectangleGeneratorParams {
    private final int count;
    private final double left;
    private final double top;
    private final double right;
    private final double bottom;
    private final Color color;
    private final int seed;

    public RectangleGeneratorParams() {
        this(100, -10, -10, 10, 10, Color.BLACK, 0);
    }

    public RectangleGeneratorParams(int count, double left, double top, double right, double bottom, Color color, int seed) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be >= 1");
        }
        this.count = count;
        if (left <= right) {
            this.left = left;
            this.right = right;
        } else {
            this.left = right;
            this.right = left;
        }
        if (top > bottom) {
            this.top = top;
            this.bottom = bottom;
        } else {
            this.top = bottom;
            this.bottom = top;
        }
        this.color = color;
        this.seed = seed;
    }

    public int getCount() {
        return count;
    }

    public double getLeft() {
        return left;
    }

    public double getTop() {
        return top;
    }

    public double getRight() {
        return right;
    }

    public double getBottom() {
        return bottom;
    }

    public double getWidth() {
        return right - left;
    }

    public double getHeight() {
        return top - bottom;
    }

    public Color getColor() {
        return color;
    }

    public int getSeed() {
        return seed;
    }
}
