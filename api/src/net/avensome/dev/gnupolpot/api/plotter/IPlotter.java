package net.avensome.dev.gnupolpot.api.plotter;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.image.WritableImage;

import java.util.List;

/**
 * <p>This interface is used to manipulate the plot displayed in gnupolpot's main window. Points and shapes are managed by
 * {@link ILayer} instances. A bunch of methods provided by this interface can be used to browse layer stack, add new
 * layers and remove existing ones. IPlotter also lets you manipulate the viewport.
 *
 * <p>Plot contains a stack of independent layers. Points and shapes can belong to one or more layers. Each layer can
 * be removed independently. Points and shapes disappear from plot when they no longer belong to any layer.
 *
 * <p>Remember to call {@link #requestRepaint()} each time you make some changes to layers, like, for example, adding
 * new points.
 */
public interface IPlotter {

    /**
     * Returns whether plotter is in the pristine state, that is it only has one layer that is empty.
     * @return Whether plotter is pristine.
     */
    boolean isPristine();

    /**
     * Removes all existing layers and creates a single, empty layer.
     */
    void clear();


    /**
     * @return Read-only list of all layers, starting with the bottommost one.
     */
    List<ILayer> getLayers();

    /**
     * @return Currently active layer.
     */
    ILayer getActiveLayer();

    /**
     * Creates a new layer on top of layer stack.
     * @param label that should be assigned to the newly created layer
     * @return Created layer.
     */
    ILayer createLayerOnTop(String label);

    /**
     * Creates a new layer above any provided layer currently on the layer stack.
     * @param refLayer reference layer; new one will be created above it
     * @param label that should be assigned to the newly created layer
     * @return Created layer.
     */
    ILayer createLayerAbove(ILayer refLayer, String label);

    /**
     * Creates a new layer below any provided layer currently on the layer stack.
     * @param refLayer reference layer; new one will be created below it
     * @param label that should be assigned to the newly created layer
     * @return Created layer.
     */
    ILayer createLayerUnder(ILayer refLayer, String label);

    /**
     * Removes layer from the layer stack.
     * @param layer that should be deleted
     */
    void deleteLayer(ILayer layer);

    /**
     * Activates layer. Active layer is returned by {@link #getActiveLayer()} and is affected by Layer pane
     * buttons.
     * @param layer to be activated
     */
    void selectActiveLayer(ILayer layer);

    /**
     * Moves layer up or down the layer stack one layer at a time or to the top/bottom instantly.
     * @param layer a layer currently on the layer stack that you want to move
     * @param move how to move the layer
     * @throws LayerException When moving layer is impossible. For example if you try to move the topmost layer up.
     */
    void moveLayer(ILayer layer, LayerMove move) throws LayerException;

    /**
     * Duplicates provided layer and all points and shapes that it contains, then adds it over current active layer.
     * @param layer to be duplicated
     */
    void duplicateLayer(ILayer layer);

    /**
     * Merges one or more layers into a master layer. Master layer is the only one of provided ones that remains on the
     * stack after this method completes. Master layer contains all points and shapes that belonged to any of passed
     * layers. Other layers are removed from the layer stack.
     * @param mergeInto master layer that points and shapes will be appended to
     * @param toBeMerged layers to be merged into master layer
     */
    void mergeLayers(ILayer mergeInto, ILayer... toBeMerged);


    /**
     * @return Viewport object that can be used to manipulate plotter's viewport.
     */
    IViewport getViewport();

    /**
     * Modify viewport to make entire plot visible at once and as big as possible, then center plot.
     */
    void zoomAll();

    /**
     * <p>Queue plot repaint. This method must be called each time layer contents change. This includes repositioning
     * points, adding shapes, changing colors etc. If your plot doesn't update update unless you pan it, then you
     * forgot to call this method.
     *
     * <p>Calling this method multiple times in a row should be safe. It doesn't repaint plot immediately, it queues
     * a repaint in a near future.
     */
    void requestRepaint();

    /**
     * @return An image of currently visible plot part.
     */
    WritableImage getSnapshot();


    /**
     * Sets mouse cursor image when the pointer is over the plot area.
     * @param cursor that will be set
     */
    void setCursor(Cursor cursor);

    /**
     * Plot currently under the cursor.
     * @return Property object.
     */
    SimpleObjectProperty<PlotPoint> focusedPointProperty();


    /**
     * How layer should be moved.
     */
    enum LayerMove {
        UP, DOWN, TOP, BOTTOM
    }
}
