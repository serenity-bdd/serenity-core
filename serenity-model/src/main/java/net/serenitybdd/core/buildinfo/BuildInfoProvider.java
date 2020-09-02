package net.serenitybdd.core.buildinfo;

import com.google.common.base.Splitter;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by john on 12/02/15.
 */
public class BuildInfoProvider {
    private final EnvironmentVariables environmentVariables;
    private final DriverCapabilityRecord driverCapabilityRecord;
    private final Map<String, Map<String, String>> sections;

    private static final Logger LOGGER = LoggerFactory.getLogger(BuildInfoProvider.class);

    public BuildInfoProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.driverCapabilityRecord = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);
        this.sections = new HashMap<>();
    }

    public BuildProperties getBuildProperties() {
        Map<String, String> generalProperties = new HashMap<>();
        generalProperties.put("Default Driver", ThucydidesSystemProperty.DRIVER.from(environmentVariables,"firefox"));
        generalProperties.put("Operating System",System.getProperty("os.name") + " version " + System.getProperty("os.version"));
        addRemoteDriverPropertiesTo(generalProperties);
        addSaucelabsPropertiesTo(generalProperties);
        addCustomPropertiesTo(generalProperties);

        Map<String, Properties> driverPropertiesMap = driverCapabilityRecord.getDriverCapabilities();
        return new BuildProperties(generalProperties, driverCapabilityRecord.getDrivers(), driverPropertiesMap, sections);
    }

    private void addRemoteDriverPropertiesTo(Map<String, String> buildProperties) {
        if (ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.isDefinedIn(environmentVariables)) {
            buildProperties.put("Remote driver", ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.from(environmentVariables));
            if (ThucydidesSystemProperty.WEBDRIVER_REMOTE_BROWSER_VERSION.from(environmentVariables) != null) {
                buildProperties.put("Remote browser version", ThucydidesSystemProperty.WEBDRIVER_REMOTE_BROWSER_VERSION.from(environmentVariables));
            }
            if (ThucydidesSystemProperty.WEBDRIVER_REMOTE_OS.from(environmentVariables) != null) {
                buildProperties.put("Remote OS", ThucydidesSystemProperty.WEBDRIVER_REMOTE_OS.from(environmentVariables));
            }
        }
    }

    private void addSaucelabsPropertiesTo(Map<String, String> buildProperties) {
        if (ThucydidesSystemProperty.SAUCELABS_URL.isDefinedIn(environmentVariables)) {
            buildProperties.put("Saucelabs URL", maskAPIKey(ThucydidesSystemProperty.SAUCELABS_URL.from(environmentVariables)));
            if (ThucydidesSystemProperty.SAUCELABS_USER_ID.from(environmentVariables) != null) {
                buildProperties.put("Saucelabs user", ThucydidesSystemProperty.SAUCELABS_USER_ID.from(environmentVariables));
            }
            if (ThucydidesSystemProperty.SAUCELABS_TARGET_PLATFORM.from(environmentVariables) != null) {
                buildProperties.put("Saucelabs target platform", ThucydidesSystemProperty.SAUCELABS_TARGET_PLATFORM.from(environmentVariables));
            }
            if (ThucydidesSystemProperty.SAUCELABS_BROWSER_VERSION.from(environmentVariables) != null) {
                buildProperties.put("Saucelabs browser version", ThucydidesSystemProperty.SAUCELABS_BROWSER_VERSION.from(environmentVariables));
            }
            if (ThucydidesSystemProperty.WEBDRIVER_REMOTE_OS.from(environmentVariables) != null) {
                buildProperties.put("Remote OS", ThucydidesSystemProperty.WEBDRIVER_REMOTE_OS.from(environmentVariables));
            }
        }
    }

    private String maskAPIKey(String url) {
        int apiKeyStart = url.indexOf(":");
        int apiKeyEnd = url.indexOf("@");
        return url.substring(0,apiKeyStart + 3) + "XXXXXXXXXXXXXXXX" + url.substring(apiKeyEnd);
    }


    private void addCustomPropertiesTo(Map<String, String> buildProperties) {

        List<String> sysInfoKeys = environmentVariables.getKeys().stream()
                                                        .filter( key -> key.startsWith("sysinfo."))
                                                        .collect(Collectors.toList());
        for(String key : sysInfoKeys) {
            String simplifiedKey = key.replace("sysinfo.", "");

            String expression = EnvironmentSpecificConfiguration.from(environmentVariables)
                    .getOptionalProperty(key)
                    .orElse(null);

            String value = (isGroovyExpression(expression)) ? evaluateGroovyExpression(key, expression) : expression;

            if (isInASection(simplifiedKey)) {
                String sectionKey = humanizedFormOf(sectionKeyFrom(simplifiedKey));
                String fieldKey = humanizedFormOf(sectionLabelFrom(simplifiedKey));
                Map<String, String> sectionValues = sections.getOrDefault(sectionKey, new HashMap<>());
                sectionValues.put(fieldKey, value);
                sections.put(sectionKey, sectionValues);
            } else {
                buildProperties.put(humanizedFormOf(simplifiedKey), value);
            }
        }

    }

    private String sectionKeyFrom(String simplifiedKey) {
        return Splitter.on(".").splitToList(simplifiedKey).get(0);
    }
    private String sectionLabelFrom(String simplifiedKey) {
        return Splitter.on(".").splitToList(simplifiedKey).get(1);
    }

    private boolean isInASection(String simplifiedKey) {
        return simplifiedKey.contains(".");
    }

    private boolean isGroovyExpression(String expression) {
        return expression.startsWith("${") && expression.endsWith("}");
    }

    private String humanizedFormOf(String simplifiedKey) {
        return StringUtils.strip(StringUtils.capitalize(
                StringUtils.replace(simplifiedKey,"."," ")
                           .replace("_"," ")),"\"");
    }

    private Binding binding;
    private Binding groovyBinding() {
        binding = new Binding();
        binding.setVariable("env", environmentVariables.asMap());
        return binding;
    }

    private String evaluateGroovyExpression(String key, String expression) {
        GroovyShell shell = new GroovyShell(groovyBinding());
        Object result = null;
        try {
            String groovy = expression.substring(2, expression.length() - 1);
            if (StringUtils.isNotEmpty(groovy)) {
                result = shell.evaluate(groovy);
            }
        } catch (GroovyRuntimeException e) {
            String error = String.format("Failed to evaluate build info expression '%s' for key '%s'",expression, key);
            LOGGER.warn(error);
        }
        return (result != null) ? result.toString() : expression;
    }
}
