package net.serenitybdd.core.reports

import net.serenitybdd.core.SerenityReports
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import spock.lang.Specification

class WhenSettingUpReportServices extends Specification {

    def environmentVariables = new MockEnvironmentVariables();
    def configuration = new SystemPropertiesConfiguration(environmentVariables);

    def "should be able to configure default report services"() {
        when:
            def reportService = SerenityReports.getReportService(configuration)
        then:
            reportService.getSubscribedReporters().size() != 0
    }

    def "should be able to configure default listeners"() {
        when:
            def listeners = SerenityReports.setupListeners(configuration)
        then:
            listeners.baseStepListener != null
    }

    def "should be able to obtain the results from the base step listener"() {
        when:
            def listeners = SerenityReports.setupListeners(configuration)
        then:
            listeners.getResults() == listeners.baseStepListener.testOutcomes
    }
}
