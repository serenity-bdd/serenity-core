package net.serenitybdd.reports.model

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration
import net.thucydides.model.ThucydidesSystemProperty.REPORT_TAGTYPES
import net.thucydides.model.ThucydidesSystemProperty.SERENITY_REPORT_TAGTYPES
import net.thucydides.model.util.EnvironmentVariables

class ReportTags(val environmentVariables: EnvironmentVariables) {

    val displayedTagTypes = EnvironmentSpecificConfiguration.from(environmentVariables)
                                    .getOptionalProperty(REPORT_TAGTYPES, SERENITY_REPORT_TAGTYPES)
                                    .orElse("feature")
                                    .split(",")
                                    .map { value -> value.trim() }
                                    .filter { value -> value.isNotEmpty() }
}
