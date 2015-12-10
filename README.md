# gnupolpot

*"It's like gnuplot, but wrong."*


## Importing plots

*TODO*


## Using plugins

Just add plugin JAR to classpath. For example create a folder called `plugins`, place plugin JARs inside and launch gnupolpot like this:

    java -classpath "path/to/gnupolpot/jar;plugins/*" net.avensome.dev.gnupolpot.core.PolpotApp

You'll see a *"<N> plugin(s) loaded"* message if you have set the classpath correctly. Features provided by plugins will be available through dedicated button.


## Building from source

The best way is to import project into IntelliJ IDEA as a Gradle project. IDEA and Gradle will take care of the rest.

To build classes and JARs, use Gradle's `build` task on root project. `gnupolpot.jar` is the all-in-one standalone app. `api.jar` can be used for plugin development.


## Creating plugins

1. Use `api.jar` as a library.
1. Extend `Plugin` class and implement `getFeatures()` and/or `getTools()`. Plugins are singletons, so feel free to use state.
1. Return objects implementing `Feature` interface or extending `Tool` class.
1. You can use `Panning`, `Moving` and `Zooming` classes to inherit behavior from built-in tools.
1. API provides some useful Comparators and utils too.
1. When you update points/shapes, remember to call `api.getPlotter().requestRepaint()`.
1. To test your plugin, add its classes to classpath along with `gnupolpot.jar`, then run `net.avensome.dev.gnupolpot.core.PolpotApp`.

Gradle-based plugin stub coming soon (maybe).
        

## Various stuff

gnupolpot uses parts of other open source projects:

  - [Gnome icon theme](http://ftp.gnome.org/pub/GNOME/sources/gnome-icon-theme/3.12/) (LGPL v3).
