package net.serenitybdd.core.reports.integration

import net.serenitybdd.core.SerenityReports
import net.thucydides.model.configuration.SystemPropertiesConfiguration
import net.thucydides.model.domain.Story
import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification

import java.nio.file.Files

class WhenGeneratingReportsUsingTheReportService extends Specification {

    File outputDir

    def environmentVariables = new MockEnvironmentVariables()
    def configuration = new SystemPropertiesConfiguration(environmentVariables)

    def setup() {
        outputDir = Files.createTempDirectory("reports").toFile()
    }

    def cleanup() {
        outputDir.deleteDir()
    }

    def "should generate reports using each of the subscribed reporters"() {
        given:
            configuration.setOutputDirectory(outputDir)
            SerenityReports.setupListeners(configuration)
            def testOutcomes = [TestOutcome.forTestInStory("some test", Story.called("some story"))]
        when:
            SerenityReports.getReportService(configuration).generateReportsFor(testOutcomes)
            Thread.sleep(1000)
        then:
            outputDir.list().findAll { it.endsWith(".html")}.size() == 1
            outputDir.list().findAll { it.endsWith(".xml") && it.startsWith("SERENITY-JUNIT")}.size() == 1
            outputDir.list().findAll { it.endsWith(".json") && !it.startsWith("manifest")}.size() == 1
    }
}
