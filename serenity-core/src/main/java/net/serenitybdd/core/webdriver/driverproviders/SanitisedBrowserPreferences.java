package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.model.configuration.FilePathParser;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SanitisedBrowserPreferences {

    public static Map<String, Object> cleanUpPathsIn(Map<String, Object> rawPreferences) {
        Map<String, Object> preferences = new HashMap<>();
        rawPreferences.forEach(
                (key,value) -> preferences.put(key, SanitisedBrowserPreferences.of(value))
        );
        return preferences;
    }

    public static List<String> cleanUpPathsIn(List<String> rawPreferences) {
        return rawPreferences.stream()
                .map( value ->  SanitisedBrowserPreferences.of(value).toString() )
                .collect(Collectors.toList());
    }

    private static Object of(Object value) {
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
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
