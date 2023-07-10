package net.thucydides.core.statistics.service;


import net.serenitybdd.core.collect.NewSet;
import net.thucydides.core.requirements.FileSystemRequirementsTagProvider;
import net.thucydides.core.requirements.TestOutcomeRequirementsTagProvider;
import net.thucydides.core.steps.TestSourceType;


public class JUnitTagProviderStrategy implements TagProviderStrategy {

    @Override
    public boolean canHandleTestSource(String testType) {
        return TestSourceType.TEST_SOURCE_JUNIT.getValue().equals(testType) ||
                TestSourceType.TEST_SOURCE_JUNIT5.getValue().equals(testType);
    }

    @Override
    public Iterable<? extends TagProvider> getTagProviders() {
        return NewSet.of(
                new TestOutcomeRequirementsTagProvider(),
//                new PackageRequirementsTagProvider(),
                new AnnotationBasedTagProvider(),
                new FileSystemRequirementsTagProvider(),
                new FeatureStoryTagProvider(),
                new InjectedTagProvider(),
                new ContextTagProvider()
        );
    }

    @Override
    public boolean hasHighPriority() {
        return false;
    }
}
