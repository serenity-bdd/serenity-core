package net.serenitybdd.core.eventbus

import net.serenitybdd.core.guice.injected
import net.serenitybdd.core.reports.json.JSONTestOutcomeReportWriter
import net.serenitybdd.core.reports.json.JUnitReportWriter
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.webdriver.SystemPropertiesConfiguration

object Subscribers {

    var environmentVariables = injected(EnvironmentVariables::class.java)
    var systemConfiguration = injected(SystemPropertiesConfiguration::class.java)

    fun configuredByDefault(): List<Any> {
        val outputDirectory = systemConfiguration.outputDirectory.toPath()

        return listOf(
                JSONTestOutcomeReportWriter(outputDirectory, environmentVariables),
                JUnitReportWriter(outputDirectory, environmentVariables)
        );
    }
}