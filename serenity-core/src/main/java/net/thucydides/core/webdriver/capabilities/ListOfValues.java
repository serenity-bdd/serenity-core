package net.thucydides.core.webdriver.capabilities;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListOfValues {
    private final Map<String, Object> options;

    public ListOfValues(Map<String, Object> options) {
        this.options = options;
    }

    public static ListOfValues from(Map<String, Object> options) {
        return new ListOfValues(options);
    }

    @NotNull
    public List<String> forProperty(String propertyName) {
        if (options.get(propertyName) != null) {
            return ((List<?>) options.get(propertyName)).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
