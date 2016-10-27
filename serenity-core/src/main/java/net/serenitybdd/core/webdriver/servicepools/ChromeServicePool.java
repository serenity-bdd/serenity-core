package net.serenitybdd.core.webdriver.servicepools;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeDriverService;

public class ChromeServicePool extends DriverServicePool<ChromeDriverService> {

    public ChromeServicePool() {
        super();
        updateChromePathIfSpecifiedIn(environmentVariables);
    }

    protected String serviceName(){ return "chrome"; }

    @Override
    protected ChromeDriverService newDriverService() {
        return new ChromeDriverService.Builder()
                .usingAnyFreePort()
                .build();
    }

    private void updateChromePathIfSpecifiedIn(EnvironmentVariables environmentVariables) {
        String environmentDefinedChromeDriverPath = environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_CHROME_DRIVER);
        if (StringUtils.isNotEmpty(environmentDefinedChromeDriverPath)) {
            System.setProperty(ThucydidesSystemProperty.WEBDRIVER_CHROME_DRIVER.toString(), environmentDefinedChromeDriverPath);
        }
    }

}
