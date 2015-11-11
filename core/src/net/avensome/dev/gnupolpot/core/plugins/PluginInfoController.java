package net.avensome.dev.gnupolpot.core.plugins;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.Plugin;
import net.avensome.dev.gnupolpot.api.Tool;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class PluginInfoController implements Initializable {
    @FXML
    private ListView<Plugin> pluginList;

    @FXML
    private Label pluginNameLabel;

    @FXML
    private Label developerLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label licenseLabel;

    @FXML
    private ListView<Tool> toolsList;

    @FXML
    private TableView<Feature> featuresTable;

    @FXML
    private TableColumn<Feature, String> featureNameColumn;

    @FXML
    private TableColumn<Feature, String> featureDescriptionColumn;

    private void selectPlugin(Plugin plugin) {
        pluginNameLabel.setText(plugin.getName());
        developerLabel.setText(plugin.getDeveloper());
        descriptionLabel.setText(plugin.getDescription());
        licenseLabel.setText(plugin.getLicense());
        toolsList.setItems(FXCollections.observableArrayList(plugin.getTools()));
        featuresTable.setItems(FXCollections.observableArrayList(plugin.getFeatures()));
    }

    public void setPlugins(Collection<Plugin> pluginsCollection) {
        List<Plugin> plugins = new ArrayList<>(pluginsCollection);
        plugins.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));

        pluginList.setItems(FXCollections.observableArrayList(plugins));

        if (plugins.size() > 0) {
            pluginList.getSelectionModel().selectFirst();
            selectPlugin(plugins.get(0));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pluginList.setCellFactory(listView -> new PluginListCell());

        toolsList.setCellFactory(listView -> new ToolListCell());

        featureNameColumn.setCellValueFactory(feature ->
                new SimpleStringProperty(feature.getValue().getMenuItem()));
        featureDescriptionColumn.setCellValueFactory(feature ->
                new SimpleStringProperty(feature.getValue().getDescription()));

        pluginList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectPlugin(newValue);
        });
    }

    private class PluginListCell extends ListCell<Plugin> {
        @Override
        public void updateItem(Plugin item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getName());
            }
        }
    }

    private class ToolListCell extends ListCell<Tool> {
        @Override
        protected void updateItem(Tool item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getName());
            }
        }
    }
}
