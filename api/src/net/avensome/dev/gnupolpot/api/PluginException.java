package net.avensome.dev.gnupolpot.api;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class PluginException extends Exception {
    public PluginException() {
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginException(Throwable cause) {
        super(cause);
    }

    public PluginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static void showAlert(Exception e) {
        Alert error = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        error.setTitle("gnupolpot");
        error.setHeaderText("Plugin caused an error");
        error.setContentText(e.toString());
    }
}
