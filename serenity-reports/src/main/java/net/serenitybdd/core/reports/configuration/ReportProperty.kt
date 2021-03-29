package net.serenitybdd.core.reports.configuration

import serenitymodel.net.thucydides.core.util.EnvironmentVariables

interface ReportProperty<A> {
    fun configuredIn(environmentVariables: EnvironmentVariables) : A
}