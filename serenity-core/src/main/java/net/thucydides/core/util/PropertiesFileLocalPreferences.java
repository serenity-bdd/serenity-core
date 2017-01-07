package net.thucydides.core.util;

import com.google.inject.Inject;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.strip;

/**
 * Loads Thucydides preferences from a local file called thucydides.properties.
 * Thucydides options can be loaded from the thucydides.properties file in the home directory, in the working directory,
 * or on the classpath. There can be multiple thucydides.properties files, in which case values from the file in the
 * working directory override values on the classpath, and values in the home directory override values in the working
 * directory. Values can always be overridden on the command line.
 */
public class PropertiesFileLocalPreferences implements LocalPreferences {

    public static final String TYPESAFE_CONFIG_FILE = "serenity.conf";
    private File workingDirectory;
    private File homeDirectory;
    private File mavenModuleDirectory;
    private final EnvironmentVariables environmentVariables;
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesFileLocalPreferences.class);

    @Inject
    public PropertiesFileLocalPreferences(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.homeDirectory = new File(System.getProperty("user.home"));
        this.workingDirectory = new File(System.getProperty("user.dir"));
        final String mavenBuildDir = System.getProperty(SystemPropertiesConfiguration.PROJECT_BUILD_DIRECTORY);
        if (!StringUtils.isEmpty(mavenBuildDir)) {
            this.mavenModuleDirectory = new File(mavenBuildDir);
        } else {
            this.mavenModuleDirectory = this.workingDirectory;
        }
    }

    public File getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(File homeDirectory) {
        this.homeDirectory = homeDirectory;
    }

    public void loadPreferences() throws IOException {

        updatePreferencesFrom(
                typesafeConfigPreferences(),
                preferencesIn(preferencesFileInHomeDirectory()),
                preferencesIn(legacyPreferencesFileInHomeDirectory()),
                preferencesIn(preferencesFileInMavenModuleDirectory()),
                preferencesIn(preferencesFileInMavenParentModuleDirectory()),
                preferencesIn(preferencesFileInWorkingDirectory()),
                preferencesIn(legacyPreferencesFileInWorkingDirectory()),
                preferencesIn(preferencesFileWithAbsolutePath()),
                preferencesIn(legacyPreferencesFileWithAbsolutePath()),
                preferencesInClasspath());

    }

    private Properties preferencesInClasspath() throws IOException {
        try (InputStream propertiesOnClasspath = propertiesInputStream()) {
            if (propertiesOnClasspath != null) {
                Properties localPreferences = new Properties();
                localPreferences.load(propertiesOnClasspath);
                return localPreferences;
            }
        }
        return new Properties();
    }

    private InputStream propertiesInputStream(){
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(defaultPropertiesFileName());
        if (input == null) {
            input = Thread.currentThread().getContextClassLoader().getResourceAsStream(legacyPropertiesFileName());
        }
        return input;
    }

    private Properties typesafeConfigPreferences() {
        Set<Map.Entry<String, ConfigValue>> preferences = ConfigFactory.load(TYPESAFE_CONFIG_FILE).entrySet();
        Properties properties = new Properties();
        for (Map.Entry<String, ConfigValue> preference : preferences) {
            properties.put(preference.getKey(), strip(preference.getValue().render(), "\""));
        }
        return properties;
    }


    private void updatePreferencesFrom(Properties... propertySets) throws IOException {
        for (Properties localPreferences : propertySets) {
            setUndefinedSystemPropertiesFrom(localPreferences);
        }
    }

    private Properties preferencesIn(File preferencesFile) throws IOException {
        Properties preferenceProperties = new Properties();
        if (preferencesFile.exists()) {
            try(InputStream preferences = new FileInputStream(preferencesFile)) {
                LOGGER.debug("LOADING LOCAL PROPERTIES FROM {} ", preferencesFile.getAbsolutePath());
                preferenceProperties.load(preferences);
            }
        }
        return preferenceProperties;
    }

    private void setUndefinedSystemPropertiesFrom(Properties localPreferences) {
        Enumeration<?> propertyNames = localPreferences.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = (String) propertyNames.nextElement();
            String localPropertyValue = localPreferences.getProperty(propertyName);
            String currentPropertyValue = environmentVariables.getProperty(propertyName);

            if ((currentPropertyValue == null) && (localPropertyValue != null)) {
                LOGGER.info(propertyName + "=" + localPropertyValue);
                environmentVariables.setProperty(propertyName, localPropertyValue);
            }
        }
    }

    private File preferencesFileInHomeDirectory() {
        return new File(homeDirectory, defaultPropertiesFileName());
    }

    private File legacyPreferencesFileInHomeDirectory() {
        return new File(homeDirectory, legacyPropertiesFileName());
    }

    private File preferencesFileInWorkingDirectory() {
        return new File(workingDirectory, defaultPropertiesFileName());
    }

    private File preferencesFileInMavenModuleDirectory() {
        return new File(mavenModuleDirectory, defaultPropertiesFileName());
    }

    private File preferencesFileInMavenParentModuleDirectory() {
        File parentModuleDirectory = mavenModuleDirectory.getParentFile();
        return new File(parentModuleDirectory, defaultPropertiesFileName());
    }

    private File legacyPreferencesFileInWorkingDirectory() {
        return new File(workingDirectory, legacyPropertiesFileName());
    }

    private File preferencesFileWithAbsolutePath() {
        return new File(defaultPropertiesFileName());
    }

    private File legacyPreferencesFileWithAbsolutePath() {
        return new File(legacyPropertiesFileName());
    }

    private String defaultPropertiesFileName() {
        return ThucydidesSystemProperty.PROPERTIES.from(environmentVariables, "serenity.properties");
    }

    private String legacyPropertiesFileName() {
        return ThucydidesSystemProperty.PROPERTIES.from(environmentVariables, "thucydides.properties");
    }

}
