package net.serenitybdd.junit.runners;

import net.thucydides.core.annotations.Manual;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.model.TestResult;
import org.junit.Ignore;
import org.junit.runners.model.FrameworkMethod;

public class TestMethodConfiguration {

    private final FrameworkMethod method;

    public TestMethodConfiguration(FrameworkMethod method) {
        this.method = method;
    }

    public static TestMethodConfiguration forMethod(FrameworkMethod method) {
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
