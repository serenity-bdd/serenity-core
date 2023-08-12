package net.serenitybdd.junit5;

import com.google.common.base.Preconditions;
import net.serenitybdd.annotations.DriverOptions;
import net.serenitybdd.annotations.WithDriver;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Method;
import java.util.Optional;

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

    public Optional<String> getDisplayName(){
        DisplayName displayNameAnnotation = method.getAnnotation(DisplayName.class);
        if( displayNameAnnotation != null){
            return Optional.of(displayNameAnnotation.value());
        }
        return Optional.empty();
    }

    public String specifiedDriver() {
        Preconditions.checkArgument(isDriverSpecified());
        return (method.getAnnotation(WithDriver.class).value());
    }

    public String driverOptions() {
        Preconditions.checkArgument(isDriverSpecified());
        return (method.getAnnotation(DriverOptions.class).value());
    }

}
