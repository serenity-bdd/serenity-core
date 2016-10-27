package net.serenitybdd.core.webdriver.servicepools;

import com.google.common.base.Optional;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.os.CommandLine;

import java.io.File;

import static com.google.common.base.Preconditions.checkState;

public class DriverServiceExecutable {

    private final String exeName;
    private final String exeProperty;
    private final String documentationUrl;
    private final String downloadUrl;
    private final EnvironmentVariables environmentVariables;

    public DriverServiceExecutable(String exeName,
                                   String exeProperty,
                                   String documentationUrl,
                                   String downloadUrl,
                                   EnvironmentVariables environmentVariables) {
        this.exeName = exeName;
        this.exeProperty = exeProperty;
        this.documentationUrl = documentationUrl;
        this.downloadUrl = downloadUrl;
        this.environmentVariables = environmentVariables;
    }


    public static DriverServiceExecutableBuilder called(String exeName) {
        return new DriverServiceExecutableBuilder(exeName);
    }

    public static class DriverServiceExecutableBuilder {

        private final String exeName;
        private String exeProperty;
        private String documentationUrl;
        private Optional<EnvironmentVariables> environmentVariables = Optional.absent();

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

        public DriverServiceExecutable andDownloadableFrom(String downloadUrl) {
            return new DriverServiceExecutable(exeName,
                    exeProperty,
                    documentationUrl,
                    downloadUrl,
                    environmentVariables.or(
                            Injectors.getInjector().getInstance(EnvironmentVariables.class))
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

        String documentationSource = Optional.fromNullable(documentationUrl).or(downloadUrl);

        checkState(exePath != null,
                "The path to the %s driver executable must be set by the %s system property;"
                        + " for more information, see %s. "
                        + "The latest version can be downloaded from %s",
                exeName, exeProperty, documentationSource, downloadUrl);

        File exe = new File(exePath);
        checkExecutable(exe);
        return exe;
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
