package net.thucydides.core.util;

import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Loads Thucydides preferences from a local file called thucydides.properties.
 * Thucydides options can be loaded from the thucydides.properties file in the home directory, in the working directory,
 * or on the classpath. There can be multiple thucydides.properties files, in which case values from the file in the
 * working directory override values on the classpath, and values in the home directory override values in the working
 * directory. Values can always be overridden on the command line.
 */
public class PropertiesFileLocalPreferences implements LocalPreferences {

    private File workingDirectory;
    private File homeDirectory;
    private final EnvironmentVariables environmentVariables;
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesFileLocalPreferences.class);

    @Inject
    public PropertiesFileLocalPreferences(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.homeDirectory = new File(System.getProperty("user.home"));
        this.workingDirectory = new File(System.getProperty("user.dir"));
    }

    public File getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(File homeDirectory) {
        this.homeDirectory = homeDirectory;
    }

    public void loadPreferences() throws IOException {
        updatePreferencesFrom(preferencesFileInHomeDirectory());
        updatePreferencesFrom(preferencesFileInWorkingDirectory());
        updatePreferencesFrom(preferencesFileWithAbsolutePath());
        updatePreferencesFromClasspath();
    }

    private void updatePreferencesFromClasspath() throws IOException {
        InputStream propertiesOnClasspath = null;
        try {
            propertiesOnClasspath = Thread.currentThread().getContextClassLoader().getResourceAsStream(defaultPropertiesFileName());
            if (propertiesOnClasspath != null) {
                Properties localPreferences = new Properties();
                localPreferences.load(propertiesOnClasspath);
                setUndefinedSystemPropertiesFrom(localPreferences);
            }
        } finally {
            if (propertiesOnClasspath != null) {
                propertiesOnClasspath.close();
            }
        }
    }

    private void updatePreferencesFrom(File preferencesFile) throws IOException {
        if (preferencesFile.exists()) {
            Properties localPreferences = new Properties();
            LOGGER.info("LOADING LOCAL THUCYDIDES PROPERTIES FROM {} ", preferencesFile.getAbsolutePath());
            localPreferences.load(new FileInputStream(preferencesFile));
            setUndefinedSystemPropertiesFrom(localPreferences);
        }
    }

    private void setUndefinedSystemPropertiesFrom(Properties localPreferences) {
        Enumeration propertyNames = localPreferences.propertyNames();
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

    private File preferencesFileInWorkingDirectory() {
        return new File(workingDirectory, defaultPropertiesFileName());
    }

    private File preferencesFileWithAbsolutePath() {
        return new File(defaultPropertiesFileName());
    }

    private String defaultPropertiesFileName() {
        return ThucydidesSystemProperty.PROPERTIES.from(environmentVariables,"thucydides.properties");
    }

}
