package net.serenitybdd.junit5;

import net.serenitybdd.annotations.Manual;
import net.serenitybdd.annotations.Pending;
import net.thucydides.model.domain.TestResult;

import java.lang.reflect.Method;
import java.util.Arrays;

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
        return Arrays.stream(method.getAnnotations()).anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("Ignore"));
    }

    public boolean isPending() {
        return method.getAnnotation(Pending.class) != null;
    }


    public String getManualResultReason() {
        return method.getAnnotation(Manual.class).reason();
    }
}
