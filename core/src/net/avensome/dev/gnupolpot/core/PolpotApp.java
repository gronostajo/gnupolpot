package net.avensome.dev.gnupolpot.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.PluginException;
import net.avensome.dev.gnupolpot.core.plugins.PluginInfo;
import net.avensome.dev.gnupolpot.core.plugins.PluginManager;

import java.io.IOException;

public class PolpotApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        MainController mainController = loader.getController();
        mainController.setPrimaryStage(primaryStage);

        primaryStage.setTitle("gnupolpot");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(640);
        primaryStage.setMinHeight(480);
        primaryStage.show();

        PluginManager pluginManager = new PluginManager();
        for (Feature feature : pluginManager.getFeatures()) {
            try {
                mainController.registerFeature(feature);
            } catch (Exception e) {
                PluginException.showAlert(e);
            }
        }
        int pluginCount = pluginManager.getPlugins().size();
        if (pluginCount > 0) {
            mainController.setStatus(String.format("%d plugin(s) loaded", pluginCount));
        } else {
            mainController.setStatus("No plugins found");
        }

        PluginInfo.createInfoWindow(primaryStage, pluginManager);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
