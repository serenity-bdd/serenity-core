package net.thucydides.junit.hamcrest;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Method;

public class MethodNamedMatcher extends TypeSafeMatcher<Method> {
    
    private final Matcher<String> matcher;
    
    public MethodNamedMatcher(Matcher<String> matcher) {
        this.matcher = matcher;
    }

    public boolean matchesSafely(Method method) {
        return matcher.matches(method.getName());
    }

    public void describeTo(org.hamcrest.Description description) {
        description.appendText("a method with a name that ");
        matcher.describeTo(description);
    }
}
