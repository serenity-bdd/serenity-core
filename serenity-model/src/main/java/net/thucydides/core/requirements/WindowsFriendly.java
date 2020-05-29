package net.thucydides.core.requirements;

import java.util.regex.Pattern;

public class WindowsFriendly {

    private static final Pattern WINDOWS_SLASH_DRIVE_PREFIX = Pattern.compile("\\/[A-Z]:\\/.*");
    private static final Pattern WINDOWS_DRIVE_PREFIX = Pattern.compile("^[A-Z]:\\/.*");

    public static String formOf(String path) {
        if (WINDOWS_SLASH_DRIVE_PREFIX.matcher(path).matches()) {
            return path.substring(1).replace("/","\\");
        } else if (WINDOWS_DRIVE_PREFIX.matcher(path).matches()) {
            return path.replace("/","\\");
        }
        return path;
    }
}
