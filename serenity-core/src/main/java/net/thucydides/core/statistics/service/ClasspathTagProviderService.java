package net.thucydides.core.statistics.service;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.ServiceLoader;

public class ClasspathTagProviderService implements TagProviderService {

    private TagProviderFilter<TagProvider> filter = new TagProviderFilter<>();

    public ClasspathTagProviderService() {
    }

    @Override
    public List<TagProvider> getTagProviders() {
        return getTagProviders(null);
    }

    @Override
    public List<TagProvider> getTagProviders(String testSource) {
            List<TagProvider> newTagProviders = Lists.newArrayList();
            Iterable<? extends TagProvider> tagProviderServiceLoader = loadTagProvidersFromPath(testSource);
            for (TagProvider tagProvider : tagProviderServiceLoader) {
                newTagProviders.add(tagProvider);
            }
            return filter.removeOverriddenProviders(newTagProviders);
    }

    protected Iterable<? extends TagProvider> loadTagProvidersFromPath(String testSource) {
        Iterable<TagProviderStrategy> tagProviderStrategies = ServiceLoader.load(TagProviderStrategy.class);
        Iterable<? extends TagProvider> tagProvidersWithHighPriority = tagProvidersWithHighPriority(tagProviderStrategies);
        if( tagProvidersWithHighPriority != null) {
            return tagProvidersWithHighPriority;
        }
        if (testSource == null) {
            return allKnownTagProviders(tagProviderStrategies);// ServiceLoader.load(TagProvider.class);
        } else {
            return tagProvidersThatCanProcess(tagProviderStrategies, testSource);
        }
    }

    private Iterable<? extends TagProvider> tagProvidersWithHighPriority(Iterable<TagProviderStrategy> tagProviderStrategies) {
        for (TagProviderStrategy strategy : tagProviderStrategies) {
            if (isHighPriority(strategy)) {
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
        return Lists.newArrayList();
    }

    private Iterable<TagProvider> allKnownTagProviders(Iterable<TagProviderStrategy> tagProviderStrategies) {
        List<TagProvider> tagProviders = Lists.newArrayList();

        for (TagProviderStrategy strategy : tagProviderStrategies) {
            tagProviders.addAll(Lists.newArrayList(strategy.getTagProviders()));
        }
        return tagProviders;
    }
}
