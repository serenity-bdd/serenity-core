package net.serenitybdd.reports.model

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import net.thucydides.core.ThucydidesSystemProperty.REPORT_TAGTYPES
import net.thucydides.core.ThucydidesSystemProperty.SERENITY_REPORT_TAGTYPES
import net.thucydides.core.util.EnvironmentVariables

class ReportTags(val environmentVariables: EnvironmentVariables) {

    val displayedTagTypes = EnvironmentSpecificConfiguration.from(environmentVariables)
                                    .getOptionalProperty(REPORT_TAGTYPES, SERENITY_REPORT_TAGTYPES)
                                    .orElse("feature")
                                    .split(",")
                                    .map { value -> value.trim() }
                                    .filter { value -> value.isNotEmpty() }
}