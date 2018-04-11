package net.thucydides.core.util;

import java.io.IOException;

/**
 * Loads configuration values from local files into the environment variables.
 */
public interface LocalPreferences {
    void loadPreferences() throws IOException;
}
