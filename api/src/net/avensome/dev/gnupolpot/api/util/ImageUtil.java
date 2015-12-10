package net.avensome.dev.gnupolpot.api.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * An utility class for dealing with images.
 */
public final class ImageUtil {
    private ImageUtil() {
        throw new AssertionError("Utility class - do not instantiate!");
    }

    public static void saveToFile(WritableImage image, File file) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            error.setHeaderText("Saving snapshot failed");
            error.showAndWait();
        }
    }
}
