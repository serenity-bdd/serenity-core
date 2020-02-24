package net.thucydides.core.logging;

import net.thucydides.core.util.EnvironmentVariables;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_LOGGING;

/**
 * Which errors should be displayed
 */
public enum LoggingLevel {
    /**
     * Disable Serenity logging.
     */
    NONE,
    /**
     * Only report compromised tests, errors and failures.
     */
    QUIET,
    /**
     * Report on the completion of each test
     */
    SUMMARY,
    /**
     * Log the start and end of each test, and the result of each test.
     */
    NORMAL,
    /**
     * Log the start and end of each test, and the result of each test, and each test step.
     */
    VERBOSE;

    public static LoggingLevel definedIn(EnvironmentVariables environmentVariables) {
        String logLevel = SERENITY_LOGGING.from(environmentVariables, NORMAL.name()).toUpperCase();
        return valueOf(logLevel);
    }

    public boolean isAtLeast(LoggingLevel minimumLoggingLevel) {
        return (this.compareTo(minimumLoggingLevel) >= 0);
    }
}
