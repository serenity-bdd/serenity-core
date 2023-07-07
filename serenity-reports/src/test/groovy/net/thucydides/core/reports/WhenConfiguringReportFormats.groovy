package net.thucydides.core.reports

import net.thucydides.core.environment.MockEnvironmentVariables
import spock.lang.Specification

/**
 * Created by john on 28/05/2014.
 */
class WhenConfiguringReportFormats extends Specification {

    def environmentVars = new MockEnvironmentVariables()

    def "should produce JSON by default"() {
        given:
            FormatConfiguration formatConfiguration = new FormatConfiguration(environmentVars)
        when:
            List<String> formats = formatConfiguration.getFormats()
        then:
            formats == [OutcomeFormat.JSON]
    }

    def "should be able to define a unique format"() {
        given:
        environmentVars.setProperty("output.formats","xml")
        FormatConfiguration formatConfiguration = new FormatConfiguration(environmentVars)
        when:
        List<String> formats = formatConfiguration.getFormats()
        then:
        formats == [OutcomeFormat.XML]
    }


    def "should ignore case"() {
        given:
        environmentVars.setProperty("output.formats","xMl")
        FormatConfiguration formatConfiguration = new FormatConfiguration(environmentVars)
        when:
        List<String> formats = formatConfiguration.getFormats()
        then:
        formats == [OutcomeFormat.XML]
    }

    def "should ignore unknown formats"() {
        given:
        environmentVars.setProperty("output.formats","XML, UNKNOWN")
        FormatConfiguration formatConfiguration = new FormatConfiguration(environmentVars)
        when:
        List<String> formats = formatConfiguration.getFormats()
        then:
        formats == [OutcomeFormat.XML]
    }


    def "should use default if format is empty"() {
        given:
        environmentVars.setProperty("output.formats","")
        FormatConfiguration formatConfiguration = new FormatConfiguration(environmentVars)
        when:
        List<String> formats = formatConfiguration.getFormats()
        then:
        formats == [OutcomeFormat.JSON]
    }

    def "should use first in list as the preferred format for imports"() {
        when:
        environmentVars.setProperty("output.formats","xml, json")
        FormatConfiguration formatConfiguration = new FormatConfiguration(environmentVars)
        then:
        formatConfiguration.preferredFormat == OutcomeFormat.XML
    }

    def "should throw exception if no import formats are defined"() {
        given:
            environmentVars.setProperty("output.formats","UNKNOWN")
            FormatConfiguration formatConfiguration = new FormatConfiguration(environmentVars)
        when:
            formatConfiguration.preferredFormat
        then:
            thrown(IllegalArgumentException)
    }
}
