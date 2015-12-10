package net.avensome.dev.gnupolpot.api.mouse;

/**
 * Mouse event types.
 */
public enum MouseEventType {
    /**
     * Mouse pointer is being moved with all buttons released.
     */
    MOVED,

    /**
     * A button has just been pressed.
     */
    PRESSED,

    /**
     * Mouse pointer is being moved with at least one button pressed.
     */
    DRAGGED,

    /**
     * A button has just been released. More buttons may still be pressed.
     */
    RELEASED
}
