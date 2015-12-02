package net.avensome.dev.gnupolpot.core.ui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.ui.LabeledControlGridBuilder;
import net.avensome.dev.gnupolpot.core.plotter.Plotter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class SummaryDialog {
    private final Plotter plotter;

    public SummaryDialog(Plotter plotter) {
        this.plotter = plotter;
    }

    public void show() {
        List<PlotPoint> points = plotter.getActiveLayer().getPoints();

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
        Label shapeCountLabel = new Label(String.valueOf(plotter.getActiveLayer().getShapes().size()));
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
}
