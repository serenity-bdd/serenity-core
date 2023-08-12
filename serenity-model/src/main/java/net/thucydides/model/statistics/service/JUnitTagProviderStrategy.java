package net.thucydides.model.statistics.service;


import net.serenitybdd.model.collect.NewSet;
import net.thucydides.model.requirements.FileSystemRequirementsTagProvider;
import net.thucydides.model.requirements.TestOutcomeRequirementsTagProvider;
import net.thucydides.model.steps.TestSourceType;


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
