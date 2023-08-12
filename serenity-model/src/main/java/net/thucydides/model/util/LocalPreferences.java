package net.thucydides.model.util;

import com.typesafe.config.Config;

import java.io.IOException;

/**
 * Loads configuration values from local files into the environment variables.
 */
public interface LocalPreferences {
    void loadPreferences() throws IOException;

    Config getConfig();
}
