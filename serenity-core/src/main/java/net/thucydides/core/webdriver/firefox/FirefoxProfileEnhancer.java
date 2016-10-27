package net.thucydides.core.webdriver.firefox;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FirefoxProfileEnhancer {

    private static final String FIREBUGS_VERSION = "2.0.18";
    private static final String MAX_FIREBUGS_VERSION = "999.99.0";
    private static final String FIREBUGS_XPI_FILE = "/firefox/firebug-" + FIREBUGS_VERSION + ".xpi";

    private static final String FIREFINDER_VERSION = "1.4-fx";
    private static final String MAX_FIREFINDER_VERSION = "999.9";
    private static final String FIREFINDER_XPI_FILE = "/firefox/firefinder_for_firebug-" + FIREFINDER_VERSION + ".xpi";

    private static final Logger LOGGER = LoggerFactory.getLogger(FirefoxProfileEnhancer.class);
    private static final String FIREFOX_NETWORK_PROXY_TYPE = "network.proxy.type";
    private static final String FIREFOX_NETWORK_PROXY_HTTP = "network.proxy.http";
    private static final String FIREFOX_NETWORK_PROXY_HTTP_PORT = "network.proxy.http_port";
    private final EnvironmentVariables environmentVariables;

    public FirefoxProfileEnhancer(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public boolean shouldActivateFirebugs() {
        return ThucydidesSystemProperty.THUCYDIDES_ACTIVATE_FIREBUGS.booleanFrom(environmentVariables);
    }

    public void addFirebugsTo(final FirefoxProfile profile) {
//        try {
//  TODO: Make this work for recent versions of FireFox. It fails due to an apparent bug in Firefox
//            profile.addExtension(this.getClass(), FIREBUGS_XPI_FILE);
//            profile.setPreference("extensions.firebug.currentVersion", MAX_FIREBUGS_VERSION); // Avoid startup screen
//
//            profile.addExtension(this.getClass(), FIREFINDER_XPI_FILE);
//            profile.setPreference("extensions.firebug.currentVersion", MAX_FIREFINDER_VERSION); // Avoid startup screen
//
//        } catch (IOException e) {
//            LOGGER.warn("Failed to add Firebugs extension to Firefox");
//        }
    }

    public void configureJavaSupport(FirefoxProfile profile) {
        boolean enableJava = environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.SECURITY_ENABLE_JAVA, false);
        profile.setPreference("security.enable_java", enableJava);
    }

    public void allowWindowResizeFor(final FirefoxProfile profile) {
        profile.setPreference("dom.disable_window_move_resize",false);
    }

    public void activateProxy(final FirefoxProfile profile, String proxyUrl, String proxyPort) {
        profile.setPreference(FIREFOX_NETWORK_PROXY_HTTP, proxyUrl);
        profile.setPreference(FIREFOX_NETWORK_PROXY_HTTP_PORT, NumberUtils.toInt(proxyPort));
        profile.setPreference(FIREFOX_NETWORK_PROXY_TYPE, 1);
    }

    public void activateNativeEventsFor(FirefoxProfile profile, boolean enabled) {
        profile.setEnableNativeEvents(enabled);
    }

    static class PreferenceValue {
        private final String key;
        private final Object value;

        PreferenceValue(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public void applyTo(FirefoxProfile profile) {
            if (value instanceof Boolean) {
                profile.setPreference(key, (Boolean) value);
            } else if (value instanceof Integer) {
                profile.setPreference(key, (Integer) value);
            } else {
                profile.setPreference(key, value.toString());
            }
        }
    }
    public void addPreferences(FirefoxProfile profile) {
        String preferences = environmentVariables.getProperty(ThucydidesSystemProperty.FIREFOX_PREFERENCES);
        List<PreferenceValue> preferenceValues = getPreferenceValuesFrom(preferences);
        for (PreferenceValue preference : preferenceValues) {
            preference.applyTo(profile);
        }
    }

    private List<PreferenceValue> getPreferenceValuesFrom(String preferences) {
        List<PreferenceValue> preferenceValues = Lists.newArrayList();
        if (StringUtils.isNotEmpty(preferences)) {
            List<String> arguments = split(preferences, ";");
            for(String argument : arguments) {
                preferenceValues.addAll(convertToPreferenceValue(argument).asSet());
            }
        }
        return preferenceValues;
    }

    private Optional<PreferenceValue> convertToPreferenceValue(String argument) {
        List<String> arguments = split(argument, "=");
        if (arguments.size() == 1) {
            String key = arguments.get(0);
            return Optional.of(new PreferenceValue(key,Boolean.TRUE));
        } else if (arguments.size() == 2) {
            String key = arguments.get(0);
            String value = arguments.get(1);
            return Optional.of(new PreferenceValue(key,argumentValueOf(value)));
        } else {
            return Optional.absent();
        }
    }

    private Object argumentValueOf(String value) {
        if (NumberUtils.isDigits(value)) {
            return Integer.parseInt(value);
        } else if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
            return Boolean.valueOf(value);
        } else {
            return value;
        }
    }

    private ArrayList<String> split(String values, String separator) {
        return Lists.newArrayList(Splitter.on(separator).trimResults().split(values));
    }
}
