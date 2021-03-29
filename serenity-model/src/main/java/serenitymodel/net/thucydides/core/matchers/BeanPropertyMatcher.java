package serenitymodel.net.thucydides.core.matchers;

import org.hamcrest.Matcher;
import serenitymodel.net.thucydides.core.matchers.dates.BeanFields;

public class BeanPropertyMatcher implements BeanFieldMatcher {
    private final String fieldName;
    private final Matcher<? extends Object> matcher;

    public BeanPropertyMatcher(String fieldName, Matcher<? extends Object> matcher) {
        this.fieldName = fieldName;
        this.matcher = matcher;
    }

    @Override
    public boolean matches(final Object bean) {

        return matcher.matches(BeanFields.fieldValueIn(bean).forField(fieldName));
    }

    @Override
    public <T> Matcher<T> getMatcher() {
        return new InstantiatedBeanMatcher<T>(this);
    }

    @Override
    public String toString() {
        String matcherDescription = matcher.toString();
        String htmlFriendlyMatcherDescription = matcherDescription.replaceAll("\"", "'");
        return fieldName + " " + htmlFriendlyMatcherDescription;
    }
}