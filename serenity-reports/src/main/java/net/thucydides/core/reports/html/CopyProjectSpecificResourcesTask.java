package net.thucydides.core.reports.html;

import net.thucydides.core.reports.ReportGenerationFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static net.thucydides.core.ThucydidesSystemProperty.REPORT_ASSETS_DIRECTORY;

/**
 * Copy image resources from a project directory into the target directory, in a directory called assets
 * By default, all files will be copied from the src/test/resources/assets directory to image directory in the output directory
 */
class CopyProjectSpecificResourcesTask extends HtmlReporter implements ReportingTask {

    private final static String DEFAULT_ASSETS_DIRECTORIES = "src/test/resources/assets";

    @Override
    public void generateReports() throws IOException {

        if (!assetSourcePath().toFile().exists()) { return; }

        Files.createDirectories(assetsDestinationDirectory());

        Files.list(assetSourcePath())
                .filter(this::notCopied)
                .parallel()
                .forEach(this::copyToTarget);
    }

    @Override
    public String reportName() {
        return "CopyProjectSpecificResourcesTask";
    }

    private boolean notCopied(Path imageFile) {
        return !transferred(imageFile).toFile().exists();
    }

    private void copyToTarget(Path imageFile) {
        try {
            if (notCopied(imageFile)) {
                Files.copy(imageFile, transferred(imageFile), REPLACE_EXISTING);
            }
        } catch (FileSystemException anotherThreadIsAlreadyCopyingTheFileWeAreProbablyCool) {
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