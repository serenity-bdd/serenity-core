package net.thucydides.model.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SessionLocalTempDirectory {
    private static final ThreadLocal<Path> TEMP_DIR = ThreadLocal.withInitial(() -> temporaryDirectory());

    public static Path forTheCurrentSession() {
        return TEMP_DIR.get();
    }

    public static String asACanonicalPathForTheCurrentSession() {
        try {
            return TEMP_DIR.get().toFile().getCanonicalPath();
        } catch (IOException e) {
            return System.getProperty("java.io.tempdir");
        }
    }

    private static Path temporaryDirectory() {
        try {
            return Files.createTempDirectory("serenity");
        } catch (IOException e) {
            return Paths.get(System.getProperty("java.io.tempdir"));
        }
    }
}
