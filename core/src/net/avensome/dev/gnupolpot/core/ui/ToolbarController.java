package net.avensome.dev.gnupolpot.core.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.PluginException;
import net.avensome.dev.gnupolpot.api.plotter.DataFormatException;
import net.avensome.dev.gnupolpot.api.plotter.PlotData;
import net.avensome.dev.gnupolpot.api.util.ImageUtil;
import net.avensome.dev.gnupolpot.core.plotter.Exporter;
import net.avensome.dev.gnupolpot.api.plotter.Importer;
import net.avensome.dev.gnupolpot.core.plotter.Plotter;
import net.avensome.dev.gnupolpot.core.plotter.layers.Layer;
import net.avensome.dev.gnupolpot.core.plugins.PluginInfo;
import net.avensome.dev.gnupolpot.core.plugins.PluginInterface;

import java.io.*;
import java.util.List;
import java.util.Optional;

public final class ToolbarController {
    @FXML
    private Button saveButton;
    @FXML
    private Button saveAsButton;
    @FXML
    private MenuButton featureButton;
    @FXML
    private Button summaryButton;

    private Stage primaryStage;
    private Plotter plotter;
    private PluginInterface pluginInterface;

    private FeatureMenuAppender featureMenuAppender;

    private SimpleObjectProperty<File> currentFile = new SimpleObjectProperty<>(null);

    public void configure(Stage primaryStage, Plotter plotter, PluginInterface pluginInterface) {
        this.primaryStage = primaryStage;
        this.plotter = plotter;
        this.pluginInterface = pluginInterface;

        currentFile.addListener((observable, oldValue, newValue) -> updateControlsDisabledState());

        featureMenuAppender = new FeatureMenuAppender(featureButton, pluginInterface);

        updateControlsDisabledState();
    }

    @FXML
    private void newClicked() {
        if (plotter.isPristine()) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Discard current plot?", ButtonType.OK, ButtonType.CANCEL);// TODO don't display if saved and unchanged
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.get() == ButtonType.OK) {
            plotter.clear();
            updateControlsDisabledState();
            pluginInterface.setStatus("");
            currentFile.set(null);
        }
    }

    @FXML
    private void openClicked() {
        Optional<ButtonType> result;
        if (!plotter.isPristine()) {
            Alert clearPrompt = new Alert(Alert.AlertType.CONFIRMATION, "Discard current plot and load new from file?");   // TODO don't display if saved and unchanged
            clearPrompt.setTitle("gnupolpot");
            clearPrompt.setHeaderText(null);
            clearPrompt.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            result = clearPrompt.showAndWait();
            if (result.get() != ButtonType.OK) {
                return;
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open plot");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }

        importFile(file);
        currentFile.set(file);
    }

    @FXML
    private void importClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import data from file");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }

        boolean wasPristine = plotter.isPristine();
        Layer previouslyActiveLayer = plotter.getActiveLayer();
        Layer newLayer = plotter.createLayerAbove(previouslyActiveLayer, "Imported layer");
        if (wasPristine) {
            plotter.deleteLayer(previouslyActiveLayer);
        }
        plotter.selectActiveLayer(newLayer);
        importFile(file);
    }

    private void importFile(File file) {
        try {
            PlotData importedPlot = Importer.fromStream(new FileInputStream(file));
            plotter.getActiveLayer().getPoints().addAll(importedPlot.getPoints());
            plotter.getActiveLayer().getShapes().addAll(importedPlot.getShapes());
            plotter.requestRepaint();

            pluginInterface.setStatus(String.format("Loaded %d points, %d shapes",
                    importedPlot.getPoints().size(),
                    importedPlot.getShapes().size()));
            updateControlsDisabledState();
        } catch (FileNotFoundException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, "Selected file doesn't exist", ButtonType.OK);
            error.showAndWait();
        } catch (DataFormatException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            error.setHeaderText("Invalid line in data file");
            error.showAndWait();
        }
    }

    @FXML
    private void saveClicked() {
        if (currentFile.get() == null) {
            throw new RuntimeException("Illegal state");
        }
        saveToFile(currentFile.get());
    }

    @FXML
    private void saveAsClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save plot");
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file == null) {
            return;
        }

        saveToFile(file);
        currentFile.set(file);
    }

    private void saveToFile(File file) {
        PlotData viewDump = new PlotData(plotter.getPointsView(), plotter.getShapesView());
        String output = Exporter.toString(viewDump);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            error.setHeaderText("Saving failed");
            error.showAndWait();
        }
    }

    // TODO point adding tool


    @FXML
    private void pluginInfoClicked() {
        PluginInfo.showInfoWindow();
    }

    @FXML
    private void zoomFitClicked() {
        plotter.zoomAll();
    }

    @FXML
    private void zoomOneToOneClicked() {
        plotter.getViewport().setScalePower(0);
        plotter.requestRepaint();
    }

    @FXML
    private void centerClicked() {
        plotter.getViewport().centerAt(0, 0);
        plotter.requestRepaint();
    }

    @FXML
    private void snapshotClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save snapshot");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG image", "*.png"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file == null) {
            return;
        }

        ImageUtil.saveToFile(plotter.getSnapshot(), file);
    }

    @FXML
    private void renderClicked() {
        if (!plotter.isPristine()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("gnupolpot");
            confirm.setHeaderText(null);
            confirm.setContentText("This will erase current plot. Continue?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != ButtonType.OK) {
                return;
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Render plot from file");
        List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
        if (files == null) {
            return;
        }

        for (File inFile : files) {
            importFile(inFile);
            plotter.zoomAll();
            plotter.requestLayout();
            File outFile = new File(inFile.getPath() + ".png");
            ImageUtil.saveToFile(plotter.getSnapshot(), outFile);
        }
    }

    @FXML
    private void summaryClicked() {
        new SummaryDialog(plotter).show();
    }

    public void updateControlsDisabledState() {
        boolean plotIsPristine = plotter.isPristine();
        summaryButton.setDisable(plotIsPristine);
        saveButton.setDisable(plotIsPristine || currentFile.isNull().get());
        saveAsButton.setDisable(plotIsPristine);
    }

    public void registerFeature(Feature feature) throws PluginException {
        featureMenuAppender.addFeature(feature);
    }
}
