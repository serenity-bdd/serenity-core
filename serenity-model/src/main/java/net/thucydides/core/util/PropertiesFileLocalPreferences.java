package net.thucydides.core.util;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.requirements.SearchForFilesWithName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.*;

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

    private static final Config systemProperties = ConfigFactory.systemProperties();

    @Inject
    public PropertiesFileLocalPreferences(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.homeDirectory = new File(System.getProperty("user.home"));
        this.workingDirectory = new File(System.getProperty("user.dir"));
        final String mavenBuildDir = System.getProperty(SystemPropertiesConfiguration.PROJECT_BUILD_DIRECTORY);
        if (!isEmpty(mavenBuildDir)) {
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

    private Properties typesafeConfigPreferences() {
        return defaultPropertiesConfFile()
                .filter(File::exists)
                .map(configFile -> getPropertiesFromConfig(typesafeConfigFile(configFile).entrySet()))
                .orElse(getPropertiesFromConfig(ConfigFactory.load(TYPESAFE_CONFIG_FILE).entrySet()));
    }

    private Config typesafeConfigFile(File configFile) {

        // TODO: Cache resolved config for the aggregate phase
        try {
            return ConfigFactory.parseFile(configFile).resolveWith(ConfigFactory.systemProperties());
        } catch (ConfigException failedToReadTheSerenityConfFile) {
            try {
                LOGGER.warn("Failed to read the serenity.conf file: " + failedToReadTheSerenityConfFile.getMessage()
                        + " - Falling back on serenity.conf without using environment variables");
                return ConfigFactory.parseFile(configFile);
            } catch (ConfigException failedToReadTheUnresolvedSerenityConfFile) {
                LOGGER.error("Failed to parse the serenity.conf file", failedToReadTheUnresolvedSerenityConfFile);
                throw failedToReadTheUnresolvedSerenityConfFile;
            }
        }

    }

    private Properties getPropertiesFromConfig(Set<Map.Entry<String, ConfigValue>> preferences) {
        Properties properties = new Properties();
        for (Map.Entry<String, ConfigValue> preference : preferences) {
            properties.put(preference.getKey(), strip(preference.getValue().render(), "\""));
        }
        return properties;
    }

    private void updatePreferencesFrom(Properties... propertySets) throws IOException {
        for (Properties localPreferences : propertySets) {
            PropertiesUtil.expandPropertyAndEnvironmentReferences(System.getenv(), localPreferences);
            setUndefinedSystemPropertiesFrom(localPreferences);
        }
    }

    private Properties preferencesIn(File preferencesFile) throws IOException {
        Properties preferenceProperties = new Properties();
        if (preferencesFile.exists()) {
            try (InputStream preferences = new FileInputStream(preferencesFile)) {
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

            if (isEmpty(currentPropertyValue) && isNotEmpty(localPropertyValue)) {
                LOGGER.debug(propertyName + "=" + localPropertyValue);
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

    private final String PROPERTIES = ThucydidesSystemProperty.PROPERTIES.getPropertyName();

    private Optional<File> defaultPropertiesConfFile() {

        List<String> possibleConfigFileNames = new ArrayList<>();

        optionalEnvironmentVariable(System.getProperty(PROPERTIES)).ifPresent(possibleConfigFileNames::add);

        serenityConfFileInASensibleLocation().ifPresent(possibleConfigFileNames::add);

        return possibleConfigFileNames.stream()
                .map(File::new)
                .filter(File::exists)
                .findFirst();
    }

    private final String SERENITY_CONF_FILE = "(.*)[\\/\\\\]?src[\\/\\\\]test[\\/\\\\]resources[\\/\\\\]serenity.conf";

    private Optional<String> serenityConfFileInASensibleLocation() {
        try {
            return SearchForFilesWithName.matching(Paths.get("."), SERENITY_CONF_FILE).getMatchingFiles()
                    .stream()
                    .findFirst()
                    .map(path -> path.toAbsolutePath().toString());
        } catch (IOException e) {
            return Optional.empty();
        }
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
