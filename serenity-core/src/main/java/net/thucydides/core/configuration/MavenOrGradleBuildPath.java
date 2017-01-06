package net.thucydides.core.configuration;

import com.google.common.base.Joiner;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.nio.file.Paths;

import static net.thucydides.core.configuration.MavenBuildDirectory.*;
import static net.thucydides.core.configuration.SystemPropertiesConfiguration.PROJECT_BUILD_DIRECTORY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class MavenOrGradleBuildPath {

    private final EnvironmentVariables environmentVariables;

    /**
     * This property is used when maven/gradle build conta subprojects by serenity  plugins
     */
    private static final String PROJECT_REPORTING_OUTPUT_DIRECTORY = "project.reporting.OutputDirectory";

    MavenOrGradleBuildPath(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static MavenOrGradleBuildPath specifiedIn(EnvironmentVariables environmentVariables) {
        return new MavenOrGradleBuildPath(environmentVariables);
    }

    public String getBuildDirectory() {

        if (isNotEmpty(projectOutputDirectory()) && isAbsolute(projectOutputDirectory())) {
            return forAMavenProjectWithAConfiguredReportDirectory().buildDirectoryFrom(projectOutputDirectory());
        }

        if (isNotEmpty(projectOutputDirectory())) {
            String baseDirectory = Joiner.on(File.separator).skipNulls().join(projectMavenBuildDirectory(), projectOutputDirectory());
            return forAMavenProjectWithAConfiguredReportDirectory().buildDirectoryFrom(baseDirectory);
        }

        if (isNotEmpty(projectMavenReportingDirectory())) {
            String baseDirectory = Joiner.on(File.separator).skipNulls().join(projectMavenBuildDirectory(), projectMavenReportingDirectory());
            return forAMavenProjectWithAConfiguredReportDirectoryTarget().buildDirectoryFrom(baseDirectory);
        }

        if (isNotEmpty(projectMavenBuildDirectory())) {
            return forAMavenProject().buildDirectoryFrom(projectMavenBuildDirectory());
        }
        return forADefaultMavenConfiguration().buildDirectoryFrom(projectMavenBuildDirectory());
    }

    private boolean isAbsolute(String path) {
        return Paths.get(path).isAbsolute();
    }

    private String projectMavenBuildDirectory() {
        return environmentVariables.getProperty(PROJECT_BUILD_DIRECTORY);
    }

    private String projectMavenReportingDirectory() {
        return environmentVariables.getProperty(PROJECT_REPORTING_OUTPUT_DIRECTORY);
    }

    private String projectOutputDirectory() {
        String thucydidesOutputDurectory = environmentVariables.getProperty(ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY);
        String serenityOutputDurectory = environmentVariables.getProperty(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY);

        return (serenityOutputDurectory != null) ? serenityOutputDurectory : thucydidesOutputDurectory;
    }

    public File resolve(File relativeDirectory) {
        if (isEmpty(projectMavenBuildDirectory())) {
            return relativeDirectory;
        }

        return Paths.get(projectMavenBuildDirectory()).resolve(relativeDirectory.toPath()).toFile();
    }
}
