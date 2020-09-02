package net.thucydides.core.statistics.service;

import net.thucydides.core.requirements.CoreTagProvider;
import net.thucydides.core.requirements.OverridableTagProvider;

import java.util.ArrayList;
import java.util.List;

public class TagProviderFilter<T extends TagProvider> {

    public List<T> removeOverriddenProviders(List<T> providers) {
        if (additionalTagProvidersArePresentIn(providers)) {
            return removeOverridableProvidersFrom(providers);
        } else {
            return providers;
        }
    }

    private boolean additionalTagProvidersArePresentIn(List<T> providers) {
        for(TagProvider provider : providers) {
            if (!(CoreTagProvider.class.isAssignableFrom(provider.getClass()))) {
                return true;
            }
        }
        return false;
    }

    private List<T> removeOverridableProvidersFrom(List<T> providers) {
        List<T> retainedProviders = new ArrayList<>();
        for(T provider : providers) {
            if (!OverridableTagProvider.class.isAssignableFrom(provider.getClass())) {
                retainedProviders.add(provider);
            }
        }
        return retainedProviders;
    }
}
