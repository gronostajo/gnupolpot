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

        Scene scene = new Scene(root);

        MainController mainController = loader.getController();
        mainController.configureExternalDependencies(primaryStage, scene);

        primaryStage.setTitle("gnupolpot");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(560);
        primaryStage.show();

        PluginManager pluginManager = new PluginManager();

        for (Feature feature : pluginManager.getFeatures()) {
            try {
                mainController.registerFeature(feature);
            } catch (Exception e) {
                PluginException.showAlert(e);
            }
        }

        pluginManager.getTools().forEach(mainController::registerTool);

        int pluginCount = pluginManager.getPlugins().size();
        if (pluginCount > 0) {
            mainController.getPluginInterface().setStatus(String.format("%d plugin(s) loaded", pluginCount));
        } else {
            mainController.getPluginInterface().setStatus("No plugins found");
        }

        PluginInfo.createInfoWindow(primaryStage, pluginManager);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
