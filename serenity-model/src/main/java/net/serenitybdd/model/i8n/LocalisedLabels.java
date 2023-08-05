package net.serenitybdd.model.i8n;

import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.Locale;

/**
 * Read localised labels from the serenity.conf file.
 *
 * i8n {
 *     "Search" {
 *         en: "Search"
 *         fr: "Recherche"
 *     }
 *     "Sign Up" {
 *         en: "Sign Up"
 *         fr: "Se connecter"
 *     }
 * } */
public class LocalisedLabels {

    private static final ThreadLocal<Locale> CURRENT_LOCALE = ThreadLocal.withInitial(Locale::getDefault);
    private final EnvironmentVariables environmentVariables;
    private final Locale currentLocale;

    private LocalisedLabels(EnvironmentVariables environmentVariables, Locale currentLocale) {
        this.environmentVariables = environmentVariables;
        this.currentLocale = currentLocale;
    }

    public static LocalisedLabels forCurrentLocale() {
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
        return new LocalisedLabels(environmentVariables, CURRENT_LOCALE.get());
    }

    public static LocalisedLabels forLocale(Locale locale) {
        CURRENT_LOCALE.set(locale);
        return forCurrentLocale();
    }

    public String getLabelFor(String name) {
        return null;
    }
}
