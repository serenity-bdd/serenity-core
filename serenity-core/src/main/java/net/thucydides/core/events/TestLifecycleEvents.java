package net.thucydides.core.events;

import com.google.common.eventbus.EventBus;

public class TestLifecycleEvents {

    private static final EventBus eventBus = new EventBus();

    public static void postEvent(Object event) {
        eventBus.post(event);
    }

    public static  void register(Object listener) {
        eventBus.register(listener);
    }

    public static  void unregister(Object listener) {
        eventBus.unregister(listener);
    }

    public static Object testSuiteStarted() {
        return new TestSuiteStarted();
    }
    public static Object testSuiteFinished() {
        return new TestSuiteFinished();
    }

    public static class TestSuiteStarted {}
    public static class TestSuiteFinished {}


}
