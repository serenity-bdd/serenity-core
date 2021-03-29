package serenitymodel.net.thucydides.core.matchers;

import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import serenitymodel.net.thucydides.core.matchers.dates.BeanFields;

import static java.util.Collections.min;

public class MinFieldValueMatcher implements BeanCollectionMatcher {
    private final String fieldName;
    private final Matcher<? extends Comparable> valueMatcher;

    public MinFieldValueMatcher(String fieldName, Matcher<? extends Comparable> valueMatcher) {
        this.fieldName = fieldName;
        this.valueMatcher = valueMatcher;
    }

    public <T> boolean matches(Collection<T> elements) {

        List<Comparable> fieldValues = elements.stream()
                .map(element -> (Comparable) BeanFields.fieldValueIn(element).forField(fieldName))
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
