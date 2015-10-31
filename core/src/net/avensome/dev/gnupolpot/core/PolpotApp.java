package net.avensome.dev.gnupolpot.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PolpotApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        MainController mainController = loader.getController();
        mainController.setPrimaryStage(primaryStage);

        primaryStage.setTitle("gnupolpot");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(640);
        primaryStage.setMinHeight(400);
        primaryStage.show();

        PluginManager pluginManager = new PluginManager();
        pluginManager.registerFeatures(mainController::registerFeature);
        int pluginCount = pluginManager.getPlugins().size();
        if (pluginCount > 0) {
            mainController.setStatus(String.format("%d plugin(s) loaded", pluginCount));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
