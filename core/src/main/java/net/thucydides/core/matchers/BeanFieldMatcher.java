package net.thucydides.core.matchers;

import org.hamcrest.Matcher;

public interface BeanFieldMatcher extends BeanMatcher{
    <T> Matcher<T> getMatcher();
}
