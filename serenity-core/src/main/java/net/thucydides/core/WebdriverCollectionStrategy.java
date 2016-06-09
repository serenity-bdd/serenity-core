package net.thucydides.core;

import net.thucydides.core.reports.TestOutcomesCompromised;
import net.thucydides.core.util.EnvironmentVariables;

public enum WebdriverCollectionStrategy {
    Optimistic, Pessimistic;

    public static WebdriverCollectionStrategy definedIn(EnvironmentVariables environmentVariables) {
        String configuredStrategy = ThucydidesSystemProperty.SERENITY_WEBDRIVER_COLLECTION_LOADING_STRATEGY.from(environmentVariables,"");
        try {
            return valueOf(configuredStrategy);
        } catch(IllegalArgumentException invalidEnumValue) {
            throw new TestOutcomesCompromised("Illegal value for " + ThucydidesSystemProperty.SERENITY_WEBDRIVER_COLLECTION_LOADING_STRATEGY
                                            + ", should be one of " + WebdriverCollectionStrategy.values());
        }
    }
}
