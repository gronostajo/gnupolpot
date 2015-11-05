package net.avensome.dev.gnupolpot.core.tools;

import javafx.scene.input.MouseEvent;
import net.avensome.dev.gnupolpot.api.Api;
import net.avensome.dev.gnupolpot.api.Tool;
import net.avensome.dev.gnupolpot.api.mouse.MouseEventType;

public class TestTool extends Tool {
    @Override
    public String getName() {
        return "Test tool";
    }

    @Override
    public void receiveMouseEvent(Api api, MouseEventType eventType, MouseEvent event) {
        // Ignore completely
    }
}
