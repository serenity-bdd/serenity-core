package net.thucydides.model.matchers;

import org.hamcrest.Matcher;

public interface BeanFieldMatcher extends BeanMatcher{
    <T> Matcher<T> getMatcher();
}
