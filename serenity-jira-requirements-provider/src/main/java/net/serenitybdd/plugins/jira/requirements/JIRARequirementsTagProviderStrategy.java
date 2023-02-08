package net.serenitybdd.plugins.jira.requirements;

import net.thucydides.core.statistics.service.TagProvider;
import net.thucydides.core.statistics.service.TagProviderStrategy;

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
