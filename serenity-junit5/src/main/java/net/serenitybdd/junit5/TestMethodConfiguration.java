package net.serenitybdd.junit5;

import net.thucydides.core.annotations.Manual;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.model.TestResult;
import org.junit.Ignore;
import org.junit.runners.model.FrameworkMethod;

import java.lang.reflect.Method;

public class TestMethodConfiguration {

    private final Method method;

    public TestMethodConfiguration(Method method) {
        this.method = method;
    }

    public static TestMethodConfiguration forMethod(Method method) {
        return new TestMethodConfiguration(method);
    }

    public boolean isManual() {
        return method.getAnnotation(Manual.class) != null;
    }

    public TestResult getManualResult() {
        return method.getAnnotation(Manual.class).result();
    }

    public boolean isIgnored() {
        return method.getAnnotation(Ignore.class) != null;
    }

    public boolean isPending() {
        return method.getAnnotation(Pending.class) != null;
    }


    public String getManualResultReason() {
        return method.getAnnotation(Manual.class).reason();
    }
}
