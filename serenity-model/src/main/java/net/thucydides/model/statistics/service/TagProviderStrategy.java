package net.thucydides.model.statistics.service;

public interface TagProviderStrategy {

    boolean canHandleTestSource(String testSource);

    Iterable<? extends TagProvider> getTagProviders();

    /**
     * In case that true is returned from this method,
     * the TagProviders returned by this implementation will have higher priority
     * as the <code>canHandleTestSource</code>
     */
    boolean hasHighPriority();
}
