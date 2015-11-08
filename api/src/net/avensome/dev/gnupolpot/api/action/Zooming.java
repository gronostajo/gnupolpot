package net.avensome.dev.gnupolpot.api.action;

import javafx.scene.input.ScrollEvent;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;

public class Zooming {
    public static void changeZoom(Api api, ScrollEvent event) {
        IPlotter plotter = api.getPlotter();

        if (event.getDeltaY() < 0) {
            plotter.getViewport().zoom(0.5);
        } else if (event.getDeltaY() > 0) {
            plotter.getViewport().zoom(2);
        } else {
            return;
        }

        plotter.requestRepaint();
    }
}
