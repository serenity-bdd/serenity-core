package net.serenitybdd.reports.email.model

import net.thucydides.core.util.EnvironmentVariables

class ReportTags(val environmentVariables: EnvironmentVariables) {

    companion object {
        const val REPORTED_TAG_TYPES = "report.tagtypes"
    }

    val displayedTagTypes = environmentVariables.getProperty(REPORTED_TAG_TYPES, "")
            .split(",")
            .map { value -> value.trim() }
            .filter { value -> value.isNotEmpty() }
}