package net.avensome.dev.gnupolpot.api;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import net.avensome.dev.gnupolpot.api.action.Zooming;
import net.avensome.dev.gnupolpot.api.mouse.MouseEventType;

/**
 * A tool is a piece of app's functionality. Tools change behavior of the plot when user is interacting with it.
 */
public abstract class Tool {
    /**
     * @return Tool's name. Available in the <i>Plugin info</i> window and tooltips.
     */
    public abstract String getName();

    /**
     * @return An object representing image that will be painted on tool's button in the sidebar.
     */
    public Image getButtonImage() {
        return new Image("/net/avensome/dev/gnupolpot/api/ui/emblem-unreadable.png");
    }

    /**
     * Called when tool is activated. <b>Don't call this manually unless you know what you're doing.</b> Most of the
     * time you probably rather want to use {@link Api#selectTool(Tool)}.
     * @param api API interface
     */
    public void activated(Api api) {
    }

    /**
     * Called when tool is deactivated. <b>Don't call this manually unless you know what you're doing.</b> Most of the
     * time you probably rather want to use {@link Api#selectTool(Tool)}.
     * @param api API interface
     */
    public void deactivated(Api api) {
    }

    /**
     * Called when user interacts with the plot using a mouse pointer.
     * @param api API interface
     * @param eventType type of mouse event that triggered this method
     * @param event JavaFX mouse pointer event from plot's perspective
     * @param focusedPointChanged whether this event changed the focused point (de-focusing all points is a change too)
     */
    public abstract void receiveMouseEvent(Api api, MouseEventType eventType, MouseEvent event, boolean focusedPointChanged);

    /**
     * Called when user interacts with the plot using a mouse wheel.
     * @param api API interface
     * @param event JavaFX mouse wheel event
     */
    public void receiveScrollEvent(Api api, ScrollEvent event) {
        Zooming.changeZoom(api, event);
    }
}
