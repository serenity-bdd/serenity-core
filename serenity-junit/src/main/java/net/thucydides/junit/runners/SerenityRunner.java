package net.thucydides.junit.runners;

import com.google.inject.Injector;
import com.google.inject.Module;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;
import org.junit.runners.model.InitializationError;

/**
 * An alternative name for the ThucydidesRunner
 */
public class SerenityRunner extends ThucydidesRunner{
    public SerenityRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    public SerenityRunner(Class<?> klass, Module module) throws InitializationError {
        super(klass, module);
    }

    public SerenityRunner(Class<?> klass, Injector injector) throws InitializationError {
        super(klass, injector);
    }

    public SerenityRunner(Class<?> klass, WebDriverFactory webDriverFactory) throws InitializationError {
        super(klass, webDriverFactory);
    }

    public SerenityRunner(Class<?> klass, WebDriverFactory webDriverFactory, Configuration configuration) throws InitializationError {
        super(klass, webDriverFactory, configuration);
    }

    public SerenityRunner(Class<?> klass, WebDriverFactory webDriverFactory, Configuration configuration, BatchManager batchManager) throws InitializationError {
        super(klass, webDriverFactory, configuration, batchManager);
    }

    public SerenityRunner(Class<?> klass, BatchManager batchManager) throws InitializationError {
        super(klass, batchManager);
    }

    public SerenityRunner(Class<?> klass, WebdriverManager webDriverManager, Configuration configuration, BatchManager batchManager) throws InitializationError {
        super(klass, webDriverManager, configuration, batchManager);
    }
}
