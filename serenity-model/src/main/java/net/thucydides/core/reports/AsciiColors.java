package net.thucydides.core.reports;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

public class AsciiColors {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[91m";
    private static final String ANSI_GREEN = "\u001B[92m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001b[35m";
    private static final String ANSI_CYAN = "\u001B[96m";
    private static final String ANSI_ORANGE = "\u001B[96m";
    private static final String ANSI_SERENITY_GREEN = "\u001B[36m";

    private final EnvironmentVariables environmentVariables;

    public AsciiColors(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    private boolean showColoredOutput() {
        return ThucydidesSystemProperty.SERENITY_CONSOLE_COLORS.booleanFrom(environmentVariables,false);
    }

    public String red(String text) {
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
}
