package net.serenitybdd.plugins.jira.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Somewhere to hold the Guice injector.
 * There might be a better way to do this.
 */
public class Injectors {

    private static Injector injector;

    public static Injector getInjector() {
        if (injector == null) {
            injector = Guice.createInjector(new ThucydidesJiraModule());
        }
        return injector;
    }
}
