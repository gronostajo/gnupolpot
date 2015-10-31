package net.avensome.dev.gnupolpot.core;

import com.google.common.collect.ImmutableMap;
import javafx.util.Callback;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class PluginManager {
    private Map<String, Plugin> plugins = new HashMap<>();

    public PluginManager() {
        ServiceLoader<Plugin> serviceLoader = ServiceLoader.load(Plugin.class);

        for (Plugin plugin : serviceLoader) {
            plugins.put(plugin.getName(), plugin);
        }
    }

    public Map<String, Plugin> getPlugins() {
        return ImmutableMap.copyOf(plugins);
    }

    public void registerFeatures(Callback<Feature, Integer> registrarCallback) {
        plugins.values().stream()
                .flatMap(plugin -> plugin.getFeatures().stream())
                .forEach(registrarCallback::call);
    }
}
