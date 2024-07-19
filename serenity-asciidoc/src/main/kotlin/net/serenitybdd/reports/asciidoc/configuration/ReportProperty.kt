package net.serenitybdd.reports.asciidoc.configuration

import net.thucydides.model.util.EnvironmentVariables

interface ReportProperty<A> {
    fun configuredIn(environmentVariables: EnvironmentVariables) : A
}
