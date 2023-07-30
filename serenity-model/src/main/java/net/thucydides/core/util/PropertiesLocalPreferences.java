package net.thucydides.core.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Loads Thucydides preferences from a local file called thucydides.properties.
 * Thucydides options can be loaded from the thucydides.properties file in the home directory, in the working directory,
 * or on the classpath. There can be multiple thucydides.properties files, in which case values from the file in the
 * working directory override values on the classpath, and values in the home directory override values in the working
 * directory. Values can always be overridden on the command line.
 */
public class PropertiesLocalPreferences implements LocalPreferences {

    public static final String TYPESAFE_CONFIG_FILE = "serenity.conf";
    private volatile File workingDirectory;
    private volatile File homeDirectory;
    private volatile File mavenModuleDirectory;
    private final Map<String, String> currentProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLocalPreferences.class);

    private final Lock lock = new ReentrantLock();

    /**
     * Configuration file path provided programmatically - used for testing
     */
    private Path configurationFilePath;

    public PropertiesLocalPreferences(Map<String, String> properties) {
        this.currentProperties = properties;
        this.homeDirectory = new File(System.getProperty("user.home"));
        this.workingDirectory = new File(System.getProperty("user.dir"));
        final String mavenBuildDir = System.getProperty(SystemPropertiesConfiguration.PROJECT_BUILD_DIRECTORY);
        if (!isEmpty(mavenBuildDir)) {
            this.mavenModuleDirectory = new File(mavenBuildDir);
        } else {
            this.mavenModuleDirectory = this.workingDirectory;
        }
    }

    public PropertiesLocalPreferences(Map<String, String> currentProperties, Path configurationFilePath) {
        this(currentProperties);
        this.configurationFilePath = configurationFilePath;
    }

    public PropertiesLocalPreferences(EnvironmentVariables environmentVariables) {
        this(environmentVariables.properties());
    }

    public File getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(File homeDirectory) {
        lock.lock();
        try {
            this.homeDirectory = homeDirectory;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Config getConfig() {
        return typesafeConfig();
    }

    public void loadPreferences() throws IOException {
        lock.lock();
        try {
            updatePreferencesFrom(
                    preferencesIn(preferencesFileWithAbsolutePath()),
                    preferencesIn(legacyPreferencesFileWithAbsolutePath()),
                    typesafeConfigPreferencesInCustomDefinedConfigFile(),
                    typesafeConfigPreferences(),
                    preferencesIn(preferencesFileInMavenModuleDirectory()),
                    preferencesIn(preferencesFileInMavenParentModuleDirectory()),
                    preferencesIn(preferencesFileInWorkingDirectory()),
                    preferencesIn(legacyPreferencesFileInWorkingDirectory()),
                    preferencesIn(preferencesFileInHomeDirectory()),
                    preferencesIn(legacyPreferencesFileInHomeDirectory()),
                    preferencesInClasspath());
        } finally {
            lock.unlock();
        }
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

    private InputStream propertiesInputStream() {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(defaultPropertiesFileName());
        if (input == null) {
            input = Thread.currentThread().getContextClassLoader().getResourceAsStream(legacyPropertiesFileName());
        }
        return input;
    }

    private Properties typesafeConfigPreferencesInCustomDefinedConfigFile() {

        Optional<File> providedConfigFile = defaultPropertiesConfFile();
        if (!providedConfigFile.isPresent()) {
            return new Properties();
        }

        Set<Map.Entry<String, ConfigValue>> preferences = typesafeConfigFile(providedConfigFile.get()).entrySet();
        return getPropertiesFromConfig(preferences);
    }

    private Config typesafeConfig() {
        return defaultPropertiesConfFile()
                .filter(File::exists)
                .map(this::typesafeConfigFile)
                .orElse(ConfigFactory.load(TYPESAFE_CONFIG_FILE));
    }

    private boolean typesafeConfigFileExists() {
        if (defaultPropertiesConfFile().isPresent() && defaultPropertiesConfFile().get().exists()) {
            return true;
        }
        URL configFilePath = getClass().getClassLoader().getResource(TYPESAFE_CONFIG_FILE);
        if (configFilePath != null) {
            return new File(configFilePath.getFile()).exists();
        } else {
            return false;
        }
    }

    private Properties typesafeConfigPreferences() {
        return getPropertiesFromConfig(typesafeConfig().entrySet());
    }

    private Config typesafeConfigFile(File configFile) {
        return ConfigCache.instance().getConfig(configFile);
    }

    private Properties getPropertiesFromConfig(Set<Map.Entry<String, ConfigValue>> preferences) {
        Properties properties = new Properties();
        for (Map.Entry<String, ConfigValue> preference : preferences) {
            properties.put(preference.getKey(), strip(preference.getValue().render(), "\""));
        }
        return properties;
    }

    private void updatePreferencesFrom(Properties... propertySets) {
        for (Properties localPreferences : propertySets) {
            PropertiesUtil.expandPropertyAndEnvironmentReferences(System.getenv(), localPreferences);
            setUndefinedSystemPropertiesFrom(localPreferences);
        }
    }

    private Properties preferencesIn(File preferencesFile) throws IOException {
        Properties preferenceProperties = new Properties();
        if (preferencesFile.exists()) {
            try (InputStream preferences = new FileInputStream(preferencesFile)) {
                LOGGER.trace("LOADING LOCAL PROPERTIES FROM {} ", preferencesFile.getAbsolutePath());
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
            String currentPropertyValue = currentProperties.get(propertyName);

            if (isEmpty(currentPropertyValue) && isNotEmpty(localPropertyValue) && !propertyName.equals("//") && !propertyName.equals("#")) {
                LOGGER.trace("{} = {}",propertyName, localPropertyValue);
                currentProperties.put(propertyName, localPropertyValue);
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

    private final String PROPERTIES = ThucydidesSystemProperty.PROPERTIES.getPropertyName();

    private Optional<File> defaultPropertiesConfFile() {

        if (configurationFilePath != null) {
            return Optional.ofNullable(configurationFilePath.toFile());
        }
        List<String> possibleConfigFileNames = new ArrayList<>();

        optionalEnvironmentVariable(System.getProperty(PROPERTIES)).ifPresent(possibleConfigFileNames::add);

        serenityConfFileInASensibleLocation().ifPresent(possibleConfigFileNames::add);

        return possibleConfigFileNames.stream()
                .map(File::new)
                .filter(File::exists)
                .findFirst();
    }

    private final String SERENITY_CONF_FILE = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "serenity.conf";
    private Optional<String> serenityConfFileInASensibleLocation() {
            Path configFileInCurrentDirectory = Paths.get(SERENITY_CONF_FILE);
            return configFileInCurrentDirectory.toFile().exists() ? Optional.of(configFileInCurrentDirectory.toAbsolutePath().toString()) : Optional.empty();
    }

    private String defaultPropertiesFileName() {

        return optionalEnvironmentVariable(System.getProperty(PROPERTIES))
                .orElse(
                        optionalEnvironmentVariable(System.getenv(PROPERTIES))
                                .orElse("serenity.properties")
                );
    }

    private String legacyPropertiesFileName() {
        return optionalEnvironmentVariable(System.getProperty(PROPERTIES))
                .orElse(
                        optionalEnvironmentVariable(System.getenv(PROPERTIES))
                                .orElse("thucydides.properties")
                );
    }

    private Optional<String> optionalEnvironmentVariable(String value) {
        return Optional.ofNullable(value);
    }

}
