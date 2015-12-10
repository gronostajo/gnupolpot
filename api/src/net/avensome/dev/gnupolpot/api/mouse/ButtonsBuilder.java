package net.avensome.dev.gnupolpot.api.mouse;

/**
 * {@link Buttons} builder.
 * @see Buttons
 */
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

    /**
     * Add primary mouse button to the current pattern.
     * @return Builder object
     */
    public ButtonsBuilder withPrimary() {
        return new ButtonsBuilder(true, secondary, middle);
    }

    /**
     * Add secondary mouse button to the current pattern.
     * @return Builder object
     */
    public ButtonsBuilder withSecondary() {
        return new ButtonsBuilder(primary, true, middle);
    }

    /**
     * Add middle mouse button to the current pattern.
     * @return Builder object
     */
    public ButtonsBuilder withMiddle() {
        return new ButtonsBuilder(primary, secondary, true);
    }

    /**
     * Create a {@link Buttons} object from current pattern.
     * @return A {@link Buttons} object.
     */
    public Buttons build() {
        return new Buttons(primary, secondary, middle);
    }
}
