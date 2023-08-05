package net.serenitybdd.reports.configuration

import net.serenitybdd.model.environment.ConfiguredEnvironment
import net.thucydides.model.util.EnvironmentVariables
import java.nio.file.Path
import java.nio.file.Paths

class ConfiguredOutputDirectoryProperty : ReportProperty<Path> {
    override fun configuredIn(environmentVariables: EnvironmentVariables) : Path {
        return ConfiguredEnvironment.getConfiguration().outputDirectory.toPath()
    }
}
