package net.avensome.dev.gnupolpot;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Plotter extends Pane {
    private Canvas canvas = new Canvas();

    public Plotter() {
        super();

        getChildren().add(canvas);

        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> repaint();
        widthProperty().addListener(resizeListener);
        heightProperty().addListener(resizeListener);

        repaint();
    }

    private void repaint() {
        System.out.println(String.format("Resize %f, %f", getWidth(), getHeight()));
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        ctx.clearRect(0, 0, getWidth(), getHeight());
        ctx.setStroke(Color.RED);
        ctx.strokeLine(0, 0, getWidth(), getHeight());
        ctx.strokeLine(0, getHeight(), getWidth(), 0);
    }
}
