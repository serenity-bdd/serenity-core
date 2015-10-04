package net.serenitybdd.core.photography;

import com.google.common.eventbus.EventBus;

public class DarkroomQueue {
    static private EventBus eventBus = new EventBus();

    public static EventBus getEventBus() {
        return eventBus;
    }

    public static void sendNegative(ScreenshotNegative screenshotNegative) {
        getEventBus().post(screenshotNegative);
    }
}
