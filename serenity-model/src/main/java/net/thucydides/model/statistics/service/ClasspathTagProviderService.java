package net.thucydides.model.statistics.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class ClasspathTagProviderService implements TagProviderService {

    private TagProviderFilter<TagProvider> filter = new TagProviderFilter<>();

    public ClasspathTagProviderService() {
    }

    @Override
    public List<TagProvider> getTagProviders() {
        return getTagProviders(null);
    }

    private final String ALL_TAG_PROVIDERS = "ALL";

    Map<String, List<TagProvider>> tagProviderCache = new ConcurrentHashMap<>();

    @Override
    public List<TagProvider> getTagProviders(String testSource) {
        if (!tagProviderCache.containsKey(forTestSource(testSource))) {
            tagProviderCache.put(forTestSource(testSource), tagProvidersFor(testSource));
        }
        return tagProviderCache.get(forTestSource(testSource));

    }

    private List<TagProvider> tagProvidersFor(String testSource) {
        List<TagProvider> newTagProviders = new ArrayList<>();
        Iterable<? extends TagProvider> tagProviderServiceLoader = loadTagProvidersFromPath(testSource);
        for (TagProvider tagProvider : tagProviderServiceLoader) {
            if (newTagProviders.stream().anyMatch(provider -> provider.getClass().equals(tagProvider.getClass()))) {
                continue;
            }
            newTagProviders.add(tagProvider);
        }
        return filter.removeOverriddenProviders(newTagProviders);
    }

    private String forTestSource(String testSource) {
        return (testSource == null) ? ALL_TAG_PROVIDERS : testSource;
    }

    protected Iterable<? extends TagProvider> loadTagProvidersFromPath(String testSource) {
        Iterable<TagProviderStrategy> tagProviderStrategies = ServiceLoader.load(TagProviderStrategy.class);
        Iterable<? extends TagProvider> tagProvidersWithHighPriority = tagProvidersWithHighPriority(tagProviderStrategies, testSource);
        if( tagProvidersWithHighPriority != null){
            return tagProvidersWithHighPriority;
        }
        if (testSource == null) {
            return allKnownTagProviders(tagProviderStrategies);// ServiceLoader.load(TagProvider.class);
        } else {
            return tagProvidersThatCanProcess(tagProviderStrategies, testSource);
        }
    }

    private Iterable<? extends TagProvider> tagProvidersWithHighPriority(Iterable<TagProviderStrategy> tagProviderStrategies, String testSource) {
        for (TagProviderStrategy strategy : tagProviderStrategies) {
            if (isHighPriority(strategy) && strategy.canHandleTestSource(testSource)) {
                return strategy.getTagProviders();
            }
        }
        return null;
    }

    private boolean isHighPriority(TagProviderStrategy strategy) {
        try {
            return strategy.hasHighPriority();
        } catch(AbstractMethodError usingAnOldAPI) {
            return false;
        }
    }

    private Iterable<? extends TagProvider> tagProvidersThatCanProcess(Iterable<TagProviderStrategy> tagProviderStrategies,String testSource) {
        for (TagProviderStrategy strategy : tagProviderStrategies) {
            if (strategy.canHandleTestSource(testSource)) {
                return strategy.getTagProviders();
            }
        }
        return new ArrayList<>();
    }

    private Iterable<TagProvider> allKnownTagProviders(Iterable<TagProviderStrategy> tagProviderStrategies) {
        List<TagProvider> tagProviders = new ArrayList<>();

        for (TagProviderStrategy strategy : tagProviderStrategies) {
            strategy.getTagProviders().forEach(
                    tagProviders::add
            );
        }
        return tagProviders;
    }
}
