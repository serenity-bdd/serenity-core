package net.thucydides.model.steps;

import net.serenitybdd.annotations.Pending;
import net.serenitybdd.annotations.TestAnnotations;

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
