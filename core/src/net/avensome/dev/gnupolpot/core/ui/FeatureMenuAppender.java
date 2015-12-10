package net.avensome.dev.gnupolpot.core.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import net.avensome.dev.gnupolpot.api.Feature;
import net.avensome.dev.gnupolpot.api.PluginException;
import net.avensome.dev.gnupolpot.core.plugins.PluginInterface;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FeatureMenuAppender {
    private final MenuButton featureButton;
    private final PluginInterface pluginInterface;

    public FeatureMenuAppender(MenuButton featureButton, PluginInterface pluginInterface) {
        this.featureButton = featureButton;
        this.pluginInterface = pluginInterface;
    }

    public void addFeature(Feature feature) throws PluginException {
        featureButton.setDisable(false);

        MenuItem newItem = createFeatureMenuItem(feature);
        List<String> itemPath = Arrays.asList(feature.getMenuItem().split("\\\\")).stream()
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());

        List<String> pathWithoutFinalItem = itemPath.subList(0, itemPath.size() - 1);
        ObservableList<MenuItem> menuItems = findParentMenu(featureButton.getItems(), pathWithoutFinalItem, 2);

        for (int i = 2; i < menuItems.size(); i++) {
            MenuItem menuItem = menuItems.get(i);
            if (menuItem instanceof Menu) { // it's a submenu
                continue;
            }
            if (newItem.getText().compareTo(menuItem.getText()) < 0) {
                menuItems.add(i, newItem);
                return;
            }
        }

        menuItems.add(newItem);
    }

    private ObservableList<MenuItem> findParentMenu(ObservableList<MenuItem> items, List<String> itemPath, int offset) {
        if (itemPath.size() == 0) {
            return items;
        }

        String thisLevel = itemPath.get(0);
        List<String> nextLevels = itemPath.subList(1, itemPath.size());

        for (MenuItem item : items) {
            if (!(item instanceof Menu)) {
                continue;
            }
            if (item.getText().equals(thisLevel)) {
                return findParentMenu(((Menu) item).getItems(), nextLevels, 0);
            }
        }

        Menu newMenu = new Menu(thisLevel);
        for (int i = offset; i < items.size(); i++) {
            MenuItem menuItem = items.get(i);
            if (newMenu.getText().compareTo(menuItem.getText()) < 0) {
                items.add(i, newMenu);
                return newMenu.getItems();
            }
        }
        items.add(newMenu);
        return newMenu.getItems();
    }

    private MenuItem createFeatureMenuItem(Feature feature) throws PluginException {
        String menuItem = feature.getMenuItem();
        String menuItemName = menuItem.substring(menuItem.lastIndexOf('\\') + 1);
        if (menuItemName.trim().isEmpty()) {
            throw new PluginException();
        }

        MenuItem item = new MenuItem(menuItemName);
        item.setOnAction(event -> {
            try {
                feature.execute(pluginInterface);
            } catch (Exception e) {
                PluginException.showAlert(e);
            }
        });
        return item;
    }
}
