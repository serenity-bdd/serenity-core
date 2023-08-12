package net.thucydides.model.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

class InstantiatedBeanMatcher<T> extends TypeSafeMatcher<T> {

    private final BeanPropertyMatcher propertyMatcher;

    public InstantiatedBeanMatcher(final BeanPropertyMatcher propertyMatcher) {
        this.propertyMatcher = propertyMatcher;
    }

    @Override
    public boolean matchesSafely(Object bean) {
        return propertyMatcher.matches(bean);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(propertyMatcher.toString());
    }
}
