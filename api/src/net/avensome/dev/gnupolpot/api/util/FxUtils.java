package net.avensome.dev.gnupolpot.api.util;

import javafx.scene.paint.Color;

/**
 * JavaFX utilities that don't belong anywhere.
 */
public final class FxUtils {
    private FxUtils() {
        throw new AssertionError("Utility class - do not instantiate!");
    }

    /**
     * @param color a JavaFX color
     * @return Provided color in web hex notation.
     */
    public static String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)
        );
    }

    /**
     * @param color a color to apply opacity to
     * @param opacity a value between 0 and 1
     * @return provided color with opacity value applied
     */
    public static Color applyOpacity(Color color, double opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity * color.getOpacity());
    }
}
