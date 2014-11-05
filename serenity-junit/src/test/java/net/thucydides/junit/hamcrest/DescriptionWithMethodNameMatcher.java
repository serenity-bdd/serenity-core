package net.thucydides.junit.hamcrest;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.runner.Description;

public class DescriptionWithMethodNameMatcher extends TypeSafeMatcher<Description> {
    
    private final Matcher<String> matcher;
    
    public DescriptionWithMethodNameMatcher(Matcher<String> matcher) {
        this.matcher = matcher;
    }

    public boolean matchesSafely(Description description) {
        return matcher.matches(description.getMethodName());
    }

    public void describeTo(org.hamcrest.Description description) {
        description.appendText("a method name that ");
        matcher.describeTo(description);
    }

}
