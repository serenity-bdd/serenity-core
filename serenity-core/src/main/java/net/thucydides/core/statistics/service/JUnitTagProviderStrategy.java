package net.thucydides.core.statistics.service;


import net.thucydides.core.steps.StepEventBus;

import java.util.ServiceLoader;


public class JUnitTagProviderStrategy implements TagProviderStrategy {

    @Override
    public boolean canHandleTestSource(String testType) {
        return StepEventBus.TEST_SOURCE_JUNIT.equals(testType);
    }

    @Override
    public Iterable<TagProvider> getTagProviders() {
        return ServiceLoader.load(TagProvider.class);
    }
}
