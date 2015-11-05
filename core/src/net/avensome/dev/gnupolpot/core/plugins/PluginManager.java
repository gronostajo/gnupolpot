package net.avensome.dev.gnupolpot.core.plugins;

import com.google.common.collect.ImmutableList;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.Plugin;
import net.avensome.dev.gnupolpot.api.Tool;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

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

    public List<Feature> getFeatures() {
        return plugins.stream()
                .flatMap(plugin -> plugin.getFeatures().stream())
                .collect(Collectors.toList());
    }

    public List<Tool> getTools() {
        return plugins.stream()
                .flatMap(plugin -> plugin.getTools().stream())
                .collect(Collectors.toList());
    }
}
