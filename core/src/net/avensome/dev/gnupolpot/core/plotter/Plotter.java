package net.avensome.dev.gnupolpot.core.plotter;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.plotter.*;
import net.avensome.dev.gnupolpot.api.plotter.LayerException;
import net.avensome.dev.gnupolpot.core.plotter.layers.Layer;
import net.avensome.dev.gnupolpot.core.plotter.layers.LayerBinder;
import net.avensome.dev.gnupolpot.core.plotter.layers.LayersOps;
import net.avensome.dev.gnupolpot.core.plotter.painters.*;

import java.util.*;
import java.util.stream.Collectors;

// TODO Reorder methods to match interface order
public class Plotter extends Pane implements IPlotter {

    public static final int POINT_FOCUS_RADIUS = 3;

    public static final String MAIN_LAYER_LABEL = "Main layer";
    public static final String LAYER_EXCEPTION_UNKNOWN_IMPLEMENTATION = "Layers must be created inside plotter";
    public static final String LAYER_EXCEPTION_NOT_ON_STACK = "Layer not on stack";

    private final Canvas canvas = new Canvas();
    private boolean requiresRepaint;

    private final ObservableSet<PlotPoint> pointsView = FXCollections.observableSet();
    private final ObservableSet<Shape> shapesView = FXCollections.observableSet();

    private final ObservableList<Layer> layers = FXCollections.observableArrayList();
    private final SimpleObjectProperty<Layer> activeLayer = new SimpleObjectProperty<>();

    private final ObservableSet<Guide> guides = FXCollections.observableSet();

    private final Viewport viewport;

    private final List<Painter> painters = new LinkedList<>();

    private final SimpleObjectProperty<PlotPoint> focusedPoint = new SimpleObjectProperty<>();

    public Plotter() {
        super();

        getChildren().add(canvas);

        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        viewport = new Viewport(0, 0, getWidth(), getHeight(), 1);

        selectActiveLayer(createLayerOnTop(MAIN_LAYER_LABEL));
        registerLayerValidator();

        registerResizeHandlers();
        createPaintingPipeline();
        addZeroGuides();

        requestRepaint();
    }

    private void registerLayerValidator() {
        ListChangeListener<Layer> layerListChangeListener = change -> {
            change.next();
            for (Layer removedLayer : change.getRemoved()) {
                removedLayer.setInvalid(true);
            }
            for (Layer addedLayer : change.getAddedSubList()) {
                addedLayer.setInvalid(false);
            }
            rebuildView();
        };
        layers.addListener(layerListChangeListener);
    }

    private void rebuildView() {
        pointsView.clear();
        shapesView.clear();

        pointsView.addAll(layers.stream()
                .filter(layer -> !layer.isInvalid())
                .flatMap(layer -> layer.getPoints().stream())
                .collect(Collectors.toSet()));
        shapesView.addAll(layers.stream()
                .filter(layer -> !layer.isInvalid())
                .flatMap(layer -> layer.getShapes().stream())
                .collect(Collectors.toSet()));

        requestRepaint();
    }

    private void registerResizeHandlers() {
        ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
            viewport.resize(getWidth(), getHeight());
            requestRepaint();
        };
        widthProperty().addListener(resizeListener);
        heightProperty().addListener(resizeListener);
    }

    private void createPaintingPipeline() {
        GraphicsContext ctx = canvas.getGraphicsContext2D();

        BackgroundPainter backgroundPainter = new BackgroundPainter(ctx, Color.WHITE);
        painters.add(backgroundPainter);

        ShapePainter shapePainter = new ShapePainter(ctx, shapesView);
        painters.add(shapePainter);

        GuidePainter guidePainter = new GuidePainter(ctx, guides);
        painters.add(guidePainter);

        PointPainter pointPainter = new PointPainter(ctx, pointsView, focusedPoint);
        painters.add(pointPainter);
    }

    private void addZeroGuides() {
        guides.addAll(Arrays.asList(
                new Guide(Guide.Orientation.HORIZONTAL, 0, Color.LIGHTGRAY),
                new Guide(Guide.Orientation.VERTICAL, 0, Color.LIGHTGRAY)
        ));
    }

    public PlotData getViewDump() {
        return new PlotData(pointsView, shapesView);
    }

    @Override
    public void requestRepaint() {
        synchronized (canvas) {
            requiresRepaint = true;
        }
        Platform.runLater(() -> {
            synchronized (canvas) {
                if (requiresRepaint) {
                    requiresRepaint = false;
                    repaint();
                }
            }
        });
    }

    private void repaint() {
        for (Painter painter : painters) {
            painter.paint(viewport);
        }
    }

    @Override
    public boolean isPristine() {
        return layers.isEmpty() || layers.size() == 1 && pointsView.isEmpty();
    }

    @Override
    public void clear() {
        layers.clear();
        Layer newLayer = createLayerOnTop(MAIN_LAYER_LABEL);
        selectActiveLayer(newLayer);
        requestRepaint();
    }

    @Override
    public void importPlot(PlotData data) {
        getActiveLayer().getPoints().addAll(data.getPoints());
        getActiveLayer().getShapes().addAll(data.getShapes());
        requestRepaint();
    }

    @Override
    public void zoomAll(boolean instantRepaint) {
        if (pointsView.size() == 0) {
            viewport.centerAt(0, 0);
            if (instantRepaint) {
                repaint();
            } else {
                requestRepaint();
            }
            return;
        } else if (pointsView.size() == 1) {
            PlotPoint point = pointsView.iterator().next();
            viewport.centerAt(point.getX(), point.getY());
            if (instantRepaint) {
                repaint();
            } else {
                requestRepaint();
            }
            return;
        }

        double minX = pointsView.stream().map(PlotPoint::getX).reduce(Math::min).get();
        double maxX = pointsView.stream().map(PlotPoint::getX).reduce(Math::max).get();
        double minY = pointsView.stream().map(PlotPoint::getY).reduce(Math::min).get();
        double maxY = pointsView.stream().map(PlotPoint::getY).reduce(Math::max).get();

        double spreadX = (maxX - minX);
        double spreadY = (maxY - minY);

        double viewportSpread = Math.max(spreadX / viewport.getWidth(), spreadY / viewport.getHeight());
        double scale = Math.log(viewportSpread) / Math.log(2);  // log_e(x) / log_e(2) == log_2(x)

        double centerX = (minX + maxX) / 2;
        double centerY = (minY + maxY) / 2;

        viewport.setScalePower((int) -Math.ceil(scale));
        viewport.centerAt(centerX, centerY);
        if (instantRepaint) {
            repaint();
        } else {
            requestRepaint();
        }
    }

    @Override
    public WritableImage getSnapshot() {
        return canvas.snapshot(null, null);
    }

    public ObservableSet<PlotPoint> getPointsView() {
        return pointsView;
    }

    public ObservableSet<Shape> getShapesView() {
        return shapesView;
    }

    public void addLayersListener(ListChangeListener<Layer> listener) {
        layers.addListener(listener);
    }

    @Override
    public List<ILayer> getLayers() {
        return Collections.<ILayer>unmodifiableList(layers);
    }

    public List<Layer> getLayersInternal() {
        return Collections.unmodifiableList(layers);
    }

    @Override
    public Layer getActiveLayer() {
        return activeLayer.get();
    }

    public void addActiveLayerListener(ChangeListener<Layer> listener) {
        activeLayer.addListener(listener);
    }

    @Override
    public Layer createLayerOnTop(String label) {
        Layer newLayer = LayerBinder.createAndBindLayer(label, pointsView, shapesView, layers);
        layers.add(newLayer);
        return newLayer;
    }

    @Override
    public Layer createLayerAbove(ILayer refLayer, String label) {
        Layer newLayer = LayerBinder.createAndBindLayer(label, pointsView, shapesView, layers);
        //noinspection SuspiciousMethodCalls
        int refIndex = layers.indexOf(refLayer);
        if (refIndex == -1) {
            throw new NoSuchElementException(LAYER_EXCEPTION_NOT_ON_STACK);
        }
        layers.add(refIndex + 1, newLayer);
        return newLayer;
    }

    @Override
    public Layer createLayerUnder(ILayer refLayer, String label) {
        Layer newLayer = LayerBinder.createAndBindLayer(label, pointsView, shapesView, layers);
        //noinspection SuspiciousMethodCalls
        int refIndex = layers.indexOf(refLayer);
        if (refIndex == -1) {
            throw new NoSuchElementException(LAYER_EXCEPTION_NOT_ON_STACK);
        }
        layers.add(refIndex, newLayer);
        return newLayer;
    }

    @Override
    public void deleteLayer(ILayer layer) {
        //noinspection SuspiciousMethodCalls
        int deletedIndex = layers.indexOf(layer);
        if (deletedIndex == -1) {
            throw new NoSuchElementException(LAYER_EXCEPTION_NOT_ON_STACK);
        }
        Layer layerImpl = (Layer) layer;

        layers.remove(layerImpl);

        if (layers.isEmpty()) {
            Layer newLayer = createLayerOnTop(MAIN_LAYER_LABEL);
            selectActiveLayer(newLayer);
        } else if (activeLayer.get() == layerImpl) {
            Layer newActiveLayer = layers.get(Math.max(0, deletedIndex - 1));
            selectActiveLayer(newActiveLayer);
        }
    }

    public void readdLayerAtIndex(Layer layer, int index) {
        if (!layer.isInvalid()) {
            throw new RuntimeException("Can't add a layer that wasn't deleted");
        }
        layers.add(index, layer);
    }

    @Override
    public void selectActiveLayer(ILayer layer) {
        //noinspection SuspiciousMethodCalls
        if (layers.indexOf(layer) == -1) {
            throw new NoSuchElementException(LAYER_EXCEPTION_NOT_ON_STACK);
        } else if (!(layer instanceof Layer)) {
            throw new IllegalArgumentException(LAYER_EXCEPTION_UNKNOWN_IMPLEMENTATION);
        }
        activeLayer.set((Layer) layer);
    }

    @Override
    public void moveLayer(ILayer layer, LayerMove move) throws LayerException {
        if (!(layer instanceof Layer)) {
            throw new IllegalArgumentException(LAYER_EXCEPTION_UNKNOWN_IMPLEMENTATION);
        }
        Layer layerImpl = (Layer) layer;
        if (layerImpl.isInvalid()) {
            throw new IllegalArgumentException("Layer was deleted");
        }

        switch (move) {
            case UP:
                LayersOps.moveUp(layers, layerImpl);
                break;
            case DOWN:
                LayersOps.moveDown(layers, layerImpl);
                break;
            case TOP:
                LayersOps.moveToTop(layers, layerImpl);
                break;
            case BOTTOM:
                LayersOps.moveToBottom(layers, layerImpl);
                break;
        }
    }

    @Override
    public void duplicateLayer(ILayer layer) {
        LayersOps.duplicate(layers, (Layer) layer);
    }

    @Override
    public void mergeLayers(ILayer mergeInto, ILayer... toBeMerged) {
        List<ILayer> iLayers = new ArrayList<>(toBeMerged.length + 1);
        iLayers.add(mergeInto);
        iLayers.addAll(Arrays.asList(toBeMerged));

        Layer[] layers = new Layer[iLayers.size()];
        for (int i = 0; i < iLayers.size(); i++) {
            ILayer iLayer = iLayers.get(i);
            if (!(iLayer instanceof Layer)) {
                throw new IllegalArgumentException(LAYER_EXCEPTION_UNKNOWN_IMPLEMENTATION);
            }
            layers[i] = (Layer) iLayer;
        }
        LayersOps.merge(this.layers, layers);
    }

    @Override
    public Viewport getViewport() {
        return viewport;
    }

    public SimpleObjectProperty<PlotPoint> focusedPointProperty() {
        return focusedPoint;
    }

    public void registerEventHandler(EventHandler eventHandler) {
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, (event) -> {
            boolean focusChanged = updateFocusedPoint(event);
            eventHandler.mouseMoved(event, focusChanged);
            if (focusChanged) {
                requestRepaint();
            }
        });
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, eventHandler::mousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, eventHandler::mouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, eventHandler::mouseReleased);
        canvas.addEventHandler(ScrollEvent.SCROLL, eventHandler::scrolled);
    }

    private boolean updateFocusedPoint(MouseEvent event) {
        Point plotCoords = viewport.fromScreenCoords(event.getX(), event.getY());
        double x = plotCoords.getX();
        double y = plotCoords.getY();

        PlotPoint currentFocusedPoint = viewport.pointAtPlotCoords(x, y, POINT_FOCUS_RADIUS, pointsView);

        boolean focusChanged = (focusedPoint.get() != currentFocusedPoint);
        focusedPoint.set(currentFocusedPoint);

        return focusChanged;
    }
}
