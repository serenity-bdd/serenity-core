package net.serenitybdd.rest.filters;

import io.restassured.filter.Filter;

import java.util.*;

/**
 * User: YamStranger
 * Date: 4/16/16
 * Time: 5:52 PM
 */
public class SkipClassIterator implements Iterator<Filter> {
    final private Set<Class> skipping = new HashSet<>();
    final private Iterator<Filter> core;

    public SkipClassIterator(final Iterator<Filter> iterator, final Class... skip) {
        this(iterator, Arrays.asList(skip));
    }

    public SkipClassIterator(final Iterator<Filter> iterator, final Collection<Class> skipping) {
        this.core = iterator;
        this.skipping.addAll(skipping);
    }

    @Override
    public boolean hasNext() {
        if (core.hasNext()) {
            final Object filter = core.next();
            for (final Class search : skipping) {
                if (filter.getClass().isAssignableFrom(search)) {
                    return this.hasNext();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Filter next() {
        return core.next();
    }

    @Override
    public void remove() {
        core.remove();
    }
}
