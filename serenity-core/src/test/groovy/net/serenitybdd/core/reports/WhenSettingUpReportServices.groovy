package net.serenitybdd.core.reports

import net.serenitybdd.core.SerenityReports
import net.thucydides.model.configuration.SystemPropertiesConfiguration
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.reports.NumberOfThreads
import net.thucydides.model.reports.ReporterRuntime
import net.thucydides.model.util.EnvironmentVariables
import spock.lang.Specification

class WhenSettingUpReportServices extends Specification {

    def environmentVariables = new MockEnvironmentVariables()
    def configuration = new SystemPropertiesConfiguration(environmentVariables)

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

    def "should be able to configure the thread coefficient"() {
        given:
            EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
        when:
            environmentVariables.setProperty("io.blocking.coefficient", "0.1")
        then:
            new NumberOfThreads().forIO() != new NumberOfThreads(environmentVariables).forIO()

    }


    def "should work correctly for virtual environments with 1 cpu"() {
        given:
            EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
            ReporterRuntime singleCPURuntime = new ReporterRuntime() {
                @Override
                int availableProcessors() {
                    return 1
                }
            }
        when:
            int numberOfThreads = new NumberOfThreads(environmentVariables, singleCPURuntime).forIO()
        then:
            numberOfThreads == 2

    }

    def "should be able to configure the thread count"() {
        given:
            EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
        when:
            environmentVariables.setProperty("report.threads", "16")
        then:
            new NumberOfThreads(environmentVariables).forIO() == 16

    }

}
