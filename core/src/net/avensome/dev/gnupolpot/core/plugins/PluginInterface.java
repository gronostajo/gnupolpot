package net.avensome.dev.gnupolpot.core.plugins;

import javafx.scene.control.Label;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.PluginException;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter;
import net.avensome.dev.gnupolpot.core.plotter.Plotter;

public class PluginInterface implements Api {
    private final Plotter plotter;
    private final Label statusLabel;

    private String lastPermanentStatus = null;

    public PluginInterface(Plotter plotter, Label statusLabel) {
        this.plotter = plotter;
        this.statusLabel = statusLabel;
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
}
