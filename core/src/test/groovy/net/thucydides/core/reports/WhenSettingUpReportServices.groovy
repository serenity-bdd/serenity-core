package net.thucydides.core.reports

import net.thucydides.core.ThucydidesReports
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.SystemPropertiesConfiguration
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class WhenSettingUpReportServices extends Specification {

    def environmentVariables = new MockEnvironmentVariables();
    def configuration = new SystemPropertiesConfiguration(environmentVariables);

    def "should be able to configure default report services"() {
        when:
            def reportService = ThucydidesReports.getReportService(configuration)
        then:
            reportService.getSubscribedReporters().size() != 0
    }

    def "should be able to configure default listeners"() {
        when:
            def listeners = ThucydidesReports.setupListeners(configuration)
        then:
            listeners.baseStepListener != null
    }

    def "should be able to configure default listeners with a webdriver instance"() {
        when:
            def driver = Mock(WebDriver)
            def listeners = ThucydidesReports.setupListeners(configuration).withDriver(driver)
        then:
            listeners.baseStepListener.driver == driver
    }

    def "should be able to obtain the results from the base step listener"() {
        when:
            def listeners = ThucydidesReports.setupListeners(configuration)
        then:
            listeners.getResults() == listeners.baseStepListener.testOutcomes
    }
}
