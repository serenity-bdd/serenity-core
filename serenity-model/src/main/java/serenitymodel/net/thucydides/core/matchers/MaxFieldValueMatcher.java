package serenitymodel.net.thucydides.core.matchers;

import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import serenitymodel.net.thucydides.core.matchers.dates.BeanFields;

import static java.util.Collections.max;

public class MaxFieldValueMatcher implements BeanCollectionMatcher {
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
            List<Comparable> fieldValues = elements.stream()
                    .map(element -> (Comparable) BeanFields.fieldValueIn(element).forField(fieldName))
                    .collect(Collectors.toList());

            maximumValue = max(fieldValues);

        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find property value for " + fieldName);
        }
        return valueMatcher.matches(maximumValue);
    }

    @Override
    public String toString() {
        return "the maximum " + fieldName + " " + valueMatcher.toString();
    }
}
