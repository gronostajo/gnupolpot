package net.avensome.dev.gnupolpot.core;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import net.avensome.dev.gnupolpot.core.plotter.Exporter;
import net.avensome.dev.gnupolpot.core.plotter.Importer;
import net.avensome.dev.gnupolpot.core.plotter.Plotter;
import net.avensome.dev.gnupolpot.core.plugins.PluginInfo;
import net.avensome.dev.gnupolpot.core.plugins.PluginInterface;
import net.avensome.dev.gnupolpot.core.tools.MovingTool;
import net.avensome.dev.gnupolpot.core.tools.PanningTool;
import net.avensome.dev.gnupolpot.core.tools.PolygonTool;
import net.avensome.dev.gnupolpot.core.ui.AddPointsDialog;
import net.avensome.dev.gnupolpot.core.ui.FeatureMenuAppender;
import net.avensome.dev.gnupolpot.core.ui.SummaryDialog;
import net.avensome.dev.gnupolpot.core.ui.ToolPaneAppender;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    @FXML
    private Plotter plotter;
    @FXML
    private Label statusLabel;
    @FXML
    private VBox toolPane;

    @FXML
    private Button saveButton;
    @FXML
    private Button saveAsButton;
    @FXML
    private MenuButton featureButton;
    @FXML
    private Button summaryButton;

    private Stage primaryStage;
    private Scene scene;

    private SimpleObjectProperty<File> currentFile = new SimpleObjectProperty<>(null);

    private SimpleObjectProperty<Tool> currentTool = new SimpleObjectProperty<>();

    private PluginInterface pluginInterface;
    private FeatureMenuAppender featureMenuAppender;
    private ToolPaneAppender toolPaneAppender;

    private static KeyCode[] toolKeyCodes = {
            KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4, KeyCode.DIGIT5,
            KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.DIGIT0
    };

    private final List<Tool> tools = new ArrayList<>();

    @FXML
    private void newClicked() {
        if (plotter.getPoints().size() == 0) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Discard current plot?", ButtonType.OK, ButtonType.CANCEL);// TODO don't display if saved and unchanged
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.get() == ButtonType.OK) {
            plotter.clear();
            summaryButton.setDisable(true);
            pluginInterface.setStatus("");
            currentFile.set(null);
        }
    }

    @FXML
    private void openClicked() {
        Optional<ButtonType> result;
        if (plotter.getPoints().size() > 0) {
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

        importFile(file);
    }

    private void importFile(File file) {
        try {
            PlotData importedPlot = Importer.fromStream(new FileInputStream(file));
            plotter.importPlot(importedPlot);
            pluginInterface.setStatus(String.format("Loaded %d points, %d shapes",
                    importedPlot.getPoints().size(),
                    importedPlot.getShapes().size()));
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

    @FXML
    private void saveClicked() {
        if (currentFile.get() == null) {    // shouldn't be possible, but let's ensure it works at least a little bit
            saveAsClicked();
            return;
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
        PlotData plotData = new PlotData(plotter.getPoints(), plotter.getShapes());
        String output = Exporter.toString(plotData);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            error.setHeaderText("Saving failed");
            error.showAndWait();
        }
    }

    @FXML
    private void addPointClicked() {
        PlotPoint addedPoint = new AddPointsDialog().show();
        if (addedPoint != null) {
            plotter.getPoints().add(addedPoint);
            plotter.requestRepaint();
        }
    }

    @FXML
    private void pluginInfoClicked() {
        PluginInfo.showInfoWindow();
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
            importFile(inFile);
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

        ListChangeListener<PlotPoint> pointsChange = listChange -> updateControlsDisabledState();
        plotter.getPoints().addListener(pointsChange);
        updateControlsDisabledState();

        currentFile.addListener((observable, oldValue, newValue) -> updateControlsDisabledState());

        pluginInterface = new PluginInterface(plotter, statusLabel, currentTool, toolPane);
        featureMenuAppender = new FeatureMenuAppender(featureButton, pluginInterface);

        toolPaneAppender = new ToolPaneAppender(pluginInterface, toolPane);
        registerTool(PanningTool.getInstance());
        registerTool(MovingTool.getInstance());
        registerTool(PolygonTool.getInstance());
        pluginInterface.selectDefaultTool();

        plotter.registerEventHandler(new EventHandler() {
            @Override
            public void mouseMoved(MouseEvent event, boolean focusedPointChanged) {
                currentTool.get().receiveMouseEvent(pluginInterface, MouseEventType.MOVED, event, focusedPointChanged);
            }

            @Override
            public void mousePressed(MouseEvent event) {
                currentTool.get().receiveMouseEvent(pluginInterface, MouseEventType.PRESSED, event, false);
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                currentTool.get().receiveMouseEvent(pluginInterface, MouseEventType.DRAGGED, event, false);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                currentTool.get().receiveMouseEvent(pluginInterface, MouseEventType.RELEASED, event, false);
            }

            @Override
            public void scrolled(ScrollEvent event) {
                currentTool.get().receiveScrollEvent(pluginInterface, event);
            }
        });
    }

    private void handleKeyPressed(KeyEvent keyEvent) {
        int toolIndex = Arrays.asList(toolKeyCodes).indexOf(keyEvent.getCode());
        if (toolIndex == -1) {
            return;
        } else if (toolIndex >= tools.size()) {
            return;
        }
        Tool tool = tools.get(toolIndex);
        pluginInterface.selectTool(tool);
    }

    private void updateControlsDisabledState() {
        boolean plotEmpty = plotter.getPoints().size() == 0;
        summaryButton.setDisable(plotEmpty);
        saveButton.setDisable(plotEmpty || currentFile.isNull().get());
        saveAsButton.setDisable(plotEmpty);
    }

    public void registerFeature(Feature feature) throws PluginException {
        featureMenuAppender.addFeature(feature);
    }

    public void registerTool(Tool tool) {
        toolPaneAppender.addTool(tool);
        tools.add(tool);
    }

    public PluginInterface getPluginInterface() {
        return pluginInterface;
    }

    public void configureExternalDependencies(Stage primaryStage, Scene scene) {
        if (this.primaryStage != null) {
            throw new AssertionError("Already configured");
        }
        this.primaryStage = primaryStage;

        scene.setOnKeyPressed(this::handleKeyPressed);
    }
}
