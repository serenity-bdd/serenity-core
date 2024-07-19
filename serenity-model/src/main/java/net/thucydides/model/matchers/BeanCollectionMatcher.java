package net.thucydides.model.matchers;

import java.util.Collection;

public interface BeanCollectionMatcher extends BeanMatcher{
    <T> boolean matches(Collection<T> elements);
}
