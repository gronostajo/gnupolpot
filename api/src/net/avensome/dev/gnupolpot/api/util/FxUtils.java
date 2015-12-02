package net.avensome.dev.gnupolpot.api.util;

import javafx.scene.paint.Color;

public final class FxUtils {
    private FxUtils() {
        throw new AssertionError("Utility class - do not instantiate!");
    }

    public static String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)
        );
    }

    public static Color applyOpacity(Color color, double opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity * color.getOpacity());
    }
}
