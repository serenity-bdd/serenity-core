package net.thucydides.junit.runners;

import net.serenity_bdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;

/**
 * @deprecated Replaced by SerenityParameterizedRunner
 *
 * Provided for backwards compatibility
 */
@Deprecated
public class ThucydidesParameterizedRunner extends SerenityParameterizedRunner {
    public ThucydidesParameterizedRunner(Class<?> klass, Configuration configuration, WebDriverFactory webDriverFactory, BatchManager batchManager) throws Throwable {
        super(klass, configuration, webDriverFactory, batchManager);
    }

    public ThucydidesParameterizedRunner(Class<?> klass) throws Throwable {
        super(klass);
    }
}
