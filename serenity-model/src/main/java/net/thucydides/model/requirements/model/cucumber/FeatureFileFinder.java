package net.thucydides.model.requirements.model.cucumber;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Return a stream of all the feature files in a given directory
 */
public class FeatureFileFinder {

    private String directoryPath;

    public FeatureFileFinder(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public Stream<File> findFeatureFiles() throws IOException {
        try {
            return Files.walk(new File(directoryPath).toPath())
                    .map(java.nio.file.Path::toFile)
                    .filter(File::isFile)
                    .filter(file -> file.getName().endsWith(".feature"));
        } catch (IllegalStateException e) {
            throw new IOException("Error parsing the cucumber feature file directories: " + e.getMessage(), e);
        }
    }
}
