package net.thucydides.model.logging;

import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;

public class ConsoleColors {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[91m";
    private static final String ANSI_GREEN = "\u001B[92m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_PURPLE = "\u001B[95m";
    private static final String ANSI_CYAN = "\u001B[96m";
    public static final String	HIGH_INTENSITY = "\u001B[1m";
    public static final String	LOW_INTENSITY		= "\u001B[2m";

    private final EnvironmentVariables environmentVariables;

    public ConsoleColors(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    private boolean showColoredOutput() {
        return ThucydidesSystemProperty.SERENITY_CONSOLE_COLORS.booleanFrom(environmentVariables, false);
    }

    public String red(String text) {
        return (showColoredOutput()) ? ANSI_RED + text + ANSI_RESET : text;
    }

    public String grey(String text) {
        return (showColoredOutput()) ? ANSI_RED + text + ANSI_RESET : text;
    }

    public String green(String text) {
        return (showColoredOutput()) ? ANSI_GREEN + text + ANSI_RESET : text;
    }

    public String yellow(String text) {
        return (showColoredOutput()) ? ANSI_YELLOW + text + ANSI_RESET : text;
    }

    public String cyan(String text) {
        return (showColoredOutput()) ? ANSI_CYAN + text + ANSI_RESET : text;
    }

    public String purple(String text) {
        return (showColoredOutput()) ? ANSI_PURPLE + text + ANSI_RESET : text;
    }

    public String white(String text) {
        return text;
    }

    public String bold(String text) { return  (showColoredOutput()) ? HIGH_INTENSITY + text + ANSI_RESET : text;}
}
