package net.thucydides.model.util;

import java.io.File;

public class TestResources {
    public static File directoryInClasspathCalled(final String resourceName) {
        return FileSystemUtils.getResourceAsFile(resourceName);
    }

    public static File fileInClasspathCalled(final String resourceName) {
        return FileSystemUtils.getResourceAsFile(resourceName);
    }

}
