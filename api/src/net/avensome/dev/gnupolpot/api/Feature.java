package net.avensome.dev.gnupolpot.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;

public interface Feature {
    @NotNull
    String getMenuItem();

    @Nullable
    String getMenuItemDescription();

    @Nullable
    String execute(IPlotter plotter);
}
