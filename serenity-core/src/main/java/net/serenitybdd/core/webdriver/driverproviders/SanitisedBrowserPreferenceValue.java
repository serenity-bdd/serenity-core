package net.serenitybdd.core.webdriver.driverproviders;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class SanitisedBrowserPreferenceValue {
    public static Object of(Object value) {
        if (value instanceof String) {
            if (isMalformedPath(value.toString())) {
                return normalisedPathOf(value.toString());
            }
        }
        return value;
    }

    private static String normalisedPathOf(String value) {
        try {
            Paths.get(value).toRealPath();
        } catch (NoSuchFileException invalidPath) {
            String normalisedPath = value.replace("\\\\", "/").replace("\\", "/");
            try {
                Paths.get(normalisedPath).toRealPath();
                return normalisedPath;
            } catch (IOException notAWindowsPath) {
                return value;
            }
        } catch (IOException otherIOError) {
            // Maybe not a path at all. We don't care much.
            return value;
        }
        // Path good and file exists
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
