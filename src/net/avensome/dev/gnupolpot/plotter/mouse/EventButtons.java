package net.avensome.dev.gnupolpot.plotter.mouse;

import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EventButtons {
    private final boolean primary;
    private final boolean secondary;
    private final boolean middle;

    EventButtons(boolean primary, boolean secondary, boolean middle) {
        this.primary = primary;
        this.secondary = secondary;
        this.middle = middle;
    }

    public static EventButtons fromMouseEvent(MouseEvent mouseEvent) {
        return new EventButtons(mouseEvent.isPrimaryButtonDown(), mouseEvent.isSecondaryButtonDown(), mouseEvent.isMiddleButtonDown());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(primary)
                .append(secondary)
                .append(middle)
                .build();
    }

    @Override
    public boolean equals(Object another) {
        if (!(another instanceof EventButtons)) {
            return false;
        } else if (another == this) {
            return true;
        }

        EventButtons that = (EventButtons) another;
        return new EqualsBuilder()
                .append(this.primary, that.primary)
                .append(this.secondary, that.secondary)
                .append(this.middle, that.middle)
                .build();
    }
}
