package net.serenitybdd.reports.model

import serenitymodel.net.thucydides.core.ThucydidesSystemProperty
import serenitymodel.net.thucydides.core.util.EnvironmentVariables

class ReportTags(val environmentVariables: EnvironmentVariables) {

    val displayedTagTypes = ThucydidesSystemProperty.REPORT_TAGTYPES.from(environmentVariables)
            .split(",")
            .map { value -> value.trim() }
            .filter { value -> value.isNotEmpty() }
}