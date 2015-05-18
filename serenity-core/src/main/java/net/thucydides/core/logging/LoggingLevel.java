package net.thucydides.core.logging;

public enum LoggingLevel {
    /**
     * No Thucydides logging at all.
     */
    QUIET,
    /**
     * Log the start and end of tests.
     */
    NORMAL,
    /**
     * Log the start and end of tests and test steps.
     */
    VERBOSE
}
