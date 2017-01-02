package net.thucydides.core.webdriver.redimension;

import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

enum RedimensionStrategy {
    DoNotRedimension,
    RedimensionToSpecifiedSize,
    Maximize;

    public static RedimensionStrategy strategyFor(WebDriver driver, EnvironmentVariables environmentVariables) {

        RedimensionConfiguration redimensionConfiguration = new RedimensionConfiguration(environmentVariables);

        if (!redimensionConfiguration.supportsScreenResizing(driver)) {
            return DoNotRedimension;
        }

        if (redimensionConfiguration.isBrowserDimensionsSpecified()) {
            return RedimensionToSpecifiedSize;
        }

        if (redimensionConfiguration.isBrowserMaximised()) {
            return RedimensionToSpecifiedSize;
        }

        return DoNotRedimension;
    }
}