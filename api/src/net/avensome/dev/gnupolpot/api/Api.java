package net.avensome.dev.gnupolpot.api;

import net.avensome.dev.gnupolpot.api.plotter.IPlotter;

// TODO Javadocs
public interface Api {
    IPlotter getPlotter();

    void setStatus(String status);
    void setTemporaryStatus(String status);
    void cancelTemporaryStatus();

    void executeFeature(Feature feature) throws PluginException;

    void selectDefaultTool();

    void selectTool(Tool tool);
}
