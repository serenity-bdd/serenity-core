package net.thucydides.core.statistics.service;

import java.util.Iterator;

public interface TagProviderStrategy {

    public boolean canHandleTestSource(String testSource);

    public Iterable<TagProvider> getTagProviders();
}
