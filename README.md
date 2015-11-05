# gnupolpot

*"It's like gnuplot, but wrong."*


## Importing plots

Plot is a collection of points. Each point can have a color and can be assigned to any number of shapes (ie. segments or polygons). Each polygon can have a color.

Text file lines that create:

- A point at (10, -10) with default color:

        10 -10

- Corners of 20x20 square in the middle of the plot, bottom right one is red:

        -20 -20
        -20 20
        20 -20
        20 20 #ff0000

- These corners connected into a square, filled with default color:

        -20 -20 $myLovelySquare
        -20 20 $myLovelySquare
        20 20 #ff0000 $myLovelySquare
        20 -20 $myLovelySquare

- This square with blue and green diagonals:

        -20 -20 $myLovelySquare $diagonal1
        -20 20 $myLovelySquare $diagonal2
        20 20 #ff0000 $myLovelySquare $diagonal1
        20 -20 $myLovelySquare $diagonal2
        $diagonal1 #00ff00
        $diagonal2 #0000ff


## Using plugins

Just add plugin JAR to classpath. For example create a folder called `plugins`, place plugin JARs inside and launch gnupolpot like this:

    java -classpath "path/to/gnupolpot/jar;plugins/*" net.avensome.dev.gnupolpot.core.PolpotApp

You'll see a *"<N> plugin(s) loaded"* message if you have set the classpath correctly. Features provided by plugins will be available through dedicated button.


## Creating plugins

Step by step tutorial for IntelliJ IDEA.

1. Create an empty Java project.
2. Create a new directory called `lib` in your project's root. Place `api.jar` file in there.
3. Right-click `api.jar` in IntelliJ IDEA, select *Add as Library...* and click OK.
4. Create a class extending `Plugin` class from the library you've just added.
5. Right-click `src` folder, create new package called `META-INF.services`. IntelliJ will complain, ignore it.
6. Inside the package you have just created add a text file called `net.avensome.dev.gnupolpot.api.Plugin`. Open it and press `Ctrl`+`Space`, your class should pop up. Press `Enter`.
7. Implement `getName()` in your class and return some name. Create a class that implements `Feature`. Add it to the list returned by `YourPlugin.getFeatures()` method.
8. Open *File* > *Project Structure* > *Modules*. Click the `+` button on the far right. Add gnupolpot's main JAR. Click OK.
9. Wait until IntelliJ IDEA finishes indexing. Open *Edit configurations* window (combo box in the top right corner).
10. Add new configuration for *Application*. Enter `net.avensome.dev.gnupolpot.core.PolpotApp` as the main class.

To build a plugin JAR, add an artifact containing your classes. You don't have to include API or core files.
        

## Various stuff

Uses [Gnome icon theme](http://ftp.gnome.org/pub/GNOME/sources/gnome-icon-theme/3.12/) (LGPL v3).
