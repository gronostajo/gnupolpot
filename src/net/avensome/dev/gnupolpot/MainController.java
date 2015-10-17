package net.avensome.dev.gnupolpot;

import com.google.common.collect.ImmutableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.avensome.dev.gnupolpot.plotter.DataFormatException;
import net.avensome.dev.gnupolpot.plotter.Plotter;
import net.avensome.dev.gnupolpot.plotter.shapes.PlotPoint;
import net.avensome.dev.gnupolpot.plotter.shapes.Shape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import points");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }

        try {
            int countImported = plotter.importPlot(new FileInputStream(file));
            statusLabel.setText(String.format("Imported %d points", countImported));
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Selected file doesn't exist", ButtonType.OK);
            alert.showAndWait();
        } catch (DataFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Invalid line in data file");
            alert.showAndWait();
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
