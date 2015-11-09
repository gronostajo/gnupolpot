package net.avensome.dev.gnupolpot.api;

public interface Feature {
    String getMenuItem();

    String getDescription();

    void execute(Api api) throws PluginException;
}
