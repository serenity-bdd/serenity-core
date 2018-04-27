package net.thucydides.core.statistics.service;

import java.util.List;

public interface TagProviderService {

    /**
     * Return all available tag providers.
     */
    List<TagProvider> getTagProviders();

    /**
     * Return the <code>TagProvider<code/>s for a given testSource.
     */
    List<TagProvider> getTagProviders(String testSource);
}
