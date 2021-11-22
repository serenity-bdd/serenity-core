package net.serenitybdd.reports.configuration

import net.thucydides.core.util.EnvironmentVariables

class ReportTagTypeProperty: ReportProperty<List<String>> {

    override fun configuredIn(environmentVariables: EnvironmentVariables): List<String> {
        return ReportTags(environmentVariables).displayedTagTypes
    }
}