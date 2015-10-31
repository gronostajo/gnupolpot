package net.avensome.dev.gnupolpot.plugin.demo;

import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.Plugin;

import java.util.Collection;
import java.util.LinkedList;

public class DemoPlugin extends Plugin {
    @Override
    public String getName() {
        return "Demo Plugin";
    }

    @Override
    public String getDeveloper() {
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
}
