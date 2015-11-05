package net.avensome.dev.gnupolpot.core;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.PluginException;
import net.avensome.dev.gnupolpot.api.Tool;
import net.avensome.dev.gnupolpot.api.mouse.MouseEventType;
import net.avensome.dev.gnupolpot.api.plotter.DataFormatException;
import net.avensome.dev.gnupolpot.api.plotter.PlotData;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.util.SnapshotUtil;
import net.avensome.dev.gnupolpot.core.plotter.EventHandler;
import net.avensome.dev.gnupolpot.core.plotter.Importer;
import net.avensome.dev.gnupolpot.core.plotter.Plotter;
import net.avensome.dev.gnupolpot.core.plugins.PluginInfo;
import net.avensome.dev.gnupolpot.core.plugins.PluginInterface;
import net.avensome.dev.gnupolpot.core.tools.DefaultTool;
import net.avensome.dev.gnupolpot.core.tools.TestTool;
import net.avensome.dev.gnupolpot.core.ui.AddPointsDialog;
import net.avensome.dev.gnupolpot.core.ui.FeatureMenuAppender;
import net.avensome.dev.gnupolpot.core.ui.SummaryDialog;
import net.avensome.dev.gnupolpot.core.ui.ToolPaneAppender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    @FXML
    private VBox toolPane;

    private Stage primaryStage;

    private File lastImportedFile;

    private SimpleObjectProperty<Tool> currentTool = new SimpleObjectProperty<>();

    private PluginInterface pluginInterface;
    private FeatureMenuAppender featureMenuAppender;
    private ToolPaneAppender toolPaneAppender;

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
            pluginInterface.setStatus("Plot cleared");
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

        SnapshotUtil.saveToFile(plotter.snapshot(), file);
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
            SnapshotUtil.saveToFile(plotter.snapshot(), outFile);
        }
    }

    @FXML
    private void summaryClicked() {
        new SummaryDialog(plotter).show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        plotter.focusedPointProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                pluginInterface.cancelTemporaryStatus();
            } else {
                pluginInterface.setTemporaryStatus(String.format("Point: %f ; %f", newValue.getX(), newValue.getY()));
            }
        });

        ListChangeListener<PlotPoint> pointsChange = listChange ->
                summaryButton.setDisable(plotter.getPoints().size() == 0);
        plotter.getPoints().addListener(pointsChange);

        pluginInterface = new PluginInterface(plotter, statusLabel, currentTool, toolPane);
        featureMenuAppender = new FeatureMenuAppender(featureButton, pluginInterface);

        toolPaneAppender = new ToolPaneAppender(pluginInterface, toolPane);
        toolPaneAppender.addTool(DefaultTool.getInstance());
        toolPaneAppender.addTool(new TestTool());
        pluginInterface.selectDefaultTool();

        plotter.registerEventHandler(new EventHandler() {
            @Override
            public void mouseMoved(MouseEvent event) {
                currentTool.get().receiveMouseEvent(pluginInterface, MouseEventType.MOVED, event);
            }

            @Override
            public void mousePressed(MouseEvent event) {
                currentTool.get().receiveMouseEvent(pluginInterface, MouseEventType.PRESSED, event);
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                currentTool.get().receiveMouseEvent(pluginInterface, MouseEventType.DRAGGED, event);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                currentTool.get().receiveMouseEvent(pluginInterface, MouseEventType.RELEASED, event);
            }

            @Override
            public void scrolled(ScrollEvent event) {
                currentTool.get().receiveScrollEvent(pluginInterface, event);
            }
        });
    }

    private void importPointsFromFile(File file, boolean replacePlot) {
        try {
            PlotData importedPlot = Importer.fromStream(new FileInputStream(file));
            if (replacePlot) {
                plotter.clear();
            }
            plotter.importPlot(importedPlot);
            pluginInterface.setStatus(String.format("Imported %d points, %d shapes",
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

    public void registerFeature(Feature feature) throws PluginException {
        featureMenuAppender.addFeature(feature);
    }

    public void registerTool(Tool tool) {
        toolPaneAppender.addTool(tool);
    }

    public PluginInterface getPluginInterface() {
        return pluginInterface;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
