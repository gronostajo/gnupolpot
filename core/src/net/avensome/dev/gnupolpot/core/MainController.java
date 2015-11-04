package net.avensome.dev.gnupolpot.core;

import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.PluginException;
import net.avensome.dev.gnupolpot.api.plotter.DataFormatException;
import net.avensome.dev.gnupolpot.api.plotter.PlotData;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.core.plotter.Importer;
import net.avensome.dev.gnupolpot.core.plotter.Plotter;
import net.avensome.dev.gnupolpot.core.plugins.PluginInfo;
import net.avensome.dev.gnupolpot.core.ui.AddPointsDialog;
import net.avensome.dev.gnupolpot.core.ui.FeatureMenuAppender;
import net.avensome.dev.gnupolpot.core.ui.SummaryDialog;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Plotter plotter;
    @FXML
    private Label statusLabel;
    @FXML
    private Button importAgainButton;
    @FXML
    private Button summaryButton;
    @FXML
    private MenuButton featureButton;

    private Stage primaryStage;

    private File lastImportedFile;

    private String lastPermanentStatus = "";

    private FeatureMenuAppender featureMenuAppender;

    @FXML
    private void addPointClicked() {
        PlotPoint addedPoint = new AddPointsDialog().show();
        if (addedPoint != null) {
            plotter.getPoints().add(addedPoint);
            plotter.requestRepaint();
        }
    }

    @FXML
    private void importPointsClicked() {
        ButtonType replaceType = new ButtonType("Replace", ButtonBar.ButtonData.YES);
        ButtonType addType = new ButtonType("Add", ButtonBar.ButtonData.NO);
        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        Optional<ButtonType> result;
        if (plotter.getPoints().size() > 0) {
            Alert clearPrompt = new Alert(Alert.AlertType.NONE, "Replace current plot or add new points?");
            clearPrompt.setTitle("gnupolpot");
            clearPrompt.setHeaderText(null);
            clearPrompt.getButtonTypes().setAll(replaceType, addType, cancelType);
            result = clearPrompt.showAndWait();
        } else {
            result = Optional.of(replaceType);
        }

        if (result.get() == cancelType) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import plot");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }

        importPointsFromFile(file, result.get() == replaceType);
        lastImportedFile = file;
    }

    @FXML
    private void importAgainClicked() {
        importPointsFromFile(lastImportedFile, true);
    }

    @FXML
    private void pluginInfoClicked() {
        PluginInfo.showInfoWindow();
    }

    @FXML
    private void clearPlotClicked() {
        if (plotter.getPoints().size() == 0) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Clear plot?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.get() == ButtonType.OK) {
            plotter.clear();
            summaryButton.setDisable(true);
            setStatus("Plot cleared");
        }
    }

    @FXML
    private void zoomFitClicked() {
        plotter.zoomAll(false);
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

        saveSnapshotToFile(file);
    }

    @FXML
    private void renderClicked() {
        if (plotter.getPoints().size() > 0) {
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
            importPointsFromFile(inFile, true);
            plotter.zoomAll(true);
            plotter.requestLayout();
            File outFile = new File(inFile.getPath() + ".png");
            saveSnapshotToFile(outFile);
        }
    }

    @FXML
    private void summaryClicked() {
        new SummaryDialog(plotter).show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        featureMenuAppender = new FeatureMenuAppender(featureButton, plotter, this::setStatus);

        plotter.focusedPointProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                restoreStatus();
            } else {
                setTemporaryStatus(String.format("Point: %f ; %f", newValue.getX(), newValue.getY()));
            }
        });

        ListChangeListener<PlotPoint> pointsChange = listChange ->
                summaryButton.setDisable(plotter.getPoints().size() == 0);
        plotter.getPoints().addListener(pointsChange);
    }

    public void setStatus(String status) {
        lastPermanentStatus = null;
        statusLabel.setText(status);
    }

    public void setTemporaryStatus(String status) {
        if (lastPermanentStatus == null) {
            lastPermanentStatus = statusLabel.getText();
        }
        statusLabel.setText(status);
    }

    public void restoreStatus() {
        if (lastPermanentStatus != null) {
            statusLabel.setText(lastPermanentStatus);
        }
    }

    private void importPointsFromFile(File file, boolean replacePlot) {
        try {
            PlotData importedPlot = Importer.fromStream(new FileInputStream(file));
            if (replacePlot) {
                plotter.clear();
            }
            plotter.importPlot(importedPlot);
            setStatus(String.format("Imported %d points, %d shapes",
                    importedPlot.getPoints().size(),
                    importedPlot.getShapes().size()));
            importAgainButton.setDisable(!replacePlot);
            summaryButton.setDisable(plotter.getPoints().size() == 0);
        } catch (FileNotFoundException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, "Selected file doesn't exist", ButtonType.OK);
            error.showAndWait();
        } catch (DataFormatException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            error.setHeaderText("Invalid line in data file");
            error.showAndWait();
        }
    }

    private void saveSnapshotToFile(File file) {
        WritableImage image = plotter.snapshot();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            error.setHeaderText("Saving snapshot failed");
            error.showAndWait();
        }
    }

    public void registerFeature(Feature feature) throws PluginException {
        featureMenuAppender.addFeature(feature);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
