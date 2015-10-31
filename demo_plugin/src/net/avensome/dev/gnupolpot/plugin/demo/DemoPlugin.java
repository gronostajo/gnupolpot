package net.avensome.dev.gnupolpot.plugin.demo;

import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.Plugin;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;

import java.util.Collection;
import java.util.LinkedList;

public class DemoPlugin extends Plugin {
    @Override
    public String getName() {
        return "Demo Plugin";
    }

    @Override
    public String getAuthor() {
        return "Krzysztof \"gronostaj\" Śmiałek";
    }

    @Override
    public String getDescription() {
        return "A simple plugin that demonstrates how gnupolpot plugins are made.";
    }

    @Override
    public Collection<Feature> getFeatures() {
        LinkedList<Feature> features = new LinkedList<>();
        features.add(new AllBlackFeature());
        return features;
    }

    private class AllBlackFeature implements Feature {

        @Override
        public String getMenuItem() {
            return "Paint it black";
        }

        @Override
        public String call(IPlotter plotter) {
            plotter.getPoints().stream().forEach(point -> point.setColor(Color.BLACK));
            plotter.getShapes().stream().forEach(shape -> shape.setColor(Color.BLACK));
            plotter.queueRepaint();
            return "Rolling Stones fan, huh?";
        }
    }
}
