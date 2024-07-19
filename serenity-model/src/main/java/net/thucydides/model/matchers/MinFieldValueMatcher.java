package net.thucydides.model.matchers;

import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.min;
import static net.thucydides.model.matchers.dates.BeanFields.fieldValueIn;

public class MinFieldValueMatcher implements BeanCollectionMatcher {
    private final String fieldName;
    private final Matcher<? extends Comparable> valueMatcher;

    public MinFieldValueMatcher(String fieldName, Matcher<? extends Comparable> valueMatcher) {
        this.fieldName = fieldName;
        this.valueMatcher = valueMatcher;
    }

    public <T> boolean matches(Collection<T> elements) {

        List<Comparable> fieldValues = elements.stream()
                .map(element -> (Comparable) fieldValueIn(element).forField(fieldName))
                .collect(Collectors.toList());

        return valueMatcher.matches(min(fieldValues));
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
