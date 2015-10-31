package net.avensome.dev.gnupolpot.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;

public interface Feature {
    @NotNull
    String getMenuPath();

    @Nullable
    String call(IPlotter plotter);
}
