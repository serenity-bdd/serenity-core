package net.thucydides.core;

import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.*;

public enum WebdriverCollectionStrategy {
    Optimistic, Pessimistic, Paranoid;

    private static final Logger logger = LoggerFactory.getLogger(WebdriverCollectionStrategy.class);

    private static final WebdriverCollectionStrategy DEFAULT_STRATEGY = Optimistic;

    public static WebdriverCollectionStrategy definedIn(EnvironmentVariables environmentVariables) {
        String configuredStrategy = ThucydidesSystemProperty.SERENITY_WEBDRIVER_COLLECTION_LOADING_STRATEGY.from(environmentVariables,"");

        try {
            if (isNotEmpty(configuredStrategy)) {
                return valueOf(capitalize(lowerCase(configuredStrategy)));
            }
        } catch(IllegalArgumentException invalidEnumValue) {
            logger.warn("Illegal value for {} - should be one of {}",
                        ThucydidesSystemProperty.SERENITY_WEBDRIVER_COLLECTION_LOADING_STRATEGY,
                        WebdriverCollectionStrategy.values());
        }
        return DEFAULT_STRATEGY;
    }
}
