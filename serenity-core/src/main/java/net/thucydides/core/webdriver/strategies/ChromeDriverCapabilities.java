package net.thucydides.core.webdriver.strategies;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.chrome.OptionsSplitter;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

public class ChromeDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;

    public ChromeDriverCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public DesiredCapabilities getCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        String chromeSwitches = environmentVariables.getProperty(ThucydidesSystemProperty.CHROME_SWITCHES);
        ChromeOptions chromeOptions = optionsFromSwitches(chromeSwitches);
        updateChromeBinaryIfSpecified(chromeOptions);
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        capabilities.setCapability("chrome.switches", chromeSwitches);
        return capabilities;
    }


    private ChromeOptions optionsFromSwitches(String chromeSwitches) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("test-type");
        if (StringUtils.isNotEmpty(chromeSwitches)) {
            List<String> arguments = new OptionsSplitter().split(chromeSwitches);
            options.addArguments(arguments);
        }
        return options;
    }

    private void updateChromeBinaryIfSpecified(ChromeOptions options) {
        String chromeBinary = environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_CHROME_BINARY);
        if (StringUtils.isNotEmpty(chromeBinary)) {
            options.setBinary(chromeBinary);
        }
    }

}
