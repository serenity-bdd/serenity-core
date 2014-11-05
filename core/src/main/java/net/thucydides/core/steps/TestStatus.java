package net.thucydides.core.steps;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.TestAnnotations;

import java.lang.reflect.Method;

/**
 * Determine the status of a method based on its annotations.
 * @author johnsmart
 *
 */
public final class TestStatus {
    
    private final Method method;
    
    private TestStatus(final Method method) {
        super();
        this.method = method;
    }

    public static TestStatus of(final Method method) {
        return new TestStatus(method);
    }

    public boolean isPending() {
        Pending pending = method.getAnnotation(Pending.class);
        return (pending != null);
    }

    public boolean isIgnored() {
        return TestAnnotations.isIgnored(method);
    }
}
