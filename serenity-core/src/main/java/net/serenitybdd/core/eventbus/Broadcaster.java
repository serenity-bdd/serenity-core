package net.serenitybdd.core.eventbus;

import com.google.common.eventbus.EventBus;

public class Broadcaster {

    static private ThreadLocal<EventBus> eventBusThreadLocal = new ThreadLocal<>();

    public static EventBus getEventBus() {
        if (eventBusThreadLocal.get() == null) {
            eventBusThreadLocal.set(new EventBus());
        }
        return eventBusThreadLocal.get();
    }

    public static void unregisterAllListeners() {
        eventBusThreadLocal.remove();
    }

}
