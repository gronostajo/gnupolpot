package net.avensome.dev.gnupolpot.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public interface Feature {
    @NotNull
    String getMenuItem();

    @Nullable
    String getDescription();

    @Nullable
    void execute(Api api) throws PluginException;
}
