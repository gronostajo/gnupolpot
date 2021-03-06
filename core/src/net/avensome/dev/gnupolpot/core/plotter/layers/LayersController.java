package net.avensome.dev.gnupolpot.core.plotter.layers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import net.avensome.dev.gnupolpot.api.plotter.IPlotter.LayerMove;
import net.avensome.dev.gnupolpot.api.plotter.LayerException;
import net.avensome.dev.gnupolpot.core.plotter.Plotter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO Renaming layers
public final class LayersController {
    @FXML
    private TitledPane outerPane;
    @FXML
    private ListView<Layer> layersList;

    @FXML
    private Button mergeButton;
    @FXML
    private Button toTopButton;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;
    @FXML
    private Button toBottomButton;

    private Plotter plotter;

    public void configure(Plotter plotter) {
        this.plotter = plotter;

        layersList.setCellFactory(lv -> new LayerListCell(plotter));
        updateContents();

        plotter.addActiveLayerListener((observable, oldValue, newValue) -> {
            if (layersList.getSelectionModel().getSelectedItem() != newValue) {
                layersList.getSelectionModel().select(newValue);
                updateButtonsDisabled();
            }
        });
        plotter.addLayersListener(change -> updateContents());

        layersList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (plotter.getLayersInternal().contains(newValue)
                            && newValue != plotter.getActiveLayer()) {
                        plotter.selectActiveLayer(newValue);
                    }
                    updateButtonsDisabled();
                });
    }

    private void updateContents() {
        List<Layer> reversedLayers = new ArrayList<>(plotter.getLayersInternal());
        Collections.reverse(reversedLayers);
        layersList.getItems().setAll(reversedLayers);
        layersList.getSelectionModel().select(plotter.getActiveLayer());
        outerPane.setText(String.format("Layers (%s)", reversedLayers.size()));
        updateButtonsDisabled();
    }

    private void updateButtonsDisabled() {
        List<Layer> layers = plotter.getLayersInternal();
        int activeIndex = layers.indexOf(plotter.getActiveLayer());
        boolean isBottomLayer = (activeIndex == 0);
        boolean isTopLayer = (activeIndex == layers.size() - 1);

        mergeButton.setDisable(isBottomLayer);
        toTopButton.setDisable(isTopLayer);
        upButton.setDisable(isTopLayer);
        downButton.setDisable(isBottomLayer);
        toBottomButton.setDisable(isBottomLayer);
    }

    @FXML
    private void addClicked() {
        Layer activeLayer = plotter.getActiveLayer();
        Layer createdLayer = plotter.createLayerAbove(activeLayer, "New layer");
        plotter.selectActiveLayer(createdLayer);
    }

    @FXML
    private void removeClicked() {
        Layer activeLayer = plotter.getActiveLayer();
        plotter.deleteLayer(activeLayer);
    }

    @FXML
    private void duplicateClicked() {
        plotter.duplicateLayer(plotter.getActiveLayer());
    }

    @FXML
    private void mergeClicked() {
        Layer activeLayer = plotter.getActiveLayer();
        int activeLayerIndex = plotter.getLayersInternal().indexOf(activeLayer);
        Layer lowerLayer = plotter.getLayersInternal().get(activeLayerIndex - 1);
        plotter.mergeLayers(activeLayer, lowerLayer);
    }

    @FXML
    private void toTopClicked() throws LayerException {
        plotter.moveLayer(plotter.getActiveLayer(), LayerMove.TOP);
    }

    @FXML
    private void upClicked() throws LayerException {
        plotter.moveLayer(plotter.getActiveLayer(), LayerMove.UP);
    }

    @FXML
    private void downClicked() throws LayerException {
        plotter.moveLayer(plotter.getActiveLayer(), LayerMove.DOWN);
    }

    @FXML
    private void toBottomClicked() throws LayerException {
        plotter.moveLayer(plotter.getActiveLayer(), LayerMove.BOTTOM);
    }

    private class LayerListCell extends ListCell<Layer> {
        private final Plotter plotter;

        public LayerListCell(Plotter plotter) {
            this.plotter = plotter;
        }

        @Override
        protected void updateItem(Layer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
            } else if (item != null) {
                int index = plotter.getLayersInternal().indexOf(item);
                setText(String.format("%d. %s", index + 1, item.getLabel()));
            }
        }
    }
}
