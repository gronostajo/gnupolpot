package net.avensome.dev.gnupolpot.core.plugins;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class PluginInfo {

    private static Stage dialog;
    private static Control root;

    public static void createInfoWindow(Stage primaryStage, PluginManager pluginManager) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(PluginInfo.class.getResource("pluginInfo.fxml"));
        root = loader.load();

        dialog = new Stage();
        dialog.setTitle("Plugins");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(primaryStage);

        Scene scene = new Scene(root);
        dialog.setScene(scene);

        PluginInfoController controller = loader.getController();
        controller.setPlugins(pluginManager.getPlugins());
    }

    public static void showInfoWindow() {
        Platform.runLater(() -> {
            dialog.setMinWidth(root.getWidth());
            dialog.setMinHeight(root.getHeight());
        });
        dialog.showAndWait();
    }
}
