package net.thucydides.model.matchers;

import org.hamcrest.Matcher;

public class SimpleValueMatcher {
    private final Object value;
    private final Matcher<? extends Object> matcher;

    protected SimpleValueMatcher(Object value, Matcher<? extends Object> matcher) {
        this.value = value;
        this.matcher = matcher;
    }

    public boolean matches() {
        return matcher.matches(value);
    }
}
