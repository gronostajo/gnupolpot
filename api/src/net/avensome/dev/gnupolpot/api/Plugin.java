package net.avensome.dev.gnupolpot.api;

import java.util.Collection;
import java.util.LinkedList;

public abstract class Plugin {
    public Plugin() {
    }

    public abstract String getName();

    public String getDeveloper() {
        return null;
    }

    public String getDescription() {
        return null;
    }

    public String getLicense() {
        return "GNU GPL v2";
    }

    public Collection<Feature> getFeatures() {
        return new LinkedList<>();
    }

    public Collection<Tool> getTools() {
        return new LinkedList<>();
    }
}
