package net.serenitybdd.core.webdriver.servicepools;

import net.serenitybdd.core.CurrentOS;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.os.ExecutableFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class DriverServiceExecutable {

    private final String exeName;
    private final String exeProperty;
    private final String documentationUrl;
    private final String downloadUrl;
    private final EnvironmentVariables environmentVariables;
    private final boolean reportMissingBinary;

    private static final String ANSI_RED = "\u001B[91m";
    private static final String ANSI_RESET = "\u001B[0m";
    private final static String DRIVER_ISSUE_BANNER =
            "  ___      _                ___           __ _                    _   _            ___                \n" +
                    " |   \\ _ _(_)_ _____ _ _   / __|___ _ _  / _(_)__ _ _  _ _ _ __ _| |_(_)___ _ _   |_ _|_______  _ ___ \n" +
                    " | |) | '_| \\ V / -_) '_| | (__/ _ \\ ' \\|  _| / _` | || | '_/ _` |  _| / _ \\ ' \\   | |(_-<_-< || / -_)\n" +
                    " |___/|_| |_|\\_/\\___|_|    \\___\\___/_||_|_| |_\\__, |\\_,_|_| \\__,_|\\__|_\\___/_||_| |___/__/__/\\_,_\\___|\n" +
                    "  ___ ___ ___ ___ ___ ___ ___ ___ ___ ___ ___ |___/__ ___ ___ ___ ___ ___ ___ ___ ___ ___ ___ ___ ___ \n" +
                    " |___|___|___|___|___|___|___|___|___|___|___|___|___|___|___|___|___|___|___|___|___|___|___|___|___|\n" +
                    "                                                                                                      \n" +
                    "                                                                                                      ";

    private final static String MISSING_BINARY =
            "The path to the %s driver executable must be set by the %s system property, or be available on the system path;"
                    + " for more information, see %s. "
                    + "The latest version can be downloaded from %s";

    private final static String NON_EXECUTABLE_BINARY =
            "The specified driver value of '%s' appears to be incorrect. Make sure it is the correct WebDriver driver for this operating system.";
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverServiceExecutable.class);


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
        private Optional<EnvironmentVariables> environmentVariables = Optional.empty();
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
            //this.reportMissingBinary = true;
            // Todo: Report Missing Binaries does not work in multi-module Gradle projects, and is disabled until this is resolved.
            return this;
        }

        public DriverServiceExecutable downloadableFrom(String downloadUrl) {
            return new DriverServiceExecutable(exeName,
                    exeProperty,
                    documentationUrl,
                    downloadUrl,
                    environmentVariables.orElse(
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

        Path binaryPath = asAPath();

        if (reportMissingBinary) {
            checkForMissingBinaries(binaryPath);
        }
        return binaryPath != null ? binaryPath.toFile() : null;
    }

    public Path asAPath() {

        String pathOnFilesystem = new ExecutableFinder().find(exeName);
        Optional<String> defaultPath = Optional.ofNullable(pathOnFilesystem);

        Optional<String> osSpecificPath
                = Optional.ofNullable(nullIfEmpty(
                configuredPath(osSpecific(exeProperty))
        ));

        Optional<String> configuredBinaryPath = Optional.ofNullable(nullIfEmpty((configuredPath(exeProperty))));

        String exePath = configuredBinaryPath.orElse(osSpecificPath.orElse(defaultPath.orElse(null)));

        return (exePath == null) ? null : Paths.get(exePath);
    }

    private String configuredPath(String propertyName) {
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(propertyName)
                .orElse(null);
    }

    private String nullIfEmpty(String value) {
        return isEmpty(value) ? null : value;
    }

    private String osSpecific(String exeProperty) {
        return "drivers." + CurrentOS.getType() + "." + exeProperty;
    }

    private void checkForMissingBinaries(Path binaryPath) {
        String documentationSource = Optional.ofNullable(documentationUrl).orElse(downloadUrl);

        if (binaryPath == null) {
            logErrorBanner(System.lineSeparator() + DRIVER_ISSUE_BANNER
                    + System.lineSeparator()
                    + String.format(MISSING_BINARY, exeName, exeProperty, documentationSource, downloadUrl));
        } else if (!isExecutable(binaryPath.toFile())) {
            logErrorBanner(System.lineSeparator() + DRIVER_ISSUE_BANNER
                    + System.lineSeparator()
                    + String.format(NON_EXECUTABLE_BINARY, binaryPath.toFile()));
        }

        checkState(binaryPath != null, "Path to the driver file was not defined");
        checkExecutable(binaryPath.toFile());
    }


    protected static boolean isExecutable(File exe) {
        return exe.exists() && !exe.isDirectory() && exe.canExecute();
    }

    protected static void checkExecutable(File exe) {

        checkState(exe.exists(),
                "The driver executable does not exist: %s", exe.getAbsolutePath());
        checkState(!exe.isDirectory(),
                "The driver executable is a directory: %s", exe.getAbsolutePath());
        checkState(exe.canExecute(),
                "The driver is not executable: %s", exe.getAbsolutePath());
    }

    private static void logErrorBanner(String text) {
        LOGGER.error(ANSI_RED + text + ANSI_RESET);
    }
}
