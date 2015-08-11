package net.thucydides.core.matchers;

import java.util.Collection;

public interface BeanCollectionMatcher extends BeanMatcher{
    <T> boolean matches(Collection<T> elements);
}
