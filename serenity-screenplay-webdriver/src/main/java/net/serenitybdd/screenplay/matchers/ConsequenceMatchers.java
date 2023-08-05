package net.serenitybdd.screenplay.matchers;

import com.google.common.base.Preconditions;
import net.serenitybdd.annotations.Fields;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class ConsequenceMatchers {

    public static <T> Matcher<T> displays(String propertyName, Matcher<?> valueMatcher) {
        return new AnswerMatcher<>(propertyName, valueMatcher);
    }

    public static class AnswerMatcher<T> extends TypeSafeMatcher<T> {
        private final String propertyName;
        private final Matcher<?> valueMatcher;

        @Override
        protected void describeMismatchSafely(T domainObject, Description mismatchDescription) {
            mismatchDescription.appendText("displayed  '")
                    .appendText(propertyName)
                    .appendText("' as ")
                    .appendValue(fieldsOf(domainObject).get(propertyName));
        }

        public AnswerMatcher(String propertyName, Matcher<?> valueMatcher) {
            this.propertyName = propertyName;
            this.valueMatcher = valueMatcher;
        }

        @Override
        protected boolean matchesSafely(T domainObject) {
            Preconditions.checkState(fieldsOf(domainObject).containsKey(propertyName), "Unknown display field '" + propertyName + "'. Must be one of: " + nonStaticFieldsOf(domainObject));

            return valueMatcher.matches(fieldsOf(domainObject).get(propertyName));
        }

        private Map<String, Object> fieldsOf(T domainObject) {
            return Fields.of(domainObject).asMap();
        }

        private String nonStaticFieldsOf(T domainObject) {
            ArrayList<String> fieldNames = new ArrayList<>();
            Set<Field> fields = Fields.of(domainObject.getClass()).nonStaticFields();
            for (Field field : fields) {
                fieldNames.add("'" + field.getName() + "'");
            }
            return StringUtils.join(fieldNames, ", ");
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("to display '")
                    .appendText(propertyName)
                    .appendText("' as ");
            valueMatcher.describeTo(description);
        }
    }
}
