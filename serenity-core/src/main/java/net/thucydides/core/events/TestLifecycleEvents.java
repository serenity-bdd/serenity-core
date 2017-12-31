package net.thucydides.core.events;

import com.google.common.eventbus.EventBus;

public class TestLifecycleEvents {

    private static final ThreadLocal<EventBus> threadlocalEventBus = ThreadLocal.withInitial(() -> new EventBus());

    public static void postEvent(Object event) {
        threadlocalEventBus.get().post(event);
    }

    public static void register(Object listener) {
        threadlocalEventBus.get().register(listener);
    }

    public static void unregister(Object listener) {
        threadlocalEventBus.get().unregister(listener);
    }

    public static TestSuiteStarted testSuiteStarted() {
        return new TestSuiteStarted();
    }
    public static TestStarted testStarted() { return new TestStarted();}
    public static TestFinished testFinished() {
        return new TestFinished();
    }
    public static TestSuiteFinished testSuiteFinished() { return new TestSuiteFinished();}

    public static class TestSuiteStarted {}
    public static class TestStarted {}
    public static class TestFinished {}
    public static class TestSuiteFinished {}


}
