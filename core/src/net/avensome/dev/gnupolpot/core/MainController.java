package net.avensome.dev.gnupolpot.core;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.PluginException;
import net.avensome.dev.gnupolpot.api.Tool;
import net.avensome.dev.gnupolpot.api.mouse.MouseEventType;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.core.plotter.EventHandler;
import net.avensome.dev.gnupolpot.core.plotter.Plotter;
import net.avensome.dev.gnupolpot.core.plotter.layers.LayersController;
import net.avensome.dev.gnupolpot.core.plotter.painters.ViewportCenterPainter;
import net.avensome.dev.gnupolpot.core.plugins.PluginInterface;
import net.avensome.dev.gnupolpot.core.tools.MovingTool;
import net.avensome.dev.gnupolpot.core.tools.PanningTool;
import net.avensome.dev.gnupolpot.core.tools.PointsTool;
import net.avensome.dev.gnupolpot.core.tools.PolygonTool;
import net.avensome.dev.gnupolpot.core.ui.ToolPaneAppender;
import net.avensome.dev.gnupolpot.core.ui.ToolbarController;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

// TODO Point labels
// TODO On-canvas coordinates and scale
public class MainController implements Initializable {
    @FXML
    private Plotter plotter;
    @FXML
    private VBox toolPane;

    @FXML
    private ToolBar statusBar;
    @FXML
    private GridPane statusGrid;
    @FXML
    private Label statusLabel;
    @FXML
    private CheckBox centerCheckBox;
    @FXML
    private Label zoomLabel;

    @FXML
    private ToolbarController toolbarController;

    @FXML
    private TitledPane layers;
    @FXML
    private LayersController layersController;

    private Stage primaryStage;

    private SimpleObjectProperty<Tool> currentTool = new SimpleObjectProperty<>();

    private PluginInterface pluginInterface;
    private ToolPaneAppender toolPaneAppender;

    private static KeyCode[] toolKeyCodes = {
            KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4, KeyCode.DIGIT5,
            KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.DIGIT0
    };

    private final List<Tool> tools = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        plotter.focusedPointProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                pluginInterface.cancelTemporaryStatus();
            } else {
                pluginInterface.setTemporaryStatus(String.format("Point: %f ; %f", newValue.getX(), newValue.getY()));
            }
        });

        SetChangeListener<PlotPoint> pointsChange = void1 -> toolbarController.updateControlsDisabledState();
        plotter.getPointsView().addListener(pointsChange);

        Consumer<Point2D> viewportCenterUpdater = (center) -> {
            DecimalFormat df = new DecimalFormat("0.0#########");
            String x = df.format(center.getX());
            String y = df.format(center.getY());
            centerCheckBox.setText(String.format("%s; %s", x, y));
        };
        plotter.getViewport().centerProperty().addListener((observable, oldValue, newValue) ->
            viewportCenterUpdater.accept(newValue));
        viewportCenterUpdater.accept(plotter.getViewport().getCenter());

        Consumer<Double> viewportScaleUpdater = (scale) -> {
            DecimalFormat df = new DecimalFormat("0.####################");
            zoomLabel.setText(df.format(scale * 100) + "%");
        };
        plotter.getViewport().scaleProperty().addListener((observable1, oldValue, newValue) ->
            viewportScaleUpdater.accept(newValue.doubleValue()));
        viewportScaleUpdater.accept(plotter.getViewport().getScale());

        pluginInterface = new PluginInterface(plotter, statusLabel, currentTool, toolPane);

        toolPaneAppender = new ToolPaneAppender(pluginInterface, toolPane);
        registerTool(PanningTool.getInstance());
        registerTool(PointsTool.getInstance());
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

        plotter.addPainter(new ViewportCenterPainter(centerCheckBox.selectedProperty()));
        centerCheckBox.selectedProperty().addListener((void1, void2, void3) -> plotter.requestRepaint());

        layersController.configure(plotter);
        toolbarController.configure(primaryStage, plotter, pluginInterface);

        layers.prefHeightProperty().bind(plotter.heightProperty());

        statusBar.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            Insets padding = statusBar.getPadding();
            double horizontalPadding = padding.getLeft() + padding.getRight();
            statusGrid.setPrefWidth(newWidth.doubleValue() - horizontalPadding);
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

    public void registerFeature(Feature feature) throws PluginException {
        toolbarController.registerFeature(feature);
    }

    public void registerTool(Tool tool) {
        toolPaneAppender.addTool(tool);
        tools.add(tool);
    }

    public PluginInterface getPluginInterface() {
        return pluginInterface;
    }

    public void configure(Stage primaryStage, Scene scene) {
        if (this.primaryStage != null) {
            throw new AssertionError("Already configured");
        }
        this.primaryStage = primaryStage;

        scene.setOnKeyPressed(this::handleKeyPressed);
    }
}
