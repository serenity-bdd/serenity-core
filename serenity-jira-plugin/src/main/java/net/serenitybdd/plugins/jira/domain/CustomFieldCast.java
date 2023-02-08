package net.serenitybdd.plugins.jira.domain;

import java.util.List;


public class CustomFieldCast {
    private final Object customFieldValue;

    public CustomFieldCast(Object customFieldValue) {
        this.customFieldValue = customFieldValue;
    }

    public String asString() {
        return (String) customFieldValue;
    }

    public <T> List<T> asListOf(T type) {
        return (List<T>) customFieldValue;
    }

    public Object value() {
        return customFieldValue;
    }
}
