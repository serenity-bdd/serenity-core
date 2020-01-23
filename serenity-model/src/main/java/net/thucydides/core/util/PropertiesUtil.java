package net.thucydides.core.util;

import org.apache.commons.text.StrSubstitutor;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtil {
    public static Properties copyOf(Properties sourceProperties) {
        Properties copiedProperties = new Properties();
        for(String propertyName : sourceProperties.stringPropertyNames()) {
            if (sourceProperties.getProperty(propertyName) != null) {
                copiedProperties.setProperty(propertyName, sourceProperties.getProperty(propertyName));
            }
        }
        return copiedProperties;
    }

    public static void expandPropertyAndEnvironmentReferences(Map<String, String> runnerEnvironmentVariables,Properties properties) {
        Set<String> names = properties.stringPropertyNames();
        for (String name : names) {
            String value = properties.getProperty(name);
            // Replace System Property References ${sys.property.any}
            String expandedValue = StrSubstitutor.replaceSystemProperties(value);
            // Replace Environment  References ${A_DEFINED_ENVIRONMENT_VARIABLE}
            expandedValue = StrSubstitutor.replace(expandedValue, runnerEnvironmentVariables);
            properties.setProperty(name, expandedValue);
        }
    }
}
