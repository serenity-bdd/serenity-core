package net.serenitybdd.screenplay.matchers;

import com.google.common.base.Preconditions;
import net.thucydides.core.annotations.Fields;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Map;

public class ConsequenceMatchers {

    public static <T> Matcher<T> displays(String propertyName, Matcher<?> valueMatcher) {
        return new AnswerMatcher(propertyName, valueMatcher);
    }

    public static class AnswerMatcher<T> extends TypeSafeMatcher<T> {
        private final String propertyName;
        private final Matcher<?> valueMatcher;

        public AnswerMatcher(String propertyName, Matcher<?> valueMatcher) {
            this.propertyName = propertyName;
            this.valueMatcher = valueMatcher;
        }

        @Override
        protected boolean matchesSafely(T domainObject) {
            Preconditions.checkState(fieldsOf(domainObject).containsKey(propertyName),"Unknown domain field " + propertyName);

            return valueMatcher.matches(fieldsOf(domainObject).get(propertyName));
        }

        private Map<String, Object> fieldsOf(T domainObject) {
            return Fields.of(domainObject).asMap();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("showing '")
                       .appendText(propertyName)
                       .appendText("' as ");
            valueMatcher.describeTo(description);
        }
    }
}
