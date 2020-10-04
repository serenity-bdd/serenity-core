package net.serenitybdd.junit5;

import com.google.common.base.Preconditions;
import net.thucydides.core.annotations.WithDriver;

import java.lang.reflect.Method;

public class JUnit5TestMethodAnnotations {

    private final Method method;

    private JUnit5TestMethodAnnotations(final Method method) {
        this.method = method;
    }

    public static JUnit5TestMethodAnnotations forTest(final Method method) {
        return new JUnit5TestMethodAnnotations(method);
    }

    public boolean isDriverSpecified() {
        return (method.getAnnotation(WithDriver.class) != null);
    }

    public String specifiedDriver() {
        Preconditions.checkArgument(isDriverSpecified() == true);
        return (method.getAnnotation(WithDriver.class).value());
    }

}
