package net.thucydides.junit.runners;

import com.google.inject.*;
import net.serenitybdd.junit.runners.*;
import net.thucydides.core.batches.*;
import net.thucydides.core.webdriver.*;
import org.junit.runners.model.*;

/**
 * @deprecated Replaced by SerenityRunner
 *
 * Provided for backwards compatibility
 */
@Deprecated
public class ThucydidesRunner extends SerenityRunner {
    public ThucydidesRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    public ThucydidesRunner(Class<?> klass, com.google.inject.Module module) throws InitializationError {
        super(klass, module);
    }

    public ThucydidesRunner(Class<?> klass, Injector injector) throws InitializationError {
        super(klass, injector);
    }

    public ThucydidesRunner(Class<?> klass, WebDriverFactory webDriverFactory) throws InitializationError {
        super(klass, webDriverFactory);
    }

    public ThucydidesRunner(Class<?> klass, WebDriverFactory webDriverFactory, DriverConfiguration configuration) throws InitializationError {
        super(klass, webDriverFactory, configuration);
    }

    public ThucydidesRunner(Class<?> klass, WebDriverFactory webDriverFactory, DriverConfiguration configuration, BatchManager batchManager) throws InitializationError {
        super(klass, webDriverFactory, configuration, batchManager);
    }

    public ThucydidesRunner(Class<?> klass, BatchManager batchManager) throws InitializationError {
        super(klass, batchManager);
    }

    public ThucydidesRunner(Class<?> klass, WebdriverManager webDriverManager, DriverConfiguration configuration, BatchManager batchManager) throws InitializationError {
        super(klass, webDriverManager, configuration, batchManager);
    }
}
