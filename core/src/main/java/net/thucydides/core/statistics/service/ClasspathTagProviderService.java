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
        if (tagProviders == null) {
            List<TagProvider> newTagProviders = Lists.newArrayList();

            Iterable<TagProvider> tagProviderServiceLoader = loadTagProvidersFromPath();

            for (TagProvider tagProvider : tagProviderServiceLoader) {
                newTagProviders.add(tagProvider);
            }
            tagProviders = filter.removeOverriddenProviders(newTagProviders);
        }
        return tagProviders;
    }

    protected Iterable<TagProvider> loadTagProvidersFromPath() {
        return ServiceLoader.load(TagProvider.class);
    }
}
