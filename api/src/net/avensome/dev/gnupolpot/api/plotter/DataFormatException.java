package net.avensome.dev.gnupolpot.api.plotter;

/**
 * Thrown when data from external source has unknown or invalid format.
 */
public class DataFormatException extends Exception {
    public DataFormatException() {
    }

    public DataFormatException(String message) {
        super(message);
    }

    public DataFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataFormatException(Throwable cause) {
        super(cause);
    }

    public DataFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
