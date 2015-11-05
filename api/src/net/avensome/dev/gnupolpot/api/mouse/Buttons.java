package net.avensome.dev.gnupolpot.api.mouse;

import javafx.scene.input.MouseEvent;

public class Buttons {
    private final boolean primary;
    private final boolean secondary;
    private final boolean middle;

    public static final Buttons PRIMARY = new Buttons(true, false, false);
    public static final Buttons SECONDARY = new Buttons(false, true, false);
    public static final Buttons MIDDLE = new Buttons(false, false, true);

    Buttons(boolean primary, boolean secondary, boolean middle) {
        this.primary = primary;
        this.secondary = secondary;
        this.middle = middle;
    }

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
