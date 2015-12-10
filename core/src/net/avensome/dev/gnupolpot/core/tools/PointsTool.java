package net.avensome.dev.gnupolpot.core.tools;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.Tool;
import net.avensome.dev.gnupolpot.api.action.Panning;
import net.avensome.dev.gnupolpot.api.mouse.Buttons;
import net.avensome.dev.gnupolpot.api.mouse.MouseEventType;
import net.avensome.dev.gnupolpot.api.mouse.Point;
import net.avensome.dev.gnupolpot.api.plotter.ILayer;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;

public class PointsTool extends Tool {

    private static PointsTool instance = new PointsTool();

    private PointsTool() {
    }

    public static PointsTool getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return "Create and delete points";
    }

    @Override
    public Image getButtonImage() {
        return new Image("/net/avensome/dev/gnupolpot/core/icons/tools/points.png");
    }

    @Override
    public void activated(Api api) {
        api.getPlotter().setCursor(Cursor.DEFAULT);
        api.setStatus("Left-click to create point, right-click to remove it.");
    }

    @Override
    public void deactivated(Api api) {
        super.deactivated(api);
        Panning.stop();
    }

    @Override
    public void receiveMouseEvent(Api api, MouseEventType eventType, MouseEvent event, boolean focusedPointChanged) {
        IPlotter plotter = api.getPlotter();
        switch (eventType) {
            case PRESSED:
                Buttons buttons = Buttons.fromMouseEvent(event);
                if (buttons.equals(Buttons.MIDDLE)) {
                    Panning.start(event);
                    plotter.setCursor(Cursor.MOVE);
                } else if (buttons.equals(Buttons.PRIMARY)) {
                    ILayer activeLayer = plotter.getActiveLayer();
                    Point coords = plotter.getViewport().fromScreenCoords(event.getX(), event.getY());
                    PlotPoint newPoint = new PlotPoint(coords.getX(), coords.getY());
                    activeLayer.getPoints().add(newPoint);
                    plotter.requestRepaint();
                } else if (buttons.equals(Buttons.SECONDARY)) {
                    ILayer layer = plotter.getActiveLayer();
                    PlotPoint focusedPoint = plotter.focusedPointProperty().get();
                    if (layer.getPoints().contains(focusedPoint)) {
                        layer.getPoints().remove(focusedPoint);
                        plotter.requestRepaint();
                    } else {
                        ILayer ownerLayer = plotter.getLayers().stream()
                                .filter(layer1 -> layer1.getPoints().contains(focusedPoint))
                                .reduce(null, (layer1, layer2) -> layer2);
                        int index = plotter.getLayers().indexOf(ownerLayer);
                        api.setStatus(String.format(
                                "Point not on active layer. Switch to layer %d. '%s'.",
                                index, ownerLayer.getLabel()));
                    }
                }
                break;
            case DRAGGED:
                Panning.update(api, event);
                break;
            case RELEASED:
                Panning.stop();
                plotter.setCursor(Cursor.DEFAULT);
                break;
        }
    }
}
