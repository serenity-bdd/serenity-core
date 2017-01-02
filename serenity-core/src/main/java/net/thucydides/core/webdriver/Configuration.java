package net.thucydides.core.webdriver;

import com.google.common.base.Optional;
import net.thucydides.core.model.TakeScreenshots;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;

public interface Configuration {
    SupportedWebDriver getDriverType();

    int getStepDelay();

    int getElementTimeout();

    /**
     * Use shouldUseAUniqueBrowser() instead
     */
    @Deprecated
    boolean getUseUniqueBrowser();

    boolean shouldUseAUniqueBrowser();

    void setOutputDirectory(File outputDirectory);

    File getOutputDirectory();

    double getEstimatedAverageStepCount();

    boolean onlySaveFailingScreenshots();

    void setDefaultBaseUrl(final String defaultBaseUrl);

    int getRestartFrequency();
    /**
     * This is the URL where test cases start.
     * The default value can be overriden using the webdriver.baseurl property.
     * It is also the base URL used to build relative paths.
     */

    int getCurrentTestCount();

    String getBaseUrl();

    /**
     * Take a screenshot for each action.
     */
    boolean takeVerboseScreenshots();

    /**
     * How often should screenshots be taken.
     */
    Optional<TakeScreenshots> getScreenshotLevel();

    void setIfUndefined(String property, String value);

    Configuration copy();

    Configuration withEnvironmentVariables(EnvironmentVariables environmentVariables);

    EnvironmentVariables getEnvironmentVariables();
}
