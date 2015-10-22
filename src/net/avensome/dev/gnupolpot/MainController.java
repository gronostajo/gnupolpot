package net.avensome.dev.gnupolpot;

import com.google.common.collect.ImmutableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.avensome.dev.gnupolpot.geometry.Viewport;
import net.avensome.dev.gnupolpot.plotter.DataFormatException;
import net.avensome.dev.gnupolpot.plotter.Plotter;
import net.avensome.dev.gnupolpot.plotter.PointImporter;
import net.avensome.dev.gnupolpot.plotter.shapes.PlotPoint;
import net.avensome.dev.gnupolpot.plotter.shapes.Shape;

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

    @FXML private Plotter plotter;
    @FXML private Label statusLabel;
    @FXML private Button importAgainButton;

    @FXML
    private void resetPlotClicked() {
        plotter.clear();
        statusLabel.setText("Plot cleared");
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
        fileChooser.setTitle("Import points");
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
    private void zoomAllClicked() {
        List<PlotPoint> points = plotter.getPoints();
        Viewport viewport = plotter.getViewport();

        if (points.size() == 0) {
            viewport.centerAt(0, 0);
            plotter.requestRepaint();
            return;
        } else if (points.size() == 1) {
            PlotPoint point = points.get(0);
            viewport.centerAt(point.getX(), point.getY());
            plotter.requestRepaint();
            return;
        }

        double minX = points.stream().map(PlotPoint::getX).reduce(Math::min).get();
        double maxX = points.stream().map(PlotPoint::getX).reduce(Math::max).get();
        double minY = points.stream().map(PlotPoint::getY).reduce(Math::min).get();
        double maxY = points.stream().map(PlotPoint::getY).reduce(Math::max).get();

        double spreadX = (maxX - minX);
        double spreadY = (maxY - minY);

        double viewportSpread = Math.max(spreadX / viewport.getWidth(), spreadY / viewport.getHeight());
        double scale = Math.log(viewportSpread) / Math.log(2);  // log_e(x) / log_e(2) == log_2(x)

        double centerX = (minX + maxX) / 2;
        double centerY = (minY + maxY) / 2;

        viewport.setScalePower((int) -Math.round(scale));
        viewport.centerAt(centerX, centerY);
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

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Min X:"), 0, 0);
        grid.add(new Label("Max X:"), 0, 1);
        grid.add(new Label("Min Y:"), 0, 2);
        grid.add(new Label("Max Y:"), 0, 3);
        grid.add(new Label("X spread:"), 0, 4);
        grid.add(new Label("Y spread:"), 0, 5);

        NumberFormat formatter = new DecimalFormat("#0.0000000000");
        grid.add(new Label(formatter.format(minX)), 1, 0);
        grid.add(new Label(formatter.format(maxX)), 1, 1);
        grid.add(new Label(formatter.format(minY)), 1, 2);
        grid.add(new Label(formatter.format(maxY)), 1, 3);
        grid.add(new Label(formatter.format(spreadX)), 1, 4);
        grid.add(new Label(formatter.format(spreadY)), 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<PlotPoint> points = plotter.getPoints();
        List<Shape> shapes = plotter.getShapes();

        points.add(new PlotPoint(0, 0));
        points.add(new PlotPoint(5, 5, Color.RED));

        List<PlotPoint> shapePoints = ImmutableList.of(
                new PlotPoint(30, 20),
                new PlotPoint(40, 30),
                new PlotPoint(35, 50),
                new PlotPoint(20, 50),
                new PlotPoint(15, 40)
        );
        points.addAll(shapePoints);
        shapes.add(new Shape(shapePoints, Color.GREEN));

        ImmutableList<PlotPoint> linePoints = ImmutableList.of(
                new PlotPoint(60, 70),
                new PlotPoint(70, 90)
        );
        points.addAll(linePoints);
        shapes.add(new Shape(linePoints, Color.PINK));
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void importPointsFromFile(File file, boolean replacePlot) {
        try {
            List<PlotPoint> importedPoints = PointImporter.fromStream(new FileInputStream(file));
            if (replacePlot) {
                plotter.clear();
            }
            plotter.importPoints(importedPoints);
            statusLabel.setText(String.format("Imported %d points", importedPoints.size()));
            importAgainButton.setDisable(!replacePlot);
        } catch (FileNotFoundException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, "Selected file doesn't exist", ButtonType.OK);
            error.showAndWait();
        } catch (DataFormatException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            error.setHeaderText("Invalid line in data file");
            error.showAndWait();
        }
    }
}
