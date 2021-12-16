package net.serenitybdd.cucumber.model;

import net.serenitybdd.cucumber.CucumberWithSerenity;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StoredFeatureFile {

    private final String featureFileName;

    public StoredFeatureFile(String featureFileName) {

        this.featureFileName = featureFileName;
    }

    public URL asAClasspathResource() {
        return StoredFeatureFile.class.getClassLoader().getResource(featureFileName);
    }

    public boolean existsOnTheClasspath() {
        return (asAClasspathResource() != null);
    }

    public static StoredFeatureFile withName(String featureFileName) {
        return new StoredFeatureFile(featureFileName);
    }

    public File onTheClasspath() {
        return new File(asAClasspathResource().getFile());
    }

    public boolean existsOnTheFileSystem() {
        return Files.exists(Paths.get(featureFileName));
    }

    public File onTheFileSystem() {
        return Paths.get(featureFileName).toFile();
    }
    

    public File fromTheConfiguredPaths() throws IOException {
        for(URI uri : CucumberWithSerenity.currentRuntimeOptions().getFeaturePaths()) {
            if (Files.exists(candidatePath(uri, featureFileName))) {
                return candidatePath(uri, featureFileName).toFile();
            }
        }
        throw new IOException("No such feature file found for " + featureFileName);
    }

    private Path candidatePath(URI uri, String featureFileName) {
        return Paths.get(Stream.of(uri.getPath(), featureFileName).collect(Collectors.joining(File.separator)));
    }
}
