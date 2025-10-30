package net.thucydides.core.reports.html;

import net.serenitybdd.model.di.ModelInfrastructure;
import net.thucydides.model.reports.ReportGenerationFailedError;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static net.thucydides.model.ThucydidesSystemProperty.REPORT_ASSETS_DIRECTORY;

/**
 * Copy image resources from a project directory into the target directory, in a directory called assets
 * By default, all files will be copied from the src/test/resources/assets directory to image directory in the output directory
 */
class CopyProjectSpecificResourcesTask extends HtmlReporter implements ReportingTask {

    private final static String DEFAULT_ASSETS_DIRECTORIES = "src/test/resources/assets";

    @Override
    public void generateReports() throws IOException {

        if (!Files.exists(assetSourcePath())) {
            return;
        }

        Files.createDirectories(assetsDestinationDirectory());

        Files.list(assetSourcePath())
                .filter(this::notCopied)
                .parallel()
                .forEach(this::copyToTarget);

        Files.list(assetSourcePath())
                .filter(this::notCopied)
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
        // We should be defining the project directory in the maven or gradle plugin, but if not we fall back on the current working directory
        Path projectDirectory = Optional.ofNullable(ModelInfrastructure.getConfiguration().getProjectDirectory()).orElse(Paths.get(""));

        // Now resolve the report assets directory based on the project directory, or use a sensible default value otherwise
        return projectDirectory.resolve(REPORT_ASSETS_DIRECTORY.from(getEnvironmentVariables(), DEFAULT_ASSETS_DIRECTORIES));
    }

    private Path assetsDestinationDirectory() {
        return getOutputDirectory().toPath().resolve("assets");
    }
}
