package net.avensome.dev.gnupolpot.api;

import net.avensome.dev.gnupolpot.api.plotter.IPlotter;

/**
 * Interface used by plugins to interact with core application.
 */
public interface Api {
    /**
     * @return Plotter that can be used to manipulate displayed points, shapes, layers, viewport etc.
     */
    IPlotter getPlotter();

    /**
     * Changes text visible in application's statusbar.
     * @param status text that will appear in the statusbar
     */
    void setStatus(String status);

    /**
     * Changes text visible in application's statusbar temporarily. Can be cancelled with {@link #cancelTemporaryStatus()} method.
     * @param status text that will appear in the statusbar
     */
    void setTemporaryStatus(String status);

    /**
     * Restores statusbar text to last value passed to {@link #setStatus(String)}.
     */
    void cancelTemporaryStatus();

    /**
     * Switches to default built-in tool.
     */
    void selectDefaultTool();

    /**
     * Switches to provided tool.
     * @param tool to activate
     */
    void selectTool(Tool tool);
}
