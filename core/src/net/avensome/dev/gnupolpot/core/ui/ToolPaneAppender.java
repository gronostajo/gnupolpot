package net.avensome.dev.gnupolpot.core.ui;

import javafx.scene.layout.Pane;
import net.avensome.dev.gnupolpot.api.Tool;
import net.avensome.dev.gnupolpot.core.plugins.PluginInterface;

public class ToolPaneAppender {
    private final PluginInterface pluginInterface;
    private final Pane pane;

    public ToolPaneAppender(PluginInterface pluginInterface, Pane pane) {
        this.pluginInterface = pluginInterface;
        this.pane = pane;
    }

    public void addTool(Tool tool) {
        int index = pane.getChildren().size() + 1;
        ToolButton button = new ToolButton(tool, pluginInterface, index);
        pane.getChildren().add(button);
    }
}
