package net.serenitybdd.plugins.browserstack;

import net.serenitybdd.core.Serenity;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;

import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

/**
 * Get a build name from the environment variables, or generate one if not provided.
 * It can be defined externally by setting the BROWSERSTACK_BUILD_NAME environment variable.
 * The build name is used to group test results in BrowserStack.
 */
public class BuildNameGenerator {
    private final EnvironmentVariables environmentVariables;

    BuildNameGenerator(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static BuildNameGenerator forEnvironmentVariables(EnvironmentVariables environmentVariables) {
        return new BuildNameGenerator(environmentVariables);
    }

    public String getBuildName() {
        return this.environmentVariables.optionalProperty("BROWSERSTACK_BUILD_NAME")
                .orElseGet(() -> this.environmentVariables.optionalProperty("BROWSERSTACK_BUILD_NAME")
                        .orElse(defaultBuildName()));
    }

    private String defaultBuildName() {

        String currentDirectoryName = Paths.get("").toAbsolutePath().toFile().getName();
        String currentProjectName = ThucydidesSystemProperty.SERENITY_PROJECT_NAME.from(environmentVariables,currentDirectoryName);
        String date = Serenity.getTestSuiteStartTime().format(DateTimeFormatter.BASIC_ISO_DATE)
                      + "-" + Serenity.getTestSuiteStartTime().format(DateTimeFormatter.ofPattern("HHmmss"));
        return currentProjectName + " - " + date;
    }
}
