package net.thucydides.core.reports.html;

import net.thucydides.core.reports.ReportGenerationFailedError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.thucydides.core.ThucydidesSystemProperty.REPORT_ASSETS_DIRECTORY;

/**
 * Copy image resources from a project directory into the target directory, in a directory called assets
 * By default, all files will be copied from the src/test/resources/assets directory to image directory in the output directory
 */
class CopyProjectSpecificResourcesTask extends HtmlReporter implements ReportingTask {

    private final static String DEFAULT_ASSETS_DIRECTORIES = "src/test/resources/assets";

    @Override
    public void generateReports() throws IOException {

        Files.createDirectories(assetsDestinationDirectory());

        Files.list(assetSourcePath())
                .filter(this::notCopied)
                .parallel()
                .forEach(this::copyToTarget);
    }

    private boolean notCopied(Path imageFile) {
        return !transferred(imageFile).toFile().exists();
    }

    private void copyToTarget(Path imageFile) {
        try {
            Files.copy(imageFile, transferred(imageFile));
        } catch (IOException e) {
            throw new ReportGenerationFailedError(e.getMessage(), e);
        }
    }

    private Path transferred(Path imageFile) {
        return assetsDestinationDirectory().resolve(imageFile.getFileName());
    }

    private Path assetSourcePath() {
        String assetDirectoryPath = REPORT_ASSETS_DIRECTORY.from(getEnvironmentVariables(), DEFAULT_ASSETS_DIRECTORIES);
        return Paths.get(assetDirectoryPath);
    }

    private Path assetsDestinationDirectory() {
        return getOutputDirectory().toPath().resolve("assets");
    }
}