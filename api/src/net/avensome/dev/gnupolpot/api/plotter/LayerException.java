package net.avensome.dev.gnupolpot.api.plotter;

/**
 * Thrown when a layer operation fails.
 *
 * @see ILayer
 */
public class LayerException extends Exception {
    public LayerException(String message) {
        super(message);
    }
}
