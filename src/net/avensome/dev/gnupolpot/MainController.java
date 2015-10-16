package net.avensome.dev.gnupolpot;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.shapes.PlotPoint;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Plotter plotter;

    @FXML
    private void resetPlotClicked() {
        plotter.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<PlotPoint> points = plotter.getPoints();
        points.add(new PlotPoint(0, 0));
        points.add(new PlotPoint(5, 5, Color.RED));
    }
}
