package net.thucydides.model.requirements;


import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.statistics.service.TagProvider;
import net.thucydides.model.statistics.service.TagProviderFilter;
import net.thucydides.model.statistics.service.TagProviderService;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a way to obtain the list of requirements providers.
 * Requirements providers are a special type of tag providers that also provide a list of system requirements.
 * Custom requirements providers can be placed on the classpath along with a file in the META-INF.services
 * directory called net.thucydides.model.statistics.service.TagProvider.old that lists the fully qualified class
 * name for the class.
 */
public class ClasspathRequirementsProviderService implements RequirementsProviderService {

    private TagProviderService tagProviderService;

    private List<RequirementsTagProvider> requirementsTagProviders;


    private TagProviderFilter<RequirementsTagProvider> filter = new TagProviderFilter<>();

    
    public ClasspathRequirementsProviderService(TagProviderService tagProviderService) {
        this.tagProviderService = tagProviderService;
    }


    public List<RequirementsTagProvider> getRequirementsProviders() {

        if (requirementsTagProviders == null) {
            requirementsTagProviders = loadRequirementsTagProviders();
        }

        return NewList.copyOf(requirementsTagProviders);
    }

    private List<RequirementsTagProvider> loadRequirementsTagProviders() {
        List<RequirementsTagProvider> providers = new ArrayList<>();

        List<TagProvider> tagProviders = tagProviderService.getTagProviders();
        for (TagProvider tagProvider : tagProviders) {
            if (tagProvider instanceof RequirementsTagProvider) {
                providers.add((RequirementsTagProvider)tagProvider);
            }
        }

        return filter.removeOverriddenProviders(providers);
    }

}
