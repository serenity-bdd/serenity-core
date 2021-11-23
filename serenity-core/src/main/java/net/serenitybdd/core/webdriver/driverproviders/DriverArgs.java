package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static net.thucydides.core.webdriver.CapabilityValue.asObject;

public class DriverArgs {
    private final String property;

    public DriverArgs(String property) {
        this.property = property;
    }

    public static DriverArgs fromProperty(ThucydidesSystemProperty property) {
        return new DriverArgs(property.getPropertyName());
    }

    public static DriverArgs fromProperty(String property) {
        return new DriverArgs(property);
    }

    public List<String> configuredIn(EnvironmentVariables environmentVariables) {
        Optional<String> args = (EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(property));
        if (args.isPresent()) {
            return fromValue(args.get());
        }
        return new ArrayList<>();
    }

    public static List<String> fromValue(String value) {
        if (value.isEmpty()) {
            return new ArrayList<>();
        }
        Object argsValue = asObject(value);
        if (argsValue instanceof List) {
            return ((List<Object>) argsValue).stream().map(Object::toString).collect(Collectors.toList());
        } else if (argsValue instanceof String) {
            return stream(((String) argsValue).split(";")).map(String::trim).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
