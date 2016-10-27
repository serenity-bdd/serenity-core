package net.serenitybdd.core.webdriver.driverproviders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.steps.FilePathParser;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.firefox.FirefoxProfileEnhancer;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FirefoxDriverCapabilities  implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;
    private final FirefoxProfileEnhancer firefoxProfileEnhancer;
    private ProfilesIni allProfiles;

    public FirefoxDriverCapabilities(EnvironmentVariables environmentVariables){
        this.environmentVariables = environmentVariables;
        this.firefoxProfileEnhancer = new FirefoxProfileEnhancer(environmentVariables);
    }

    public DesiredCapabilities getCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("firefox_profile",buildFirefoxProfile());
        if (ThucydidesSystemProperty.GECKO_FIREFOX_OPTIONS.isDefinedIn(environmentVariables)) {
            String firefoxOptionsInJsonFormat = ThucydidesSystemProperty.GECKO_FIREFOX_OPTIONS.from(environmentVariables);
            Map<String, Object> firefoxOptions = new Gson().fromJson(firefoxOptionsInJsonFormat, new TypeToken<HashMap<String, Object>>() {}.getType());
            capabilities.setCapability("moz:firefoxOptions", firefoxOptions);
        }
        return capabilities;
    }

    protected FirefoxProfile buildFirefoxProfile() {
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
        firefoxProfileEnhancer.activateNativeEventsFor(profile, shouldEnableNativeEvents());
        if (shouldActivateProxy()) {
            activateProxyFor(profile, firefoxProfileEnhancer);
        }
        if (firefoxProfileEnhancer.shouldActivateFirebugs()) {
            firefoxProfileEnhancer.addFirebugsTo(profile);
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


    private boolean shouldEnableNativeEvents() {
        return Boolean.valueOf(ThucydidesSystemProperty.THUCYDIDES_NATIVE_EVENTS.from(environmentVariables,"true"));
    }

    private void activateProxyFor(FirefoxProfile profile, FirefoxProfileEnhancer firefoxProfileEnhancer) {
        String proxyUrl = getProxyUrlFromEnvironmentVariables();
        String proxyPort = getProxyPortFromEnvironmentVariables();
        firefoxProfileEnhancer.activateProxy(profile, proxyUrl, proxyPort);
    }

    private String getProxyPortFromEnvironmentVariables() {
        return ThucydidesSystemProperty.THUCYDIDES_PROXY_HTTP_PORT.from(environmentVariables);
    }

    private boolean shouldActivateProxy() {
        String proxyUrl = getProxyUrlFromEnvironmentVariables();
        return StringUtils.isNotEmpty(proxyUrl);
    }

    private String getProxyUrlFromEnvironmentVariables() {
        return ThucydidesSystemProperty.THUCYDIDES_PROXY_HTTP.from(environmentVariables);
    }

    private FirefoxProfile getProfileFrom(final String profileName) {
        FirefoxProfile profile = getAllProfiles().getProfile(profileName);
        if (profile == null) {
            profile = useExistingFirefoxProfile(new File(profileName));
        }
        return profile;
    }


    protected ProfilesIni getAllProfiles() {
        if (allProfiles == null) {
            allProfiles = new ProfilesIni();
        }
        return allProfiles;
    }

    protected FirefoxProfile createNewFirefoxProfile() {
        FirefoxProfile profile;
        if (Serenity.getFirefoxProfile() != null) {
            profile = Serenity.getFirefoxProfile();
        } else {
            profile = new FirefoxProfile();
            profile.setPreference("network.proxy.socks_port",9999);
            profile.setAlwaysLoadNoFocusLib(true);
            profile.setEnableNativeEvents(true);
        }
        return profile;
    }

    protected FirefoxProfile useExistingFirefoxProfile(final File profileDirectory) {
        return new FirefoxProfile(profileDirectory);
    }

    private boolean refuseUntrustedCertificates() {
        return ThucydidesSystemProperty.REFUSE_UNTRUSTED_CERTIFICATES.booleanFrom(environmentVariables);
    }

}
