package net.thucydides.model.requirements.model.cucumber;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            return Files.walk(Paths.get("."))
                    .filter(Files::isDirectory)
                    .filter(dir -> !dir.getFileName().toString().equals("build"))
                    .filter(dir -> !dir.getFileName().toString().equals("target"))
                    .map(dir -> dir.resolve(directoryPath))
                    .filter(Files::exists)
                    .flatMap(this::findFeatureFilesInDirectory)
                    .distinct();
        } catch (IllegalStateException e) {
            throw new IOException("Error parsing the cucumber feature file directories: " + e.getMessage(), e);
        }
    }

    private Stream<File> findFeatureFilesInDirectory(Path dir) {
        try {
            return Files.walk(dir)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(file -> file.getName().endsWith(".feature"));
        } catch (IOException e) {
            throw new RuntimeException("Error reading files in directory: " + dir, e);
        }
    }
}
