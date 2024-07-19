package net.thucydides.core.reports.adaptors

import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.reports.adaptors.AdaptorService
import net.thucydides.model.reports.adaptors.UnknownAdaptor
import spock.lang.Specification

class WhenFindingAdaptors extends Specification {

    def "should use the basic xUnit adaptor by default"() {
        given:
            def adaptorService = new AdaptorService()
        when:
            def adaptor = adaptorService.getAdaptor(label)
        then:
            adaptor.class.simpleName == className
        where:
            label       | className
            "xunit"     | "DefaultXUnitAdaptor"
            "lettuce"   | "LettuceXUnitAdaptor"
    }

    def "should be able to configure a custom adaptor"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("thucydides.adaptors.myadaptor",
                                             "net.thucydides.core.reports.adaptors.MyAdaptor")
        and:
            def adaptorService = new AdaptorService(environmentVariables)
        when:
            def adaptor = adaptorService.getAdaptor("myadaptor")
        then:
            adaptor.class.simpleName == "MyAdaptor"
    }

    def "should fail gracefully if adaptor does not exist"() {
        given:
            def adaptorService = new AdaptorService()
        when:
            adaptorService.getAdaptor("myadaptor")
        then:
            thrown(UnknownAdaptor)
    }

    def "should fail gracefully if adaptor is not configured correctly"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("thucydides.adaptors.myadaptor",
                    "net.thucydides.core.reports.adaptors.AdaptorDoesNotExist")
        and:
            def adaptorService = new AdaptorService(environmentVariables)
        when:
            adaptorService.getAdaptor("myadaptor")
        then:
            thrown(UnknownAdaptor)
    }

}
