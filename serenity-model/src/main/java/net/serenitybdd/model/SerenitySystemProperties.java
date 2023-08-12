package net.serenitybdd.model;

import net.thucydides.model.ThucydidesSystemProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * Convenience class used to get and set Serenity system properties.
 */
public class SerenitySystemProperties {

    static SerenitySystemProperties currentSystemProperties = new SerenitySystemProperties();

    public static SerenitySystemProperties getProperties() {
        return currentSystemProperties;
    }

    public String getValue(ThucydidesSystemProperty property) {
        return System.getProperty(property.getPropertyName());
    }

    /**
     * @return True if a Serenity system property has been set.
     */
    public boolean isDefined(final ThucydidesSystemProperty property) {
        return (System.getProperty(property.getPropertyName()) != null);
    }

    public String getValue(final ThucydidesSystemProperty property, final String defaultValue) {
        return isDefined(property) ? getValue(property) : defaultValue;
    }

    /**
     * @return True if a given Serenity system property has been set to a non-empty value.
     */
    public boolean isEmpty(final ThucydidesSystemProperty property) {
        String value = System.getProperty(property.getPropertyName());
        return (StringUtils.isEmpty(value));
    }

    /**
     * Sets a Serenity system property to s specified value.
     * @param property the name of the property
     * @param value the property value
     */
    public void setValue(final ThucydidesSystemProperty property, final String value) {
        System.setProperty(property.getPropertyName(), value);
    }

    public Integer getIntegerValue(ThucydidesSystemProperty property, Integer defaultValue) {
        String value = System.getProperty(property.getPropertyName());
        if (value != null) {
            return Integer.valueOf(value);
        } else {
            return defaultValue;
        }
    }

    public Boolean getBooleanValue(ThucydidesSystemProperty property, boolean defaultValue) {
        String value = System.getProperty(property.getPropertyName());
        if (value != null) {
            return Boolean.valueOf(value);
        } else {
            return defaultValue;
        }
    }
}
