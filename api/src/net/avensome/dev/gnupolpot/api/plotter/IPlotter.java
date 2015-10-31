package net.avensome.dev.gnupolpot.api.plotter;

import javafx.scene.image.WritableImage;

import java.util.List;

public interface IPlotter {
    void queueRepaint();

    void clear();

    void importPlot(PlotData data) throws DataFormatException;

    void zoomAll(boolean instantRepaint);

    WritableImage snapshot();

    List<PlotPoint> getPoints();

    List<Shape> getShapes();
}
