package net.avensome.dev.gnupolpot.core.plotter.mouse;

public class EventButtonsBuilder {
    private boolean primary = false;
    private boolean secondary = false;
    private boolean middle = false;

    public EventButtonsBuilder() {
    }

    private EventButtonsBuilder(boolean primary, boolean secondary, boolean middle) {
        this.primary = primary;
        this.secondary = secondary;
        this.middle = middle;
    }

    public EventButtonsBuilder withPrimary() {
        return new EventButtonsBuilder(true, secondary, middle);
    }

    public EventButtonsBuilder withSecondary() {
        return new EventButtonsBuilder(primary, true, middle);
    }

    public EventButtonsBuilder withMiddle() {
        return new EventButtonsBuilder(primary, secondary, true);
    }

    public EventButtons build() {
        return new EventButtons(primary, secondary, middle);
    }
}
