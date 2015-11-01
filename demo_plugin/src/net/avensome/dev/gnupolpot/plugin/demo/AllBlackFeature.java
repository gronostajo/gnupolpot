package net.avensome.dev.gnupolpot.plugin.demo;

import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;

public class AllBlackFeature implements Feature {
    @Override
    public String getMenuItem() {
        return "Paint it black";
    }

    @Override
    public String getDescription() {
        return "\"I see a red door and I want it painted black\n" +
                "No colors anymore, I want them to turn black\n" +
                "I see the girls walk by dressed in their summer clothes\n" +
                "I have to turn my head until my darkness goes\"";
    }

    @Override
    public String execute(IPlotter plotter) {
        plotter.getPoints().stream().forEach(point -> point.setColor(Color.BLACK));
        plotter.getShapes().stream().forEach(shape -> shape.setColor(Color.BLACK));
        plotter.requestRepaint();
        return "Rolling Stones fan, huh?";
    }
}
