package net.thucydides.core.statistics.service;

public interface TagProviderStrategy {

    boolean canHandleTestSource(String testSource);

    Iterable<TagProvider> getTagProviders();
}
