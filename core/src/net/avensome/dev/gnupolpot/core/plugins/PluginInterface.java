package net.avensome.dev.gnupolpot.core.plugins;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.PluginException;
import net.avensome.dev.gnupolpot.api.Tool;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;
import net.avensome.dev.gnupolpot.core.plotter.Plotter;
import net.avensome.dev.gnupolpot.core.tools.PanningTool;
import net.avensome.dev.gnupolpot.core.ui.ToolButton;

import java.util.Optional;

public class PluginInterface implements Api {
    private final Plotter plotter;
    private final Label statusLabel;
    private final SimpleObjectProperty<Tool> currentTool;
    private final Pane toolPane;

    private String lastPermanentStatus = null;

    public PluginInterface(Plotter plotter, Label statusLabel, SimpleObjectProperty<Tool> currentTool, Pane toolPane) {
        this.plotter = plotter;
        this.statusLabel = statusLabel;
        this.currentTool = currentTool;
        this.toolPane = toolPane;
    }

    @Override
    public IPlotter getPlotter() {
        return plotter;
    }

    @Override
    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    @Override
    public void setTemporaryStatus(String status) {
        if (lastPermanentStatus == null) {
            lastPermanentStatus = statusLabel.getText();
        }
        statusLabel.setText(status);
    }

    @Override
    public void cancelTemporaryStatus() {
        if (lastPermanentStatus != null) {
            statusLabel.setText(lastPermanentStatus);
        }
    }

    @Override
    public void executeFeature(Feature feature) throws PluginException {
        feature.execute(this);
    }

    @Override
    public void selectDefaultTool() {
        selectTool(PanningTool.getInstance());
    }

    @Override
    public void selectTool(Tool tool) {
        toolPane.getChildren().stream()
                .filter(node -> node instanceof ToolButton)
                .forEach(node -> {
                    ToolButton button = ((ToolButton) node);
                    button.setDefaultButton(button.getTool() == tool);
                });
        Optional.ofNullable(currentTool.get()).ifPresent(cTool -> cTool.deactivated(this));
        currentTool.set(tool);
        currentTool.get().activated(this);
    }
}
