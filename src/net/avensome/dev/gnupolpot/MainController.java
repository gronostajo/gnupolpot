package net.avensome.dev.gnupolpot;

import com.google.common.collect.ImmutableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.plotter.Plotter;
import net.avensome.dev.gnupolpot.plotter.shapes.PlotPoint;
import net.avensome.dev.gnupolpot.plotter.shapes.Shape;

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
}
