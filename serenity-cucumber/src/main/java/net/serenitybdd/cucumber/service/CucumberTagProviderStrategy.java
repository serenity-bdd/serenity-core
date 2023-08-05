package net.serenitybdd.cucumber.service;


import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.requirements.FileSystemRequirementsTagProvider;
import net.thucydides.model.statistics.service.ContextTagProvider;
import net.thucydides.model.statistics.service.InjectedTagProvider;
import net.thucydides.model.statistics.service.TagProvider;
import net.thucydides.model.statistics.service.TagProviderStrategy;
import net.thucydides.model.steps.TestSourceType;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.Arrays;

public class CucumberTagProviderStrategy implements TagProviderStrategy {

    private final EnvironmentVariables environmentVariables;

    public CucumberTagProviderStrategy(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public CucumberTagProviderStrategy() {
        this(SystemEnvironmentVariables.currentEnvironmentVariables());
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
