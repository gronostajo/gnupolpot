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
        

## Various stuff

Uses [Numix icon theme](https://github.com/numixproject/numix-icon-theme) (GPL v3).
