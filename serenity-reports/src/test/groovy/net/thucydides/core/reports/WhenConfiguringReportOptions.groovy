package net.thucydides.core.reports

import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.reports.ReportOptions
import spock.lang.Specification

class WhenConfiguringReportOptions extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    def "should let users specify whether step details are to be displayed in test result tables"() {

        when:
            environmentVariables.setProperty("thucydides.reports.show.step.details", "true")
            def reportOptions = new ReportOptions(environmentVariables)
        then:
            reportOptions.showStepDetails
    }

    def "Step details are deactivated by default"() {

        when:
            def reportOptions = new ReportOptions(environmentVariables)
        then:
            !reportOptions.showStepDetails
    }
}
