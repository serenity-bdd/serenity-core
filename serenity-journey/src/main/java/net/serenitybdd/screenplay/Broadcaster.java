package net.serenitybdd.screenplay;

import com.google.common.eventbus.EventBus;

public class Broadcaster {
    static private EventBus eventBus = new EventBus();

    public static EventBus getEventBus() {
        return eventBus;
    }


}
