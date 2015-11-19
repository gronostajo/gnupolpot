package net.avensome.dev.gnupolpot.core.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import net.avensome.dev.gnupolpot.api.Tool;
import net.avensome.dev.gnupolpot.core.plugins.PluginInterface;

public class ToolButton extends Button {
    private final Tool tool;

    public ToolButton(Tool tool, PluginInterface pluginInterface, Integer index) {
        super(index.toString(), new ImageView(tool.getButtonImage()));
        this.tool = tool;

        Tooltip tooltip = new Tooltip(tool.getName());
        setTooltip(tooltip);

        setOnAction(event -> pluginInterface.selectTool(tool));
    }

    public Tool getTool() {
        return tool;
    }
}
