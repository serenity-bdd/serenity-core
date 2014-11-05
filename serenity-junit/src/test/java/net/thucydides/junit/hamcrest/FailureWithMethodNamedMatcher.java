package net.thucydides.junit.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.runner.notification.Failure;

public class FailureWithMethodNamedMatcher extends TypeSafeMatcher<Failure> {
    
    private final Matcher<String> matcher;
    
    public FailureWithMethodNamedMatcher(Matcher<String> matcher) {
        this.matcher = matcher;
    }

    public boolean matchesSafely(Failure failure) {
        return matcher.matches(failure.getDescription().getMethodName());
    }

    public void describeTo(Description description) {
        description.appendText("a TestException with a method name that ");
        matcher.describeTo(description);
    }

}
