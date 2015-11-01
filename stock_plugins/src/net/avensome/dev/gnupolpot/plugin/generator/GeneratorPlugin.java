package net.avensome.dev.gnupolpot.plugin.generator;

import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.Plugin;

import java.util.Arrays;
import java.util.Collection;

public class GeneratorPlugin extends Plugin {
    @Override
    public String getName() {
        return "Generator";
    }

    @Override
    public String getDeveloper() {
        return "Krzysztof \"gronostaj\" Śmiałek";
    }

    @Override
    public String getDescription() {
        return "Generates points and whistles";
    }

    @Override
    public String getLicense() {
        return "GNU GPL v2";
    }

    @Override
    public Collection<Feature> getFeatures() {
        return Arrays.asList(new RectangleGeneratorFeature());
    }
}
