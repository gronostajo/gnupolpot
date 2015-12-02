package net.avensome.dev.gnupolpot.api.plotter;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.image.WritableImage;

import java.util.List;

public interface IPlotter {
    void requestRepaint();

    boolean isPristine();

    void clear();

    void importPlot(PlotData data) throws DataFormatException;

    void zoomAll(boolean instantRepaint);

    WritableImage snapshot();

    List<ILayer> getLayers();

    ILayer getActiveLayer();

    ILayer createLayerOnTop();

    ILayer insertLayerAbove(ILayer refLayer);

    ILayer insertLayerUnder(ILayer refLayer);

    void deleteLayer(ILayer layer);

    void selectActiveLayer(ILayer layer);

    Viewport getViewport();

    void setCursor(Cursor cursor);

    SimpleObjectProperty<PlotPoint> focusedPointProperty();
}
