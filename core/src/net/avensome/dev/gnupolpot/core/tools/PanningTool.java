package net.avensome.dev.gnupolpot.core.tools;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.Tool;
import net.avensome.dev.gnupolpot.api.action.Panning;
import net.avensome.dev.gnupolpot.api.mouse.MouseEventType;

public class PanningTool extends Tool {

    private static PanningTool instance = new PanningTool();

    private PanningTool() {
    }

    public static PanningTool getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return "Pan and zoom";
    }

    @Override
    public Image getButtonImage() {
        return new Image("/net/avensome/dev/gnupolpot/core/icons/tools/panning.png");
    }

    @Override
    public void activated(Api api) {
        api.getPlotter().setCursor(Cursor.DEFAULT);
    }

    @Override
    public void receiveMouseEvent(Api api, MouseEventType eventType, MouseEvent event, boolean focusedPointChanged) {
        switch (eventType) {
            case PRESSED:
                Panning.start(event);
                api.getPlotter().setCursor(Cursor.MOVE);
                break;
            case DRAGGED:
                Panning.update(api, event);
                break;
            case RELEASED:
                Panning.stop();
                api.getPlotter().setCursor(Cursor.DEFAULT);
                break;
        }
    }
}
