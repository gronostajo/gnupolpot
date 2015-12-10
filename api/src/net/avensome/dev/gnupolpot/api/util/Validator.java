package net.avensome.dev.gnupolpot.api.util;

/**
 * Can be used to place additional constraints on values provided by user.
 * @param <V> type of value being passed for validation
 */
@FunctionalInterface
public interface Validator<V> {
    boolean validate(V value);
}
