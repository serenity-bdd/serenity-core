package net.thucydides.model.requirements.model;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static java.nio.file.Files.readAllBytes;
import static java.util.Arrays.stream;

public class OverviewReader {

    public static Optional<String> readOverviewFrom(String... featureDirectories)  {

        Optional<File> overviewFile = stream(featureDirectories)
                                        .map(featureDirectory -> findOverviewFileIn(featureDirectory))
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .findFirst();

        try {
            return overviewFile.isPresent() ? Optional.of(new String(readAllBytes(overviewFile.get().toPath()))) : Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static Optional<File> findOverviewFileIn(String featureDirectory) {

        if (new File(featureDirectory + "/overview.txt").exists()) {
            return Optional.of(new File(featureDirectory + "/overview.txt"));
        }
        if (new File(featureDirectory + "/overview.md").exists()) {
            return Optional.of(new File(featureDirectory + "/overview.md"));
        }
        if (new File(featureDirectory + "/readme.md").exists()) {
            return Optional.of(new File(featureDirectory + "/readme.md"));
        }
        return Optional.empty();
    }
}
