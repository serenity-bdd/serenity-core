package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.AddCustomCapabilities;
import net.thucydides.core.webdriver.capabilities.ChromePreferences;
import net.thucydides.core.webdriver.chrome.OptionsSplitter;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.Map;

import static net.thucydides.core.ThucydidesSystemProperty.ACCEPT_INSECURE_CERTIFICATES;
import static net.thucydides.core.ThucydidesSystemProperty.HEADLESS_MODE;

public class ChromeDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;
    private final String driverOptions;

    public ChromeDriverCapabilities(EnvironmentVariables environmentVariables, String driverOptions) {
        this.environmentVariables = environmentVariables;
        this.driverOptions = driverOptions;
    }

    public DesiredCapabilities getCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();

        ChromeOptions chromeOptions = configuredOptions();

        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        String chromeSwitches = environmentVariables.getProperty(ThucydidesSystemProperty.CHROME_SWITCHES);
        capabilities.setCapability("chrome.switches", chromeSwitches);

        AddCustomCapabilities.startingWith("chrome.capabilities.").from(environmentVariables).to(capabilities);

        SetProxyConfiguration.from(environmentVariables).in(capabilities);

        return capabilities;
    }

    private ChromeOptions configuredOptions() {
        ChromeOptions options = new ChromeOptions();

        addEnvironmentSwitchesTo(options);
        addRuntimeOptionsTo(options);
        addPreferencesTo(options);
        updateChromeBinaryIfSpecified(options);

        return options;
    }

    private void addEnvironmentSwitchesTo(ChromeOptions options) {

        String chromeSwitches = environmentVariables.getProperty(ThucydidesSystemProperty.CHROME_SWITCHES);

        options.addArguments("test-type");
        if (StringUtils.isNotEmpty(chromeSwitches)) {
            List<String> arguments = new OptionsSplitter().split(chromeSwitches);
            options.addArguments(arguments);
        }

        if (HEADLESS_MODE.isDefinedIn(environmentVariables) && HEADLESS_MODE.booleanFrom(environmentVariables, false)) {
            options.addArguments("--headless");
        }
    }

    private void addRuntimeOptionsTo(ChromeOptions options) {

        options.addArguments("test-type");
        if (StringUtils.isNotEmpty(driverOptions)) {
            List<String> arguments = new OptionsSplitter().split(driverOptions);
            options.addArguments(arguments);
        }

        options.setAcceptInsecureCerts(ACCEPT_INSECURE_CERTIFICATES.booleanFrom(environmentVariables,false));
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
