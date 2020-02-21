package net.thucydides.core.logging;

import net.thucydides.core.util.EnvironmentVariables;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_CONSOLE_BANNER;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_CONSOLE_HEADINGS;

public enum ConsoleHeadingStyle {
    NONE(0), NORMAL(1), ASCII(2);

    private int level;

    ConsoleHeadingStyle(int level) {
        this.level = level;
    }

    public static ConsoleHeadingStyle bannerStyleDefinedIn(EnvironmentVariables environmentVariables) {
        String logLevel = SERENITY_CONSOLE_BANNER.from(environmentVariables, ASCII.name()).toUpperCase();
        return valueOf(logLevel);
    }

    public int getLevel() { return level; }
    public int getHeaderLevel() { return level; }

    public static ConsoleHeadingStyle definedIn(EnvironmentVariables environmentVariables) {
        String logLevel = SERENITY_CONSOLE_HEADINGS.from(environmentVariables, ASCII.name()).toUpperCase();
        return valueOf(logLevel);
    }

}

