package net.avensome.dev.gnupolpot.core;

import com.google.common.collect.ImmutableList;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.plotter.DataFormatException;
import net.avensome.dev.gnupolpot.api.plotter.PlotData;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.plotter.Shape;
import net.avensome.dev.gnupolpot.api.control.DoubleTextField;
import net.avensome.dev.gnupolpot.api.util.LabeledControlGridBuilder;
import net.avensome.dev.gnupolpot.core.plotter.Importer;
import net.avensome.dev.gnupolpot.core.plotter.Plotter;
import net.avensome.dev.gnupolpot.core.plugins.PluginInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Stage primaryStage;

    private File lastImportedFile;

    private String lastPermanentStatus = "";

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
    private void addPointClicked() {
        Dialog<PlotPoint> dialog = new Dialog<>();
        dialog.setTitle("Add point");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label xLabel = new Label("X");
        Label yLabel = new Label("Y");
        Label colorLabel = new Label("Color");

        DoubleTextField xField = new DoubleTextField(0);
        DoubleTextField yField = new DoubleTextField(0);
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);

        xLabel.setPrefWidth(60);
        yLabel.setPrefWidth(60);
        colorLabel.setPrefWidth(60);
        xField.setPrefWidth(120);
        yField.setPrefWidth(120);
        colorPicker.setPrefWidth(120);

        grid.add(xLabel, 0, 0);
        grid.add(xField, 1, 0);
        grid.add(yLabel, 0, 1);
        grid.add(yField, 1, 1);
        grid.add(colorLabel, 0, 2);
        grid.add(colorPicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(xField::requestFocus);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                double x = xField.getValue();
                double y = yField.getValue();
                Color color = colorPicker.getValue();
                return new PlotPoint(x, y, color);
            } else {
                return null;
            }
        });

        Optional<PlotPoint> pointOptional = dialog.showAndWait();
        pointOptional.ifPresent(plotPoint -> {
            plotter.getPoints().add(plotPoint);
            plotter.requestRepaint();
        });
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
        List<PlotPoint> points = plotter.getPoints();

        double minX = points.stream().map(PlotPoint::getX).reduce(Math::min).get();
        double maxX = points.stream().map(PlotPoint::getX).reduce(Math::max).get();
        double minY = points.stream().map(PlotPoint::getY).reduce(Math::min).get();
        double maxY = points.stream().map(PlotPoint::getY).reduce(Math::max).get();

        double spreadX = (maxX - minX);
        double spreadY = (maxY - minY);

        Dialog dialog = new Dialog();
        dialog.setTitle("gnupolpot");
        dialog.setHeaderText("Plot summary");

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        NumberFormat formatter = new DecimalFormat("#0.0000000000");
        Label pointCountLabel = new Label(String.valueOf(points.size()));
        Label shapeCountLabel = new Label(String.valueOf(plotter.getShapes().size()));
        Label minXlabel = new Label(formatter.format(minX));
        Label maxXlabel = new Label(formatter.format(maxX));
        Label minYlabel = new Label(formatter.format(minY));
        Label maxYlabel = new Label(formatter.format(maxY));
        Label spreadXlabel = new Label(formatter.format(spreadX));
        Label spreadYlabel = new Label(formatter.format(spreadY));

        GridPane grid = new LabeledControlGridBuilder(100, 160)
                .add("Point count", pointCountLabel)
                .add("Shape count", shapeCountLabel)
                .add("Min X", minXlabel)
                .add("Max X", maxXlabel)
                .add("Min Y", minYlabel)
                .add("Max Y", maxYlabel)
                .add("X spread", spreadXlabel)
                .add("Y spread", spreadYlabel)
                .build();

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        plotter.focusedPointProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                restoreStatus();
            } else {
                setTemporaryStatus(String.format("Point: %f ; %f", newValue.getX(), newValue.getY()));
            }
        });

        // FIXME Dev content. Remove from final product.
        List<PlotPoint> points = plotter.getPoints();
        List<Shape> shapes = plotter.getShapes();

        points.add(new PlotPoint(0, 0));
        points.add(new PlotPoint(5, 5, Color.RED));

        List<PlotPoint> filledShapePoints = ImmutableList.of(
                new PlotPoint(30, 20),
                new PlotPoint(40, 30),
                new PlotPoint(35, 50),
                new PlotPoint(20, 50),
                new PlotPoint(15, 40)
        );
        points.addAll(filledShapePoints);
        shapes.add(new Shape(filledShapePoints, Color.GREEN, Shape.Type.FILLED));

        List<PlotPoint> emptyShapePoints = ImmutableList.of(
                new PlotPoint(60, 20),
                new PlotPoint(70, 50),
                new PlotPoint(50, 80),
                new PlotPoint(40, 60)
        );
        points.addAll(emptyShapePoints);
        shapes.add(new Shape(emptyShapePoints, Color.BLUE, Shape.Type.EMPTY));

        List<PlotPoint> lineShapePoints = ImmutableList.of(
                new PlotPoint(140, 20),
                new PlotPoint(180, 70),
                new PlotPoint(150, 80)
        );
        points.addAll(lineShapePoints);
        shapes.add(new Shape(lineShapePoints, Color.HOTPINK, Shape.Type.LINE));

        ImmutableList<PlotPoint> linePoints = ImmutableList.of(
                new PlotPoint(80, 70),
                new PlotPoint(90, 90)
        );
        points.addAll(linePoints);
        shapes.add(new Shape(linePoints, Color.PINK, Shape.Type.FILLED));
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

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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

    public int registerFeature(Feature feature) {
        featureButton.setDisable(false);
        ObservableList<MenuItem> menuItems = featureButton.getItems();

        MenuItem newItem = new MenuItem(feature.getMenuItem());
        newItem.setOnAction(event -> {
            String status = feature.execute(plotter);
            if (status != null) {
                statusLabel.setText(status);
            }
        });

        for (int i = 2; i < menuItems.size(); i++) {
            MenuItem menuItem = menuItems.get(i);
            if (newItem.getText().compareTo(menuItem.getText()) < 0) {
                menuItems.add(i, newItem);
                return i;
            }
        }

        menuItems.add(newItem);
        return menuItems.size() - 1;
    }
}
