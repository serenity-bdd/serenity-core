package net.thucydides.model.matchers;

import org.hamcrest.Matcher;

import java.util.Collection;

public class BeanCountMatcher implements BeanCollectionMatcher {
    
    private final Matcher<Integer> countMatcher;

    public BeanCountMatcher(Matcher<Integer> countMatcher) {
        this.countMatcher = countMatcher;
    }

    public <T> boolean matches(Collection<T> elements) {
        return countMatcher.matches(elements.size());

    }

    @Override
    public String toString() {
        return  "number of matching entries " + countMatcher;
    }

    @Override
    public boolean matches(Object target) {
        return matches((Collection) target);
    }
}
