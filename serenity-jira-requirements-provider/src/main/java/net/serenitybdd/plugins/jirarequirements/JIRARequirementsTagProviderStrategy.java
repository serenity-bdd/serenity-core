package net.serenitybdd.plugins.jirarequirements;

import net.thucydides.model.statistics.service.TagProvider;
import net.thucydides.model.statistics.service.TagProviderStrategy;

import java.util.Collections;


public class JIRARequirementsTagProviderStrategy implements TagProviderStrategy {

    @Override
    public boolean canHandleTestSource(String testSource) {
        return false;
    }

    @Override
    public Iterable<? extends TagProvider> getTagProviders() {
        return Collections.singletonList(new JIRARequirementsProvider());
    }


    @Override
    public boolean hasHighPriority() {
        return true;
    }
}
