package net.thucydides.core.statistics.service;

public interface TagProviderStrategy {

    public boolean canHandleTestSource(String testSource);

    public Iterable<TagProvider> getTagProviders();
}
