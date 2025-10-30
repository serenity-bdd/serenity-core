package net.thucydides.model.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigCache {
    private static final ConfigCache INSTANCE = new ConfigCache();

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCache.class);

    private Map<File, Config> loadedConfigFiles = new ConcurrentHashMap<>();

    public static ConfigCache instance() {
        return INSTANCE;
    }

    public Config getConfig(File configFile) {
        loadedConfigFiles.computeIfAbsent(configFile, this::load);
        return loadedConfigFiles.get(configFile);
    }

    private Config load(File configFile) {
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

    public void clear() {
        loadedConfigFiles.clear();
    }
}
