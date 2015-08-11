package net.thucydides.core.matchers;

import ch.lambdaj.function.convert.Converter;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.convert;
import static java.util.Collections.max;
import static net.thucydides.core.matchers.dates.BeanFields.fieldValueIn;

class MaxFieldValueMatcher implements BeanCollectionMatcher {
    private final String fieldName;
    private final Matcher<? extends Comparable> valueMatcher;

    public MaxFieldValueMatcher(String fieldName, Matcher<? extends Comparable> valueMatcher) {
        this.fieldName = fieldName;
        this.valueMatcher = valueMatcher;
    }

    @Override
    public boolean matches(Object target) {
        return matches((Collection) target);
    }

    public <T> boolean matches(Collection<T> elements) {
        Comparable maximumValue = null;
        try {
            List<Comparable> fieldValues = convert(elements, toComparable());
            maximumValue = max(fieldValues);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find property value for " + fieldName);
        }
        return valueMatcher.matches(maximumValue);
    }

    private <T> Converter<T, Comparable> toComparable() {
        return new Converter<T, Comparable>() {
            @Override
            public Comparable convert(T bean) {
                return (Comparable) fieldValueIn(bean).forField(fieldName);
            }
        };
    }

    @Override
    public String toString() {
        return "the maximum " + fieldName + " " + valueMatcher.toString();
    }
}
