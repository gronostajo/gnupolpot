package net.avensome.dev.gnupolpot.api.util;

public interface Validator<V> {
    boolean validate(V value);
}
