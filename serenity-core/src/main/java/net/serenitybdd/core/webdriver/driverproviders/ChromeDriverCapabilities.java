package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.servicepools.DriverServiceExecutable;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.configuration.FilePathParser;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.AddCustomCapabilities;
import net.thucydides.core.webdriver.capabilities.BrowserPreferences;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import net.thucydides.core.webdriver.chrome.OptionsSplitter;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.*;

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
        capabilities.merge(W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver"));
//        String switches = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty(CHROME_SWITCHES);
//        capabilities.setCapability("chrome.switches", switches);

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
        String chromeBinary =
                EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(CHROME_BINARY)
                .orElse(EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(WEBDRIVER_CHROME_BINARY).orElse(null));

        if (chromeBinary != null) {
            String instantiatedBinaryPath = FilePathParser.forEnvironmentVariables(environmentVariables).getInstanciatedPath(chromeBinary);
            options.setBinary(instantiatedBinaryPath);
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

        List<String> arguments = DriverArgs.fromProperty(ThucydidesSystemProperty.CHROME_SWITCHES).configuredIn(environmentVariables);

        if (!arguments.isEmpty()) {
            options.addArguments(arguments);
        }

        Optional<String> headless = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(HEADLESS_MODE);
        if (headless.isPresent() && Boolean.parseBoolean(headless.get())) {
            options.addArguments("--headless");
        }
    }

    private void addRuntimeOptionsTo(ChromeOptions options) {

        if (ThucydidesSystemProperty.USE_CHROME_AUTOMATION_OPTIONS.booleanFrom(environmentVariables, false)) {
            options.addArguments(AUTOMATION_OPTIONS);
        }

        if (StringUtils.isNotEmpty(driverOptions)) {
            List<String> arguments = new ArrayList<>(new OptionsSplitter().split(driverOptions));
            options.addArguments(arguments);
        }


        options.setAcceptInsecureCerts(ACCEPT_INSECURE_CERTIFICATES.booleanFrom(environmentVariables, false));
    }

    public static Map<String, Object> preferencesConfiguredIn(EnvironmentVariables environmentVariables) {
        Map<String, Object> chromePreferences = BrowserPreferences.startingWith("chrome_preferences.").from(environmentVariables);
        chromePreferences.putAll(BrowserPreferences.startingWith("chrome.preferences.").from(environmentVariables));
        chromePreferences.putAll(BrowserPreferences.startingWith("chrome.options.").from(environmentVariables));
        return SanitisedBrowserPreferences.cleanUpPathsIn(chromePreferences);
    }

    private void addPreferencesTo(ChromeOptions options) {
        Map<String, Object> chromePreferences = preferencesConfiguredIn(environmentVariables);
        if (!chromePreferences.isEmpty()) {
            options.setExperimentalOption("prefs", chromePreferences);
        }
    }

    private void addExperimentalOptionsTo(ChromeOptions options) {

        Map<String, Object> chromeExperimentalOptions = BrowserPreferences.startingWith("chrome_experimental_options.")
                                                                         .from(environmentVariables);

        chromeExperimentalOptions.putAll(BrowserPreferences.startingWith("chrome.experimental_options.")
                                                           .from(environmentVariables));
        chromeExperimentalOptions.keySet().forEach(
                key -> {
                    Object value = chromeExperimentalOptions.get(key);
                    if( LIST_BASED_EXPERIMENTAL_OPTIONS.contains(key) ) {
                        List<String> arguments = new ArrayList<>(new OptionsSplitter().split((String)value));
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
