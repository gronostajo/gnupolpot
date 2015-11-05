package net.avensome.dev.gnupolpot.api;

import com.sun.istack.internal.NotNull;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import net.avensome.dev.gnupolpot.api.mouse.MouseEventType;

public abstract class Tool {
    @NotNull
    public abstract String getName();

    @NotNull
    public Image getButtonImage() {
        return new Image("/net/avensome/dev/gnupolpot/api/ui/emblem-unreadable.png");
    }

    public void activated(Api api) {
    }

    public void deactivated(Api api) {
    }

    public abstract void receiveMouseEvent(Api api, MouseEventType eventType, MouseEvent event);

    public void receiveScrollEvent(Api api, ScrollEvent event) {
    }
}
