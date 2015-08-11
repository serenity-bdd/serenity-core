package net.thucydides.core.reports.matchers;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;

public final class TestOutcomeMatchers {

    public static Matcher<TestOutcome> havingTagType(final String tagType) {

        return new BaseMatcher<TestOutcome>() {

            @Override
            public boolean matches(Object matchee) {
                TestOutcome testOutcome =  (TestOutcome) matchee;
                return exists(testOutcome.getTags(), having(on(TestTag.class).getType(), is(tagType)));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a test outcome with a tag of type ").appendValue(tagType);
            }
        };
    }

    public static Matcher<TestOutcome> havingTagName(final String tagName) {

        return new BaseMatcher<TestOutcome>() {

            @Override
            public boolean matches(Object matchee) {
                TestOutcome testOutcome =  (TestOutcome) matchee;
                return exists(testOutcome.getTags(), having(on(TestTag.class).getName(), equalToIgnoringCase(tagName)));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a test outcome with a tag ").appendValue(tagName);
            }
        };
    }

    public static Matcher<TestOutcome> havingTag(final TestTag expectedTag) {

        return new BaseMatcher<TestOutcome>() {

            @Override
            public boolean matches(Object matchee) {
                TestOutcome testOutcome =  (TestOutcome) matchee;
                return exists(testOutcome.getTags(), having(on(TestTag.class), is(expectedTag)));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a test outcome with a tag ").appendValue(expectedTag);
            }
        };
    }
    
    public static Matcher<TestOutcome> withResult(final TestResult expectedResult) {
        return having(on(TestOutcome.class).getResult(), is(expectedResult));
    }

}
