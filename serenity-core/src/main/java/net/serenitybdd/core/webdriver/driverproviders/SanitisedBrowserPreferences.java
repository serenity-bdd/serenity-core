package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.configuration.FilePathParser;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SanitisedBrowserPreferences {

    public static Map<String, Object> cleanUpPathsIn(Map<String, Object> rawPreferences) {
        Map<String, Object> preferences = new HashMap<>();
        rawPreferences.forEach(
                (key,value) -> preferences.put(key, SanitisedBrowserPreferences.of(value))
        );
        return preferences;
    }

    private static Object of(Object value) {
        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        FilePathParser parser = new FilePathParser(environmentVariables);
        if (value instanceof String) {
            if (isMalformedPath(value.toString())) {
                return parser.getInstanciatedPath(value.toString());
            }
        }
        return value;
    }

    private static boolean isMalformedPath(String value) {
        try {
            Paths.get(value).toRealPath();
        } catch (IOException e) {
            return true;
        }
        return false;
    }
}
