package net.thucydides.core.requirements.reports;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class RequirementsOutcomesOfTypeCache {

    private final RequirementsOutcomes requirementsOutcomes;

    private static final long MAX_SIZE = 100;

    private final LoadingCache<String, RequirementsOutcomes> cache;

    public RequirementsOutcomesOfTypeCache(final RequirementsOutcomes requirementsOutcomes) {
        this.requirementsOutcomes = requirementsOutcomes;

        cache = CacheBuilder.newBuilder().maximumSize(MAX_SIZE).build(new CacheLoader<String, RequirementsOutcomes>() {
                                                                          @Override
                                                                          public RequirementsOutcomes load(String type) throws Exception {
                                                                              return requirementsOutcomes.ofType(type);
                                                                          }
                                                                      }
        );
    }

    public RequirementsOutcomes byType(String type) {
        return cache.getUnchecked(type);
    }

}
