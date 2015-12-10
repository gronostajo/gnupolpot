package net.avensome.dev.gnupolpot.api;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Main clsas of a gnupolpot plugin. Contains authorship details and carries information about provided features
 * and tools.
 */
public abstract class Plugin {
    public Plugin() {
    }

    /**
     * @return Plugin name displayed in <i>Plugin info</i> window.
     */
    public abstract String getName();

    /**
     * @return Developer name, possibly also contact info.
     */
    public String getDeveloper() {
        return null;
    }

    /**
     * @return Plugin description.
     */
    public String getDescription() {
        return null;
    }

    /**
     * @return Plugin license info.
     */
    public String getLicense() {
        return "GNU GPL v2";
    }

    /**
     * @return Collection of features provided by this plugin. (Unordered, will be ordered alphabetically.)
     */
    public Collection<Feature> getFeatures() {
        return Collections.emptyList();
    }

    /**
     * @return Collection of tools provided by this plugin. Ordering will be kept in the sidebar.
     */
    public List<Tool> getTools() {
        return Collections.emptyList();
    }
}
