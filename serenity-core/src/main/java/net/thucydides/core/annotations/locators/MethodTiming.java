package net.thucydides.core.annotations.locators;

/**
 * Created by john on 17/03/15.
 */
public class MethodTiming {

    private final Thread thread;

    public MethodTiming(Thread thread) {
        this.thread = thread;
    }

    public static MethodTiming forThisThread() {
        return new MethodTiming(Thread.currentThread());
    }

    public boolean isInQuickMethod() {
        for (StackTraceElement elt : Thread.currentThread().getStackTrace()) {
            if (elt.getMethodName().contains("Currently") || elt.getMethodName().contains("toString")) {
                return true;
            }
        }
        return false;
    }

}
