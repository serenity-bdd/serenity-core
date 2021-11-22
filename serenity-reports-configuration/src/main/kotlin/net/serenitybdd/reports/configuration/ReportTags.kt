package net.serenitybdd.reports.configuration

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.util.EnvironmentVariables

class ReportTags(val environmentVariables: EnvironmentVariables) {

    val displayedTagTypes = EnvironmentSpecificConfiguration.from(environmentVariables)
                                    .getOptionalProperty(ThucydidesSystemProperty.REPORT_TAGTYPES, ThucydidesSystemProperty.SERENITY_REPORT_TAGTYPES)
                                    .orElse("feature")
                                    .split(",")
                                    .map { value -> value.trim() }
                                    .filter { value -> value.isNotEmpty() }
}