package net.thucydides.core.webdriver.redimension;

import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

enum RedimensionStrategy {
    DoNotRedimension,
    RedimensionToSpecifiedSize,
    Maximize,
    FullScreen;

    public static RedimensionStrategy strategyFor(WebDriver driver, EnvironmentVariables environmentVariables) {

        RedimensionConfiguration redimensionConfiguration = new RedimensionConfiguration(environmentVariables);

        if (redimensionConfiguration.isDisabled() || !redimensionConfiguration.supportsScreenResizing(driver)) {
            return DoNotRedimension;
        }

        if (redimensionConfiguration.isBrowserDimensionsSpecified()) {
            return RedimensionToSpecifiedSize;
        }

        if (redimensionConfiguration.isBrowserMaximised()) {
            return Maximize;
        }

        if (redimensionConfiguration.isBrowserFullScreen()) {
            return FullScreen;
        }

        return DoNotRedimension;
    }
}
