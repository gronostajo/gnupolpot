package net.avensome.dev.gnupolpot.core.plugins;

import com.google.common.collect.ImmutableList;
import javafx.util.Callback;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.Plugin;

import java.io.IOException;
import java.util.*;

public class PluginManager {
    private List<Plugin> plugins = new LinkedList<>();

    public PluginManager() throws IOException {
        ServiceLoader<Plugin> serviceLoader = ServiceLoader.load(Plugin.class);

        for (Plugin plugin : serviceLoader) {
            plugins.add(plugin);
        }
    }

    public List<Plugin> getPlugins() {
        return ImmutableList.copyOf(plugins);
    }

    public void registerFeatures(Callback<Feature, Integer> registrarCallback) {
        plugins.stream()
                .flatMap(plugin -> plugin.getFeatures().stream())
                .forEach(registrarCallback::call);
    }

}
