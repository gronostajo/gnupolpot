package net.avensome.dev.gnupolpot.core.plotter;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public interface EventHandler {
    void mouseMoved(MouseEvent event, boolean focusedPointChanged);

    void mousePressed(MouseEvent event);

    void mouseDragged(MouseEvent event);

    void mouseReleased(MouseEvent event);

    void scrolled(ScrollEvent event);
}
