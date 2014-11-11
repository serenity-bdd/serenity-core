package net.thucydides.junit.runners;

import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;

/**
 * An alternative name for the ThucydidesParameterizedRunner class.
 */
public class SerenityParameterizedRunner extends ThucydidesParameterizedRunner {
    public SerenityParameterizedRunner(Class<?> klass, Configuration configuration, WebDriverFactory webDriverFactory, BatchManager batchManager) throws Throwable {
        super(klass, configuration, webDriverFactory, batchManager);
    }

    public SerenityParameterizedRunner(Class<?> klass) throws Throwable {
        super(klass);
    }
}
