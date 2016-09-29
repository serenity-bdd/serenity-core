package net.thucydides.core.statistics.service;


import com.google.common.collect.ImmutableSet;
import net.thucydides.core.requirements.FileSystemRequirementsTagProvider;
import net.thucydides.core.requirements.PackageRequirementsTagProvider;
import net.thucydides.core.steps.StepEventBus;


public class JUnitTagProviderStrategy implements TagProviderStrategy {

    @Override
    public boolean canHandleTestSource(String testType) {
        return StepEventBus.TEST_SOURCE_JUNIT.equals(testType);
    }

    @Override
    public Iterable<? extends TagProvider> getTagProviders() {
        return ImmutableSet.of(
                new PackageRequirementsTagProvider(),
                new AnnotationBasedTagProvider(),
                new FileSystemRequirementsTagProvider(),
                new FeatureStoryTagProvider()
        );
    }

    @Override
    public boolean hasHighPriority() {
        return false;
    }
}
