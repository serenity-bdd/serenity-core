package net.thucydides.core.reports;

import net.serenitybdd.core.CurrentOS;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

public class AsciiColors {

    private final EnvironmentVariables environmentVariables;
    private boolean isBold;

    public AsciiColors(EnvironmentVariables environmentVariables) {
        this(environmentVariables, false);
    }

    public AsciiColors(EnvironmentVariables environmentVariables, boolean isBold) {
        this.environmentVariables = environmentVariables;
        this.isBold = isBold;
    }
    private boolean showColoredOutput() {
        Boolean defaultValue = (!CurrentOS.isWindows());
        return ThucydidesSystemProperty.SERENITY_CONSOLE_COLORS.booleanFrom(environmentVariables,defaultValue);
    }

    public AsciiColors bold() {
        return new AsciiColors(environmentVariables, true);
    }

    public String red(String text) {
        return (showColoredOutput()) ? boldPrefix() +  AnsiEscapes.RED + text + AnsiEscapes.RESET : text;
    }

    private String boldPrefix() {
        return isBold ? AnsiEscapes.INTENSITY_BOLD.toString() : "";
    }

    public String green(String text) {
        return (showColoredOutput()) ?  boldPrefix() + AnsiEscapes.GREEN + text + AnsiEscapes.RESET : text;
    }

    public String grey(String text) {
        return (showColoredOutput()) ?  boldPrefix() +  AnsiEscapes.GREY + text + AnsiEscapes.RESET : text;
    }
    public String yellow(String text) {
        return (showColoredOutput()) ?  boldPrefix() +  AnsiEscapes.YELLOW + text + AnsiEscapes.RESET : text;
    }

    public String cyan(String text) {
        return (showColoredOutput()) ?  boldPrefix() +  AnsiEscapes.CYAN + text + AnsiEscapes.RESET : text;
    }

    public String magenta(String text) {
        return (showColoredOutput()) ?  boldPrefix() +  AnsiEscapes.MAGENTA + text + AnsiEscapes.RESET : text;
    }

    public String white(String text) {
        return (showColoredOutput()) ?  boldPrefix() +  AnsiEscapes.WHITE + text + AnsiEscapes.RESET : text;
    }
}
