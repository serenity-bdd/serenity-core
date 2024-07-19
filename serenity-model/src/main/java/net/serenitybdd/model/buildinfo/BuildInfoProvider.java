package net.serenitybdd.model.buildinfo;

import com.google.common.base.Splitter;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import net.serenitybdd.model.di.ModelInfrastructure;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by john on 12/02/15.
 */
public class BuildInfoProvider {
    private final EnvironmentVariables environmentVariables;
    private final DriverCapabilityRecord driverCapabilityRecord;
    private final Map<String, Map<String, String>> sections;

    private final static List<String> MASKABLE = Arrays.asList("accessKey", "key");

    private static final Logger LOGGER = LoggerFactory.getLogger(BuildInfoProvider.class);

    public BuildInfoProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.driverCapabilityRecord = ModelInfrastructure.getDriverCapabilityRecord();
        this.sections = new HashMap<>();
    }

    public BuildProperties getBuildProperties() {
        Map<String, String> generalProperties = new HashMap<>();
        generalProperties.put("Default Driver", ThucydidesSystemProperty.DRIVER.from(environmentVariables, "firefox"));
        generalProperties.put("Operating System", System.getProperty("os.name") + " version " + System.getProperty("os.version"));
//        addGroupPropertiesTo("webdriver",generalProperties);
        addGroupPropertiesTo("saucelabs",generalProperties);
        addGroupPropertiesTo("browserstack",generalProperties);
        driverCapabilityRecord.getDrivers().forEach(
                driver -> generalProperties.put("Driver " + driver,
                                                driverCapabilityRecord.getDriverCapabilities().get(driver).toString())
        );

        //
        // Programmatically-added build info
        //
        BuildInfo.load();
        BuildInfo.getSections().forEach(
                section -> sections.put(section, BuildInfo.getSection(section))
        );

        addCustomPropertiesTo(generalProperties);

        Map<String, Properties> driverPropertiesMap = driverCapabilityRecord.getDriverCapabilities();
        return new BuildProperties(generalProperties, driverCapabilityRecord.getDrivers(), driverPropertiesMap, sections);
    }

    private void addGroupPropertiesTo(String groupName, Map<String, String> buildProperties) {
        if (EnvironmentSpecificConfiguration.from(environmentVariables).propertyGroupIsDefinedFor(groupName)) {
            EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix(groupName).forEach(
                    (key, value) -> {
                        String ungroupedKey = key.toString().replace(groupName+".","");
                        String sanitizedValue = sanitized(ungroupedKey, value.toString());
                        buildProperties.put(StringUtils.capitalize(groupName) + " Property: " + ungroupedKey,
                                            sanitized(key.toString(), sanitizedValue));
                    }
            );
        }
    }

    private String sanitized(String property, String value) {
        boolean isMaskable = MASKABLE.stream().anyMatch(
                maskableKey -> property.contains(maskableKey)
        );
        if (isMaskable) {
            return (value.contains("@") && value.contains(":")) ? maskAPIKey(value) : maskedKey(value);
        }
        return (isMaskable) ? maskAPIKey(value) : value;
    }
    /**
     * Mask API key in URL if present
     */
    private String maskAPIKey(String url) {
        if (url == null) {
            return "";
        }
        if (url.contains("@")) {
            int apiKeyStart = url.indexOf(":");
            int apiKeyEnd = url.indexOf("@");
            return url.substring(0, apiKeyStart + 3) + "XXXXXXXXXXXXXXXX" + url.substring(apiKeyEnd);
        } else {
            return url;
        }
    }

    private void addCustomPropertiesTo(Map<String, String> buildProperties) {

        Properties sysInfoProperties = EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix("sysinfo");

        sysInfoProperties.forEach(
                (key, value) -> {
                    String simplifiedKey = key.toString().replace("sysinfo.", "");
                    String expression = value.toString();
                    String resolvedValue = (isGroovyExpression(expression)) ? evaluateGroovyExpression(key.toString(), expression) : expression;

                    if (isInASection(simplifiedKey)) {
                        String sectionKey = humanizedFormOf(sectionKeyFrom(simplifiedKey));
                        String fieldKey = humanizedFormOf(sectionLabelFrom(simplifiedKey));
                        Map<String, String> sectionValues = sections.getOrDefault(sectionKey, new HashMap<>());
                        sectionValues.put(fieldKey, resolvedValue);
                        sections.put(sectionKey, sectionValues);
                    } else {
                        buildProperties.put(humanizedFormOf(simplifiedKey), resolvedValue);
                    }
                }
        );

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
                StringUtils.replace(simplifiedKey, ".", " ")
                        .replace("_", " ")), "\"");
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
            String error = String.format("Failed to evaluate build info expression '%s' for key '%s'", expression, key);
            LOGGER.warn(error);
        }
        return (result != null) ? result.toString() : expression;
    }

    private String maskedKey(String value) {
        return value.substring(0,3) + "XXXXXXXXXXXXXXXX" + value.substring(value.length() - 3);
    }
}
