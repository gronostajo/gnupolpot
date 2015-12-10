package net.avensome.dev.gnupolpot.api;

/**
 * This class represents a feature that can be executed on demand. Features are available in a special menu. Plugins
 * can register features that they want to make available to the user.
 *
 * @see Plugin#getFeatures()
 */
public interface Feature {
    /**
     * <p>Defines of this feature in the feature menu.
     *
     * <p>Backslashes delimit submenu levels. For example <i>Foo\Bar</i> will create a submenu <i>Foo</i> with an item
     * <i>Bar</i>. Registering another feature with menu item <i>Foo\Baz</i> will append <i>Baz</i> to that submenu.
     *
     * @return Submenu trail and name of the menu item, separated with backslashes.
     */
    String getMenuItem();

    /**
     * @return Description of this feature that is shown in the <i>Plugin info</i> window.
     */
    String getDescription();

    /**
     * Called when user wants to use a feature by clicking corresponding menu item.
     * @param api API interface used to interact with core app
     * @throws PluginException when something goes wrong and plugin wants to show an error dialog.
     */
    void execute(Api api) throws PluginException;
}
