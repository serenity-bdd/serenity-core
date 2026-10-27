package net.serenitybdd.screenplay.visual;

import java.nio.file.Path;

/**
 * Provides file paths for visual comparison snapshots.
 */
public interface SnapshotFileNamer {

    /**
     * The baseline name.
     */
    String baselineName(String description);

    /**
     * Path where the baseline image is stored.
     */
    Path baselinePath(String description, String extension);

    /**
     * Path where the actual screenshot is saved.
     */
    Path actualPath(String description, String extension);

    /**
     * Path where the diff image is saved.
     */
    Path diffPath(String description, String extension);
}
