package net.serenitybdd.screenplay.visual;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Default snapshot file namer that stores baselines in {@code src/test/resources/visual-baselines},
 * actual screenshots in {@code target/visual-comparisons/actual}, and diff images in
 * {@code target/visual-comparisons/diff}.
 */
public class DefaultSnapshotFileNamer implements SnapshotFileNamer {

    private static final String BASELINES_DIR = "src/test/resources/visual-baselines";
    private static final String ACTUAL_DIR = "target/visual-comparisons/actual";
    private static final String DIFF_DIR = "target/visual-comparisons/diff";

    private final String baselineName;

    public DefaultSnapshotFileNamer(String baselineName) {
        this.baselineName = baselineName;
    }

    @Override
    public String baselineName(String description) {
        return baselineName;
    }

    @Override
    public Path baselinePath(String description, String extension) {
        return Paths.get(BASELINES_DIR, baselineName + "." + extension);
    }

    @Override
    public Path actualPath(String description, String extension) {
        return Paths.get(ACTUAL_DIR, baselineName + "." + extension);
    }

    @Override
    public Path diffPath(String description, String extension) {
        return Paths.get(DIFF_DIR, baselineName + "-diff." + extension);
    }
}
