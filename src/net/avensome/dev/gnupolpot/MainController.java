package net.avensome.dev.gnupolpot;

import com.google.common.collect.ImmutableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Stage primaryStage;

    @FXML
    private Plotter plotter;

    @FXML
    private Label statusLabel;

    @FXML
    private void resetPlotClicked() {
        plotter.clear();
        statusLabel.setText("Plot cleared");
    }

    @FXML
    private void importPointsClicked() {
        Alert clearPrompt = new Alert(Alert.AlertType.NONE, "Replace current plot or add new points?");
        clearPrompt.setTitle("gnupolpot");
        clearPrompt.setHeaderText(null);
        ButtonType replaceType = new ButtonType("Replace", ButtonBar.ButtonData.YES);
        ButtonType addType = new ButtonType("Add", ButtonBar.ButtonData.NO);
        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        clearPrompt.getButtonTypes().setAll(replaceType, addType, cancelType);

        Optional<ButtonType> result = clearPrompt.showAndWait();
        if (result.get() == cancelType) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import points");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }

        try {
            List<PlotPoint> importedPoints = PointImporter.fromStream(new FileInputStream(file));
            if (result.get() == replaceType) {
                plotter.clear();
            }
            plotter.importPoints(importedPoints);
            statusLabel.setText(String.format("Imported %d points", importedPoints.size()));
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
}
