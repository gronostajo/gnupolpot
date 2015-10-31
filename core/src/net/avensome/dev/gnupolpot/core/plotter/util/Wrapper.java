package net.avensome.dev.gnupolpot.core.plotter.util;

public class Wrapper<T> {
    private T object;

    public Wrapper() {
    }

    public Wrapper(T object) {
        this.object = object;
    }

    public T get() {
        return object;
    }

    public boolean set(T object) {
        boolean changed = object != this.object;
        this.object = object;
        return changed;
    }

    public boolean isNotNull() {
        return object != null;
    }
}
