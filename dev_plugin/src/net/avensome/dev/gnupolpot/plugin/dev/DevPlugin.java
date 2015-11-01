package net.avensome.dev.gnupolpot.plugin.dev;

import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.Plugin;

import java.util.Collection;
import java.util.Collections;

public class DevPlugin extends Plugin {
    @Override
    public String getName() {
        return "Dev Plugin";
    }

    @Override
    public String getDeveloper() {
        return "Krzysztof \"gronostaj\" Śmiałek";
    }

    @Override
    public String getDescription() {
        return "A plugin for doing developer stuff.";
    }

    @Override
    public Collection<Feature> getFeatures() {
        return Collections.singletonList(new DevPlotFeature());
    }
}
