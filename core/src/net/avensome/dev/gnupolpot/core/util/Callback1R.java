package net.avensome.dev.gnupolpot.core.util;

@FunctionalInterface
public interface Callback1R<T, R> {
    R call(T arg);
}
