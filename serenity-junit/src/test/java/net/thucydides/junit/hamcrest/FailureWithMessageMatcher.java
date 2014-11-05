package net.thucydides.junit.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.runner.notification.Failure;

public class FailureWithMessageMatcher extends TypeSafeMatcher<Failure> {
    
    private final Matcher<String> matcher;
    
    public FailureWithMessageMatcher(Matcher<String> matcher) {
        this.matcher = matcher;
    }

    public boolean matchesSafely(Failure failure) {
        return matcher.matches(failure.getMessage());
    }

    public void describeTo(Description description) {
        description.appendText("a TestException with a message that ");
        matcher.describeTo(description);
    }

}
