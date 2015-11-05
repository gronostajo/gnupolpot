package net.avensome.dev.gnupolpot.api.plotter;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.image.WritableImage;

import java.util.List;

public interface IPlotter {
    void requestRepaint();

    void clear();

    void importPlot(PlotData data) throws DataFormatException;

    void zoomAll(boolean instantRepaint);

    WritableImage snapshot();

    List<PlotPoint> getPoints();

    List<Shape> getShapes();

    Viewport getViewport();

    void setCursor(Cursor cursor);

    SimpleObjectProperty<PlotPoint> focusedPointProperty();
}
