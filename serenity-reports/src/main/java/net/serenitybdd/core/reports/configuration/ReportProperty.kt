package net.serenitybdd.reports.configuration

import net.thucydides.core.util.EnvironmentVariables

interface ReportProperty<A> {
    fun configuredIn(environmentVariables: EnvironmentVariables) : A
}