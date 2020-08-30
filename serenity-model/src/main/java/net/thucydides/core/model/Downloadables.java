package net.thucydides.core.model;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.webdriver.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class Downloadables {
    public static String copyDownloadableFileFrom(Path source) throws IOException {
        Configuration configuration = ConfiguredEnvironment.getConfiguration();
        File outputDirectory = configuration.getOutputDirectory();

        Path downloadDirectory= outputDirectory.toPath().resolve("downloadable");
        Files.createDirectories(downloadDirectory);

        Path relativePath = downloadableCopyOf(source);
        Path downloadablePath = outputDirectory.toPath().resolve(relativePath);

        Files.copy(source, downloadablePath, StandardCopyOption.REPLACE_EXISTING);

        return relativePath.toString();
    }

    private static Path downloadableCopyOf(Path path) {
        return Paths.get("downloadable", "downloadable-" + UUID.randomUUID() + "-" + path.getFileName().toString());
    }
}
