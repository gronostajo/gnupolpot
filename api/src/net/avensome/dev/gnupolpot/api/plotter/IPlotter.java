package net.avensome.dev.gnupolpot.api.plotter;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.image.WritableImage;

import java.util.List;

public interface IPlotter {

    /*
        Plot contents
     */
    boolean isPristine();

    void clear();

    void importPlot(PlotData data) throws DataFormatException;


    /*
        Layers
     */
    List<ILayer> getLayers();

    ILayer getActiveLayer();

    ILayer createLayerOnTop(String label);

    ILayer createLayerAbove(ILayer refLayer, String label);

    ILayer createLayerUnder(ILayer refLayer, String label);

    void deleteLayer(ILayer layer);

    void selectActiveLayer(ILayer layer);

    void moveLayer(ILayer layer, LayerMove move) throws LayerException;

    void duplicateLayer(ILayer layer);

    void mergeLayers(ILayer mergeInto, ILayer... toBeMerged);


    /*
        Viewport
     */
    Viewport getViewport();

    void zoomAll(boolean instantRepaint);

    void requestRepaint();

    WritableImage getSnapshot();


    /*
        Misc
     */
    void setCursor(Cursor cursor);

    SimpleObjectProperty<PlotPoint> focusedPointProperty();


    enum LayerMove {
        UP, DOWN, TOP, BOTTOM
    }
}
