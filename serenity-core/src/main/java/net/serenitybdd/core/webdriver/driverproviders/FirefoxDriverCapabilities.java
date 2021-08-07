package net.serenitybdd.core.webdriver.driverproviders;

import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.webdriver.servicepools.DriverServiceExecutable;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.configuration.FilePathParser;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.BrowserPreferences;
import net.thucydides.core.webdriver.firefox.FirefoxProfileEnhancer;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_DRIVER_UNEXPECTED_ALERT_BEHAVIOUR;
import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_GECKO_DRIVER;

/**
 * Firefox profile preferences are configured in firefox.preferences.* variables
 *
 */
public class FirefoxDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;
    private final FirefoxProfileEnhancer firefoxProfileEnhancer;
    private final String options;
    private ProfilesIni allProfiles;

    public FirefoxDriverCapabilities(EnvironmentVariables environmentVariables){
        this.environmentVariables = environmentVariables;
        this.firefoxProfileEnhancer = new FirefoxProfileEnhancer(environmentVariables);
        this.options = "";
    }

    public FirefoxDriverCapabilities(EnvironmentVariables environmentVariables, String options){
        this.environmentVariables = environmentVariables;
        this.firefoxProfileEnhancer = new FirefoxProfileEnhancer(environmentVariables);
        this.options = options;
    }

    public DesiredCapabilities getCapabilities() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();

        Map<String, Object> firefoxCapabilities = BrowserPreferences.startingWith("firefox.").from(environmentVariables);

        //
        // Add preferences from firefox.preferences (but don't add them to the overall capabilities
        //
        firefoxCapabilities.remove("preferences");
        Map<String, Object> firefoxPreferences =  BrowserPreferences.startingWith("firefox.preferences.").from(environmentVariables);
        firefoxPreferences.forEach(
                (key, value) -> {
                    if (value instanceof String) {
                        String instantiatedValue = FilePathParser.forEnvironmentVariables(environmentVariables).getInstanciatedPath(value.toString());
                        firefoxOptions.addPreference(key, instantiatedValue);
                    } else if (value instanceof Boolean) {
                        firefoxOptions.addPreference(key, (Boolean) value);
                    } else if (value instanceof Integer) {
                        firefoxOptions.addPreference(key, (Integer) value);
                    }
                }
        );

        //
        // Arguments are defined in firefox.arguments
        //
        Object firefoxArguments = firefoxCapabilities.remove("arguments");
        if (firefoxArguments instanceof List) {
            List<String> argValues = ((List<?>) firefoxArguments).stream().map(value -> value.toString()).collect(Collectors.toList());
            firefoxOptions.addArguments(argValues);
        } else if (firefoxArguments != null) {
            firefoxOptions.addArguments(listOfArgumentsIn(firefoxArguments.toString()));
        }

        firefoxCapabilities.forEach(
                (key, value) -> {
                  if (!key.startsWith("preferences.")) {
                      firefoxOptions.setCapability(key, value);
                  }
                }
        );

        if (firefoxCapabilities.containsKey("logLevel")) {
            FirefoxDriverLogLevel logLevel = FirefoxDriverLogLevel.fromString(firefoxCapabilities.get("logLevel").toString());
            firefoxOptions.setLogLevel(logLevel);
        }

        if (firefoxCapabilities.containsKey("pageLoadStrategy")) {
            PageLoadStrategy loadStrategy = PageLoadStrategy.fromString(firefoxCapabilities.get("pageLoadStrategy").toString());
            firefoxOptions.setPageLoadStrategy(loadStrategy);
        }

        ConfiguredProxy.definedIn(environmentVariables).ifPresent(firefoxOptions::setProxy);

        if (firefoxCapabilities.containsKey("profile")) {
            String profilePath = FilePathParser.forEnvironmentVariables(environmentVariables).getInstanciatedPath(firefoxCapabilities.get("profile").toString());
            firefoxOptions.setProfile(new FirefoxProfile(new File(profilePath)));
        } else {
            firefoxOptions.setProfile(buildFirefoxProfile());
        }

        return new DesiredCapabilities(firefoxOptions);
    }

    private List<String> listOfArgumentsIn(String argumentsValue) {
        if (argumentsValue.trim().startsWith("[") && argumentsValue.trim().endsWith("]")) {
            argumentsValue = argumentsValue.substring(1, argumentsValue.lastIndexOf("]"));
        }
        return Splitter.on(",").trimResults().splitToList(argumentsValue);
    }

    private FirefoxProfile buildFirefoxProfile() {
        String profileName = ThucydidesSystemProperty.WEBDRIVER_FIREFOX_PROFILE.from(environmentVariables);
        FilePathParser parser = new FilePathParser(environmentVariables);
        DesiredCapabilities firefoxCapabilities = DesiredCapabilities.firefox();
        if (StringUtils.isNotEmpty(profileName)) {
            firefoxCapabilities.setCapability(FirefoxDriver.PROFILE, parser.getInstanciatedPath(profileName));
        }

        FirefoxProfile profile;
        if (profileName == null) {
            profile = createNewFirefoxProfile();
        } else {
            profile = getProfileFrom(profileName);
        }

        firefoxProfileEnhancer.allowWindowResizeFor(profile);
        if (shouldActivateProxy()) {
            activateProxyFor(profile, firefoxProfileEnhancer);
        }
        if (refuseUntrustedCertificates()) {
            profile.setAssumeUntrustedCertificateIssuer(false);
            profile.setAcceptUntrustedCertificates(false);
        } else {
            profile.setAssumeUntrustedCertificateIssuer(true);
            profile.setAcceptUntrustedCertificates(true);
        }
        firefoxProfileEnhancer.configureJavaSupport(profile);
        firefoxProfileEnhancer.addPreferences(profile);
        return profile;
    }

    private void activateProxyFor(FirefoxProfile profile, FirefoxProfileEnhancer firefoxProfileEnhancer) {
        String proxyUrl = getProxyUrlFromEnvironmentVariables();
        String proxyPort = getProxyPortFromEnvironmentVariables();
        firefoxProfileEnhancer.activateProxy(profile, proxyUrl, proxyPort);
    }

    private String getProxyPortFromEnvironmentVariables() {
        return ThucydidesSystemProperty.SERENITY_PROXY_HTTP_PORT.from(environmentVariables);
    }

    private boolean shouldActivateProxy() {
        String proxyUrl = getProxyUrlFromEnvironmentVariables();
        return StringUtils.isNotEmpty(proxyUrl);
    }

    private String getProxyUrlFromEnvironmentVariables() {
        return ThucydidesSystemProperty.SERENITY_PROXY_HTTP.from(environmentVariables);
    }

    private FirefoxProfile getProfileFrom(final String profileName) {
        FirefoxProfile profile = getAllProfiles().getProfile(profileName);
        if (profile == null) {
            profile = useExistingFirefoxProfile(new File(profileName));
        }
        return profile;
    }


    private ProfilesIni getAllProfiles() {
        if (allProfiles == null) {
            allProfiles = new ProfilesIni();
        }
        return allProfiles;
    }

    private FirefoxProfile createNewFirefoxProfile() {
        FirefoxProfile profile;
        if (Serenity.getFirefoxProfile() != null) {
            profile = Serenity.getFirefoxProfile();
        } else {
            profile = new FirefoxProfile();
            profile.setPreference("network.proxy.socks_port",9999);
            profile.setAlwaysLoadNoFocusLib(true);
        }
        return profile;
    }

    private FirefoxProfile useExistingFirefoxProfile(final File profileDirectory) {
        return new FirefoxProfile(profileDirectory);
    }

    private boolean refuseUntrustedCertificates() {
        return ThucydidesSystemProperty.REFUSE_UNTRUSTED_CERTIFICATES.booleanFrom(environmentVariables);
    }

}
