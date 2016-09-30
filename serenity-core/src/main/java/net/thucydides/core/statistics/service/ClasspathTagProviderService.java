package net.thucydides.core.statistics.service;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ServiceLoader;

public class ClasspathTagProviderService implements TagProviderService {

    private final Logger logger = LoggerFactory.getLogger(ClasspathTagProviderService.class);

    private List<TagProvider> tagProviders;

    private TagProviderFilter<TagProvider> filter = new TagProviderFilter<>();

    public ClasspathTagProviderService() {
    }

    @Override
    public List<TagProvider> getTagProviders() {
        return getTagProviders(null);
    }

    @Override
    public List<TagProvider> getTagProviders(String testSource) {
        if (tagProviders == null) {
            List<TagProvider> newTagProviders = Lists.newArrayList();
            Iterable<? extends TagProvider> tagProviderServiceLoader = loadTagProvidersFromPath(testSource);
            for (TagProvider tagProvider : tagProviderServiceLoader) {
                newTagProviders.add(tagProvider);
            }
            tagProviders = filter.removeOverriddenProviders(newTagProviders);
        }
        return tagProviders;
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
