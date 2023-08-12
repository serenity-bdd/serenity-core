package net.thucydides.model.reports.matchers;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestTag;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public final class TestOutcomeMatchers {

    public static Matcher<TestOutcome> havingTagType(final String tagType) {

        return new BaseMatcher<TestOutcome>() {

            @Override
            public boolean matches(Object matchee) {
                TestOutcome testOutcome =  (TestOutcome) matchee;

                return testOutcome.getTags().stream()
                                  .anyMatch(testTag -> testTag.getType().equals(tagType));
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

                return testOutcome.getTags().stream()
                        .anyMatch(testTag -> testTag.getName().equalsIgnoreCase(tagName));

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

                return testOutcome.getTags().stream()
                        .anyMatch(testTag -> testTag.equals(expectedTag));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a test outcome with a tag ").appendValue(expectedTag);
            }
        };
    }
    
    public static Matcher<TestOutcome> withResult(final TestResult expectedResult) {
        return new BaseMatcher<TestOutcome>() {
            @Override
            public boolean matches(Object item) {
                return (item != null) && (item instanceof TestOutcome) && ((TestOutcome) item).getResult() == expectedResult;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("test outcome with result ").appendValue(expectedResult);
            }
        };
    }

}
