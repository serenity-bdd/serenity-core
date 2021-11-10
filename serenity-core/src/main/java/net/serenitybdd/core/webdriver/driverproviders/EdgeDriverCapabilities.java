package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.BrowserPreferences;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.thucydides.core.ThucydidesSystemProperty.HEADLESS_MODE;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_DRIVER_PAGE_LOAD_STRATEGY;

public class EdgeDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;

    public EdgeDriverCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public DesiredCapabilities getCapabilities() {

        DesiredCapabilities edgeCaps = new DesiredCapabilities();
        edgeCaps.setCapability("ms:edgeChrominum", true);

        Map<String, Object> edgeOptions = new HashMap<>();
        edgeOptions.put("prefs", preferencesConfiguredIn(environmentVariables));

        List<String> args = argsConfiguredIn(environmentVariables);
        if ((args != null) && (!args.isEmpty())) {
            edgeOptions.put("args", argsConfiguredIn(environmentVariables));
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

}
