package net.avensome.dev.gnupolpot.api.mouse;

public class ButtonsBuilder {
    private boolean primary = false;
    private boolean secondary = false;
    private boolean middle = false;

    public ButtonsBuilder() {
    }

    private ButtonsBuilder(boolean primary, boolean secondary, boolean middle) {
        this.primary = primary;
        this.secondary = secondary;
        this.middle = middle;
    }

    public ButtonsBuilder withPrimary() {
        return new ButtonsBuilder(true, secondary, middle);
    }

    public ButtonsBuilder withSecondary() {
        return new ButtonsBuilder(primary, true, middle);
    }

    public ButtonsBuilder withMiddle() {
        return new ButtonsBuilder(primary, secondary, true);
    }

    public Buttons build() {
        return new Buttons(primary, secondary, middle);
    }
}
