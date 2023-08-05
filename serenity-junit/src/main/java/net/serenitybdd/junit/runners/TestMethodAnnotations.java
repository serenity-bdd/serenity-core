package net.serenitybdd.junit.runners;

import com.google.common.base.Preconditions;
import net.serenitybdd.annotations.DriverOptions;
import net.serenitybdd.annotations.WithDriver;
import org.junit.runners.model.FrameworkMethod;

/**
 * Utility class used to read Serenity annotations for a particular JUnit test.
 * @author johnsmart
 *
 */
public final class TestMethodAnnotations {

    private final FrameworkMethod method;

    private TestMethodAnnotations(final FrameworkMethod method) {
        this.method = method;
    }

    public static TestMethodAnnotations forTest(final FrameworkMethod method) {
        return new TestMethodAnnotations(method);
    }


    public boolean isDriverSpecified() {
        return (method.getMethod().getAnnotation(WithDriver.class) != null);
    }

    public String specifiedDriver() {
        Preconditions.checkArgument(isDriverSpecified());
        return (method.getMethod().getAnnotation(WithDriver.class).value());
    }

    public String driverOptions() {
        Preconditions.checkArgument(isDriverSpecified());
        if (this.method.getMethod().getAnnotation(DriverOptions.class) != null) {
            return (method.getMethod().getAnnotation(DriverOptions.class).value());
        } else {
            return "";
        }
    }

}
