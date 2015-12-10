package net.avensome.dev.gnupolpot.api.mouse;

import javafx.scene.input.MouseEvent;

/**
 * Helper class for pattern-matching mouse events. Use {@link ButtonsBuilder} to create custom patterns or use static
 * constants in this class.
 */
public class Buttons {
    private final boolean primary;
    private final boolean secondary;
    private final boolean middle;

    /**
     * Pattern constant: primary button only. (Left button is primary on right-handed mouse.)
     */
    public static final Buttons PRIMARY = new Buttons(true, false, false);

    /**
     * Pattern constant: secondary button only. (Right button is secondary on right-handed mouse.)
     */
    public static final Buttons SECONDARY = new Buttons(false, true, false);

    /**
     * Pattern constant: middle button only.
     */
    public static final Buttons MIDDLE = new Buttons(false, false, true);

    Buttons(boolean primary, boolean secondary, boolean middle) {
        this.primary = primary;
        this.secondary = secondary;
        this.middle = middle;
    }

    /**
     * Creates an object of this class from a mouse event.
     * @param mouseEvent to base object on
     * @return {@link Buttons} object based on provided event.
     */
    public static Buttons fromMouseEvent(MouseEvent mouseEvent) {
        return new Buttons(mouseEvent.isPrimaryButtonDown(), mouseEvent.isSecondaryButtonDown(), mouseEvent.isMiddleButtonDown());
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Buttons buttons = (Buttons) o;

        if (primary != buttons.primary) return false;
        if (secondary != buttons.secondary) return false;
        return middle == buttons.middle;

    }

    @Override
    public int hashCode() {
        int result = (primary ? 1 : 0);
        result = 31 * result + (secondary ? 1 : 0);
        result = 31 * result + (middle ? 1 : 0);
        return result;
    }
}
