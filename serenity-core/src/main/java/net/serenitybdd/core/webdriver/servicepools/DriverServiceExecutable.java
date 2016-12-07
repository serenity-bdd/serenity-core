package net.serenitybdd.core.webdriver.servicepools;

import com.google.common.base.Optional;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.os.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.google.common.base.Preconditions.checkState;

public class DriverServiceExecutable {

    private final String exeName;
    private final String exeProperty;
    private final String documentationUrl;
    private final String downloadUrl;
    private final EnvironmentVariables environmentVariables;
    private final boolean reportMissingBinary;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public DriverServiceExecutable(String exeName,
                                   String exeProperty,
                                   String documentationUrl,
                                   String downloadUrl,
                                   EnvironmentVariables environmentVariables,
                                   boolean checkExecutable) {
        this.exeName = exeName;
        this.exeProperty = exeProperty;
        this.documentationUrl = documentationUrl;
        this.downloadUrl = downloadUrl;
        this.environmentVariables = environmentVariables;
        this.reportMissingBinary = checkExecutable;
    }


    public static DriverServiceExecutableBuilder called(String exeName) {
        return new DriverServiceExecutableBuilder(exeName);
    }

    public static class DriverServiceExecutableBuilder {

        private final String exeName;
        private String exeProperty;
        private String documentationUrl;
        private Optional<EnvironmentVariables> environmentVariables = Optional.absent();
        private boolean reportMissingBinary = false;

        public DriverServiceExecutableBuilder(String exeName) {
            this.exeName = exeName;
        }

        public DriverServiceExecutableBuilder withSystemProperty(String environmentProperty) {
            this.exeProperty = environmentProperty;
            return this;
        }

        public DriverServiceExecutableBuilder documentedAt(String documentationUrl) {
            this.documentationUrl = documentationUrl;
            return this;
        }

        public DriverServiceExecutableBuilder reportMissingBinary() {
            this.reportMissingBinary = true;
            return this;
        }

        public DriverServiceExecutable downloadableFrom(String downloadUrl) {
            return new DriverServiceExecutable(exeName,
                    exeProperty,
                    documentationUrl,
                    downloadUrl,
                    environmentVariables.or(
                            ConfiguredEnvironment.getEnvironmentVariables()),
                    reportMissingBinary
            );
        }

        public DriverServiceExecutableBuilder usingEnvironmentVariables(EnvironmentVariables environmentVariables) {
            this.environmentVariables = Optional.of(environmentVariables);
            return this;
        }
    }

    public File asAFile() {
        Optional<String> defaultPath = Optional.fromNullable(CommandLine.find(exeName));

        Optional<String> configuredBinaryPath = Optional.fromNullable(environmentVariables.getProperty(exeProperty));
        String exePath = configuredBinaryPath.or(defaultPath).orNull();

        File executableLocation = (exePath != null) ? new File(exePath) : null;

        if (reportMissingBinary) {
            checkForMissingBinaries(executableLocation);
        }
        return executableLocation;
    }

    private void checkForMissingBinaries(File executableLocation) {
        String documentationSource = Optional.fromNullable(documentationUrl).or(downloadUrl);

        checkState(executableLocation != null,
                "The path to the %s driver executable must be set by the %s system property;"
                        + " for more information, see %s. "
                        + "The latest version can be downloaded from %s",
                exeName, exeProperty, documentationSource, downloadUrl);
        checkExecutable(executableLocation);
    }

    protected static void checkExecutable(File exe) {

        checkState(exe.exists(),
                "The driver executable does not exist: %s", exe.getAbsolutePath());
        checkState(!exe.isDirectory(),
                "The driver executable is a directory: %s", exe.getAbsolutePath());
        checkState(FileHandler.canExecute(exe),
                "The driver is not executable: %s", exe.getAbsolutePath());
    }
}
