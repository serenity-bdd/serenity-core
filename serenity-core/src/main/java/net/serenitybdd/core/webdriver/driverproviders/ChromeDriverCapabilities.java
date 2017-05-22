package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.AddCustomCapabilities;
import net.thucydides.core.webdriver.capabilities.ChromePreferences;
import net.thucydides.core.webdriver.chrome.OptionsSplitter;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChromeDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;

    public ChromeDriverCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public DesiredCapabilities getCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();

        ChromeOptions chromeOptions = configuredOptions();

        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        String chromeSwitches = environmentVariables.getProperty(ThucydidesSystemProperty.CHROME_SWITCHES);
        capabilities.setCapability("chrome.switches", chromeSwitches);

        AddCustomCapabilities.startingWith("chrome.capabilities.").from(environmentVariables).to(capabilities);

        return capabilities;
    }

    private ChromeOptions configuredOptions() {
        ChromeOptions options = new ChromeOptions();

        addSwitchesTo(options);
        addPreferencesTo(options);
        updateChromeBinaryIfSpecified(options);

        return options;
    }

    private void addSwitchesTo(ChromeOptions options) {

        String chromeSwitches = environmentVariables.getProperty(ThucydidesSystemProperty.CHROME_SWITCHES);

        options.addArguments("test-type");
        if (StringUtils.isNotEmpty(chromeSwitches)) {
            List<String> arguments = new OptionsSplitter().split(chromeSwitches);
            options.addArguments(arguments);
        }
    }

    private void addPreferencesTo(ChromeOptions options) {

        Map<String,Object> chromePreferences =  ChromePreferences.startingWith("chrome_preferences.").from(environmentVariables);
        if (!chromePreferences.isEmpty()) {
            options.setExperimentalOption("prefs", chromePreferences);
        }

    }

    private void updateChromeBinaryIfSpecified(ChromeOptions options) {
        String chromeBinary = environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_CHROME_BINARY);

        if (StringUtils.isNotEmpty(chromeBinary)) {
            options.setBinary(chromeBinary);
        }
    }

}
