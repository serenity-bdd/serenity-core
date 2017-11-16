package net.thucydides.core.annotations.locators;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by john on 17/03/15.
 */
public class MethodTiming {

    private final Thread thread;

    List<Predicate<StackTraceElement>> QUICK_METHOD_RULES = Arrays.asList(
            elt -> elt.getMethodName().contains("Currently") || elt.getMethodName().contains("toString"),
            elt -> elt.getClassName().equals("org.openqa.selenium.support.ui.Select")
    );

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

        return Arrays.stream(Thread.currentThread().getStackTrace()).anyMatch(
                this::runQuickly
        );
    }

    private boolean runQuickly(StackTraceElement elt) {
        return QUICK_METHOD_RULES.stream().anyMatch(
                rule -> rule.test(elt)
        );
    }

}
