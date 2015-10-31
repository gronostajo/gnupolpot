package net.avensome.dev.gnupolpot.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Collection;

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
    public abstract Collection<Feature> getFeatures();
}
