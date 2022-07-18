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
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.*;

import static net.thucydides.core.ThucydidesSystemProperty.*;

public class ChromeDriverCapabilities implements DriverCapabilitiesProvider {

//    private final static List<String> AUTOMATION_OPTIONS = Arrays.asList("--enable-automation", "--test-type");
//    private static final List<String> LIST_BASED_EXPERIMENTAL_OPTIONS = Arrays.asList(
//            "args",
//            "extensions",
//            "excludeSwitches",
//            "windowTypes"
//    );
    private final EnvironmentVariables environmentVariables;
    private final String driverOptions;

    public ChromeDriverCapabilities(EnvironmentVariables environmentVariables, String driverOptions) {
        this.environmentVariables = environmentVariables;
        this.driverOptions = driverOptions;
    }

    @Override
    public MutableCapabilities getCapabilities() {
        MutableCapabilities capabilities = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        SetProxyConfiguration.from(environmentVariables).in(capabilities);
        return capabilities;
    }
}
