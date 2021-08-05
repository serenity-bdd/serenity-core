package net.serenitybdd.maven.plugins;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.Optional;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_HISTORY_DIRECTORY;

public class HistoryDirectory {

    private final static String DEFAULT_HISTORY_DIRECTORY = "history";

    public static String configuredIn(EnvironmentVariables environmentVariables, String configuredHistoryPath) {
        return SERENITY_HISTORY_DIRECTORY.from(environmentVariables,
                                               Optional.ofNullable(configuredHistoryPath).orElse(DEFAULT_HISTORY_DIRECTORY));

    }
}
