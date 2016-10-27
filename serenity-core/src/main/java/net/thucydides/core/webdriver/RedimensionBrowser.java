package net.thucydides.core.webdriver;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static net.thucydides.core.ThucydidesSystemProperty.DEFAULT_HEIGHT;
import static net.thucydides.core.ThucydidesSystemProperty.DEFAULT_WIDTH;

/**
 * Created by john on 25/06/2016.
 */
public class RedimensionBrowser {

    private final EnvironmentVariables environmentVariables;
    private final Class<? extends WebDriver> driverClass;

    public RedimensionBrowser(EnvironmentVariables environmentVariables, Class<? extends WebDriver> driverClass) {
        this.environmentVariables = environmentVariables;
        this.driverClass = driverClass;
    }

    public void withDriver(final WebDriver driver) {
        if (supportsScreenResizing(driver) && browserDimensionsSpecified()) {
            resizeBrowserTo(driver,
                            getRequestedBrowserSize().height,
                            getRequestedBrowserSize().width);
        }
    }


    private Dimension getRequestedBrowserSize() {
        int height = ThucydidesSystemProperty.THUCYDIDES_BROWSER_HEIGHT.integerFrom(environmentVariables, DEFAULT_HEIGHT);
        int width = ThucydidesSystemProperty.THUCYDIDES_BROWSER_WIDTH.integerFrom(environmentVariables, DEFAULT_WIDTH);
        return new Dimension(width, height);
    }

    private boolean browserDimensionsSpecified() {
        String snapshotWidth =  ThucydidesSystemProperty.THUCYDIDES_BROWSER_WIDTH.from(environmentVariables);
        String snapshotHeight = ThucydidesSystemProperty.THUCYDIDES_BROWSER_HEIGHT.from(environmentVariables);
        return (snapshotWidth != null) || (snapshotHeight != null);
    }

    private boolean supportsScreenResizing(final WebDriver driver) {
        boolean supportsResizing = DriverStrategySelector.inEnvironment(environmentVariables).supportsResizing(driverClass);

        return (supportsResizing) && isNotAMocked(driver);
    }

    private boolean isNotAMocked(WebDriver driver) {
        return (!(driver.getClass().getName().contains("Mock") || driver.toString().contains("Mock for")));
    }

    protected void resizeBrowserTo(WebDriver driver, int height, int width) {
        try {
            driver.manage().window().setSize(new Dimension(width, height));
            driver.manage().window().setPosition(new Point(0, 0));
        } catch (WebDriverException couldNotResizeScreen) {
            if (couldNotResizeScreen.getMessage().contains("Invalid requested size")) {
                maximiseBrowser(driver);
            }
        }
    }

    private void maximiseBrowser(WebDriver driver) {
        driver.manage().window().maximize();
    }

}
