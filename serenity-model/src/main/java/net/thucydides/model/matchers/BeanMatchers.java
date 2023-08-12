package net.thucydides.model.matchers;

import org.hamcrest.Matcher;

import java.math.BigDecimal;

public class BeanMatchers {

    public static BeanMatcher the(final String fieldName, final Matcher<? extends Object> matcher) {
        return new BeanPropertyMatcher(fieldName, matcher);
    }

    public static BeanMatcher the_count(Matcher<Integer> countMatcher) {
        return new BeanCountMatcher(countMatcher);
    }

    public static BeanConstraint each(final String fieldName) {
        return new BeanConstraint(fieldName);
    }

    public static BeanMatcher max(String fieldName, Matcher<? extends Comparable> valueMatcher) {
        return new MaxFieldValueMatcher(fieldName, valueMatcher);
    }

    public static BeanMatcher min(String fieldName, Matcher<? extends Comparable> valueMatcher) {
        return new MinFieldValueMatcher(fieldName, valueMatcher);
    }

    public static SimpleValueMatcher checkThat(final String value, final Matcher<? extends Object> matcher) {
        return new SimpleValueMatcher(value, matcher);
    }

    public static SimpleValueMatcher checkThat(final Boolean value, final Matcher<? extends Object> matcher) {
        return new SimpleValueMatcher(value, matcher);
    }

    public static BigDecimalValueMatcher checkThat(final BigDecimal value, final Matcher<? extends BigDecimal> matcher) {
        return new BigDecimalValueMatcher(value, matcher);
    }

    public static class BeanConstraint {
        private final String fieldName;

        public BeanConstraint(String fieldName) {
            this.fieldName = fieldName;
        }
        
        public BeanMatcher isDifferent() {
            return new BeanUniquenessMatcher(fieldName);
        }
    }


}
