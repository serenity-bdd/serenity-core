package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.BrowserPreferences;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import net.thucydides.core.webdriver.chrome.OptionsSplitter;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;

import static net.thucydides.core.ThucydidesSystemProperty.*;

/**
 * Driver options are command line arguments for the browser:
 * They are passed into the ms:edgeOptions dictionary in the "args" property.
 */
public class EdgeDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;
    private final String driverOptions;

    public EdgeDriverCapabilities(EnvironmentVariables environmentVariables, String driverOptions) {
        this.environmentVariables = environmentVariables;
        this.driverOptions = driverOptions;
    }

    public DesiredCapabilities getCapabilities() {

        DesiredCapabilities edgeCaps = new DesiredCapabilities();
        edgeCaps.setCapability("ms:edgeChromium", true);

        Map<String, Object> edgeOptions = new HashMap<>();
        Map<String, Object> prefs = preferencesConfiguredIn(environmentVariables);
        if (!prefs.isEmpty()) {
            edgeOptions.put("prefs", prefs);
        }

        List<String> args = argsConfiguredIn(environmentVariables);
        args.addAll(DriverArgs.fromValue(driverOptions));
        if (!args.isEmpty()) {
            edgeOptions.put("args", args);
        }
        edgeCaps.setCapability("ms:edgeOptions", edgeOptions);

        DesiredCapabilities capabilities = new DesiredCapabilities(edgeCaps);
        capabilities.merge(W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver"));

        AddLoggingPreferences.from(environmentVariables).to(capabilities);
        SetProxyConfiguration.from(environmentVariables).in(capabilities);

        return capabilities;
    }

    public EdgeOptions configuredOptions() {
        EdgeOptions options = new EdgeOptions();
        addProxyConfigurationTo(options);
        addPreferencesTo(options);
        addPageLoadStrategyTo(options);
        addRuntimeOptionsTo(options);
        return options;
    }

    private void addPreferencesTo(EdgeOptions options) {
        preferencesConfiguredIn(environmentVariables).forEach(
                options::setCapability
        );
    }

    public static Map<String, Object> preferencesConfiguredIn(EnvironmentVariables environmentVariables) {
        return SanitisedBrowserPreferences.cleanUpPathsIn(
                BrowserPreferences.startingWith("edge.preferences.").from(environmentVariables)
        );
    }

    public static List<String> argsConfiguredIn(EnvironmentVariables environmentVariables) {
        List<String> args = DriverArgs.fromProperty("edge.args").configuredIn(environmentVariables);

        Optional<String> headless = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(HEADLESS_MODE);
        if (headless.isPresent() && Boolean.parseBoolean(headless.get())) {
            args.add("headless");
        }
        return args;

    }

    private void addProxyConfigurationTo(EdgeOptions options) {
        ConfiguredProxy.definedIn(environmentVariables).ifPresent(options::setProxy);
    }

    private void addPageLoadStrategyTo(EdgeOptions options) {
        String pageLoadStrategyValue = SERENITY_DRIVER_PAGE_LOAD_STRATEGY.from(environmentVariables);
        if (pageLoadStrategyValue != null) {
            PageLoadStrategy pageLoadStrategy = PageLoadStrategy.valueOf(pageLoadStrategyValue.toUpperCase());
            options.setPageLoadStrategy(pageLoadStrategy);
        }
    }


    private void addRuntimeOptionsTo(EdgeOptions options) {
        if (StringUtils.isNotEmpty(driverOptions)) {
            List<String> arguments = new ArrayList<>(new OptionsSplitter().split(driverOptions));
            options.addArguments(arguments);
        }

        options.setAcceptInsecureCerts(ACCEPT_INSECURE_CERTIFICATES.booleanFrom(environmentVariables, false));
    }

}
