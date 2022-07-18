package net.serenitybdd.core.webdriver.driverproviders;

import com.google.common.base.Splitter;
import net.serenitybdd.core.webdriver.driverproviders.webdrivermanager.WebDriverManagerSetup;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class DownloadableDriverProvider implements DriverProvider {

    private final static Logger LOGGER = LoggerFactory.getLogger(DriverProvider.class);

    protected List<String> argumentsIn(String options) {
        return Splitter.on(";").omitEmptyStrings().splitToList(options);
    }

    private final static Set<String> DOWNLOADED_DRIVERS = new CopyOnWriteArraySet<>();

    protected void downloadDriverIfRequired(String driver, EnvironmentVariables environmentVariables) {
        if (!DOWNLOADED_DRIVERS.contains(driver)) {
            synchronized (DOWNLOADED_DRIVERS) {
                DOWNLOADED_DRIVERS.add(driver);
                if (isDriverAutomaticallyDownloaded(environmentVariables)) {
                    LOGGER.info("Using automatically driver download");
                    switch (driver) {
                        case "chrome":
                            WebDriverManagerSetup.usingEnvironmentVariables(environmentVariables).forChrome();
                            break;
                        case "edge":
                            WebDriverManagerSetup.usingEnvironmentVariables(environmentVariables).forEdge();
                            break;
                        case "firefox":
                            WebDriverManagerSetup.usingEnvironmentVariables(environmentVariables).forFirefox();
                            break;
                        case "ie":
                        case "iexplore":
                            WebDriverManagerSetup.usingEnvironmentVariables(environmentVariables).forIE();
                            break;
                        case "safari":
                            WebDriverManagerSetup.usingEnvironmentVariables(environmentVariables).forSafari();
                            break;
                        case "opera":
                            WebDriverManagerSetup.usingEnvironmentVariables(environmentVariables).forOpera();
                            break;
                    }
                } else {
                    LOGGER.info("Not using automatically driver download");
                }
            }
        }
    }
}

