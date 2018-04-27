package net.thucydides.junit.runners;

import net.serenitybdd.junit.runners.*;
import net.thucydides.core.batches.*;
import net.thucydides.core.webdriver.*;

/**
 * @deprecated Replaced by SerenityParameterizedRunner
 *
 * Provided for backwards compatibility
 */
@Deprecated
public class ThucydidesParameterizedRunner extends SerenityParameterizedRunner {
    public ThucydidesParameterizedRunner(Class<?> klass, DriverConfiguration configuration, WebDriverFactory webDriverFactory, BatchManager batchManager) throws Throwable {
        super(klass, configuration, webDriverFactory, batchManager);
    }

    public ThucydidesParameterizedRunner(Class<?> klass) throws Throwable {
        super(klass);
    }
}
