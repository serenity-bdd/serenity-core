package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.servicepools.DriverServiceExecutable;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.AddCustomCapabilities;
import net.thucydides.core.webdriver.capabilities.ChromePreferences;
import net.thucydides.core.webdriver.chrome.OptionsSplitter;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.thucydides.core.ThucydidesSystemProperty.*;

public class ChromeDriverCapabilities implements DriverCapabilitiesProvider {

    private final static List<String> AUTOMATION_OPTIONS = Arrays.asList("--enable-automation", "--test-type");
    private static final List<String> LIST_BASED_EXPERIMENTAL_OPTIONS = Arrays.asList(
            "args",
            "extensions",
            "excludeSwitches",
            "windowTypes"
    );
    private final EnvironmentVariables environmentVariables;
    private final String driverOptions;

    public ChromeDriverCapabilities(EnvironmentVariables environmentVariables, String driverOptions) {
        this.environmentVariables = environmentVariables;
        this.driverOptions = driverOptions;
    }

    @Override
    public DesiredCapabilities getCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities(configuredOptions());

        String chromeSwitches = ThucydidesSystemProperty.CHROME_SWITCHES.from(environmentVariables);
        capabilities.setCapability("chrome.switches", chromeSwitches);

        AddCustomCapabilities.startingWith("chrome.capabilities.").from(environmentVariables).to(capabilities);
        AddLoggingPreferences.from(environmentVariables).to(capabilities);
        SetProxyConfiguration.from(environmentVariables).in(capabilities);

        return capabilities;
    }

    public ChromeOptions configuredOptions() {
        ChromeOptions options = new ChromeOptions();

        /*
         * This is the only way to set the Chrome _browser_ binary.
         */
        if (WEBDRIVER_CHROME_BINARY.isDefinedIn(environmentVariables)) {
            options.setBinary(WEBDRIVER_CHROME_BINARY.from(environmentVariables));
        }
        addEnvironmentSwitchesTo(options);
        addRuntimeOptionsTo(options);
        addPreferencesTo(options);
        addExperimentalOptionsTo(options);
        updateChromeBinaryIfSpecified(options);
        addProxyConfigurationTo(options);
        addPageLoadStrategyTo(options);
        addExtensionsTo(options);
        addUnhandledPromptBehaviour(options);

        return options;
    }

    private void addExtensionsTo(ChromeOptions options) {
        CHROME_EXTENSION.optionalFrom(environmentVariables).ifPresent(
                extensionFile  -> options.addExtensions(new File(extensionFile))
        );
    }

    private void addUnhandledPromptBehaviour(ChromeOptions options) {
        String unexpectedAlertBehavior = SERENITY_DRIVER_UNEXPECTED_ALERT_BEHAVIOUR.from(environmentVariables);

        if (unexpectedAlertBehavior != null) {
            options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.fromString(unexpectedAlertBehavior));
        }
    }

    private void addPageLoadStrategyTo(ChromeOptions options) {
        String pageLoadStrategy = SERENITY_DRIVER_PAGE_LOAD_STRATEGY.from(environmentVariables);
        if (pageLoadStrategy != null) {
            options.setPageLoadStrategy(PageLoadStrategy.fromString(pageLoadStrategy));
        }
    }

    private void addProxyConfigurationTo(ChromeOptions options) {
        ConfiguredProxy.definedIn(environmentVariables).ifPresent(
                options::setProxy
        );
    }

    private void addEnvironmentSwitchesTo(ChromeOptions options) {

        String chromeSwitches = ThucydidesSystemProperty.CHROME_SWITCHES.from(environmentVariables);

        if (StringUtils.isNotEmpty(chromeSwitches)) {
            List<String> arguments = new OptionsSplitter().split(chromeSwitches);
            options.addArguments(arguments);
        }

        if (HEADLESS_MODE.isDefinedIn(environmentVariables) && HEADLESS_MODE.booleanFrom(environmentVariables, false)) {
            options.addArguments("--headless");
        }
    }

    private void addRuntimeOptionsTo(ChromeOptions options) {


        if (ThucydidesSystemProperty.USE_CHROME_AUTOMATION_OPTIONS.booleanFrom(environmentVariables, false)) {
            options.addArguments(AUTOMATION_OPTIONS);
        }

        if (StringUtils.isNotEmpty(driverOptions)) {
            List<String> arguments = new OptionsSplitter().split(driverOptions);
            options.addArguments(arguments);
        }


        options.setAcceptInsecureCerts(ACCEPT_INSECURE_CERTIFICATES.booleanFrom(environmentVariables, false));
    }

    private void addPreferencesTo(ChromeOptions options) {

        Map<String, Object> chromePreferences = ChromePreferences.startingWith("chrome_preferences.").from(environmentVariables);
        chromePreferences.putAll(ChromePreferences.startingWith("chrome.preferences.").from(environmentVariables));
        Map<String, Object> sanitizedChromePreferences = cleanUpPathsIn(chromePreferences);

        if (!chromePreferences.isEmpty()) {
            options.setExperimentalOption("prefs", sanitizedChromePreferences);
        }
    }

    private Map<String, Object> cleanUpPathsIn(Map<String, Object> chromePreferences) {
        Map<String, Object> preferences = new HashMap<>();
        chromePreferences.forEach(
                (key,value) -> preferences.put(key.toString(), SanitisedBrowserPreferenceValue.of(value))
        );
        return preferences;
    }


    private void addExperimentalOptionsTo(ChromeOptions options) {

        Map<String, Object> chromeExperimentalOptions = ChromePreferences.startingWith("chrome_experimental_options.")
                                                                         .from(environmentVariables);

        chromeExperimentalOptions.keySet().forEach(
                key -> {
                    Object value = chromeExperimentalOptions.get(key);
                    if( LIST_BASED_EXPERIMENTAL_OPTIONS.contains(key) ) {
                        List<String> arguments = new OptionsSplitter().split((String)value);
                        options.setExperimentalOption(key, arguments);
                    }else {
                        options.setExperimentalOption(key, value);
                    }
                }
        );
    }

    private void updateChromeBinaryIfSpecified(ChromeOptions options) {

        File executable = DriverServiceExecutable.called("chromedriver")
                .withSystemProperty(WEBDRIVER_CHROME_DRIVER.getPropertyName())
                .usingEnvironmentVariables(environmentVariables)
                .reportMissingBinary()
                .downloadableFrom("https://sites.google.com/a/chromium.org/chromedriver/downloads")
                .asAFile();

        if (executable != null && executable.exists()) {
            System.setProperty("webdriver.chrome.driver", executable.getAbsolutePath());
        }
    }

}
