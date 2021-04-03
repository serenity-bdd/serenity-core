package net.thucydides.core.util;

import java.io.File;

/**
 * Created by john on 29/06/2014.
 */
public class FileSystemUtils {
    public static File getResourceAsFile(String resourceName) {
        return new File(Thread.currentThread().getContextClassLoader().getResource(stripLeadingSlash(resourceName)).getFile().replaceAll("%20"," "));
    }

    private static String stripLeadingSlash(String resourceName) {
        return (resourceName.startsWith("/")) ? resourceName.substring(1) : resourceName;
    }
}
