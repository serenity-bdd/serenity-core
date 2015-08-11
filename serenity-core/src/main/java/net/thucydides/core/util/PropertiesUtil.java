package net.thucydides.core.util;

import java.util.Properties;

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
}
