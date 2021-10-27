package net.serenitybdd.cucumber.service;


import net.thucydides.core.guice.Injectors;
import net.thucydides.core.requirements.FileSystemRequirementsTagProvider;
import net.thucydides.core.statistics.service.ContextTagProvider;
import net.thucydides.core.statistics.service.InjectedTagProvider;
import net.thucydides.core.statistics.service.TagProvider;
import net.thucydides.core.statistics.service.TagProviderStrategy;
import net.thucydides.core.steps.TestSourceType;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.Arrays;

public class CucumberTagProviderStrategy implements TagProviderStrategy {

    private final EnvironmentVariables environmentVariables;

    public CucumberTagProviderStrategy(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public CucumberTagProviderStrategy() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    @Override
    public boolean canHandleTestSource(String testType) {
        return TestSourceType.TEST_SOURCE_CUCUMBER.getValue().equalsIgnoreCase(testType);
    }

    @Override
    public Iterable<? extends TagProvider> getTagProviders() {
        return Arrays.asList(
                new FileSystemRequirementsTagProvider(environmentVariables),
                new InjectedTagProvider(environmentVariables),
                new ContextTagProvider());
    }

    @Override
    public boolean hasHighPriority() {
        return false;
    }

}
