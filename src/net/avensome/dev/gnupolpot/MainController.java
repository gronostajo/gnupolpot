package net.avensome.dev.gnupolpot;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class MainController {
    @FXML
    private CheckBox gridCheckbox;

    @FXML
    private Plotter plotter;

    @FXML
    private void resetPlotClicked() {
        plotter.clear();
    }
}
