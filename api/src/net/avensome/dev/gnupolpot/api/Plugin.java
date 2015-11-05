package net.avensome.dev.gnupolpot.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Collection;
import java.util.LinkedList;

public abstract class Plugin {
    public Plugin() {
    }

    @NotNull
    public abstract String getName();

    @Nullable
    public String getDeveloper() {
        return null;
    }

    @Nullable
    public String getDescription() {
        return null;
    }

    @NotNull
    public String getLicense() {
        return "GNU GPL v2";
    }

    @NotNull
    public Collection<Feature> getFeatures() {
        return new LinkedList<>();
    }

    @NotNull
    public Collection<Tool> getTools() {
        return new LinkedList<>();
    }
}
