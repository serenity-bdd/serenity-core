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
            Iterable<TagProvider> tagProviderServiceLoader = loadTagProvidersFromPath(testSource);
            for (TagProvider tagProvider : tagProviderServiceLoader) {
                newTagProviders.add(tagProvider);
            }
            tagProviders = filter.removeOverriddenProviders(newTagProviders);
        }
        return tagProviders;
    }

    protected Iterable<TagProvider> loadTagProvidersFromPath(String testSource) {
        if (testSource == null) {
            return ServiceLoader.load(TagProvider.class);
        } else {
            Iterable<TagProviderStrategy> tagProviderStrategies = ServiceLoader.load(TagProviderStrategy.class);
            for (TagProviderStrategy strategy : tagProviderStrategies) {
                if (strategy.canHandleTestSource(testSource)) {
                    return strategy.getTagProviders();
                }
            }
        }
        return null;
    }
}
