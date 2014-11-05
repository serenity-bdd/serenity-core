package net.thucydides.core.matchers;

import ch.lambdaj.function.convert.Converter;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.convert;
import static java.util.Collections.min;
import static net.thucydides.core.matchers.dates.BeanFields.fieldValueIn;

class MinFieldValueMatcher implements BeanCollectionMatcher {
    private final String fieldName;
    private final Matcher<? extends Comparable> valueMatcher;

    public MinFieldValueMatcher(String fieldName, Matcher<? extends Comparable> valueMatcher) {
        this.fieldName = fieldName;
        this.valueMatcher = valueMatcher;
    }

    public <T> boolean matches(Collection<T> elements) {
        List<Comparable> fieldValues = convert(elements, toComparable());
        return valueMatcher.matches(min(fieldValues));
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
        return "the minimum " + fieldName + " " + valueMatcher.toString();
    }

    @Override
    public boolean matches(Object target) {
        return matches( (Collection) target);
    }
}
