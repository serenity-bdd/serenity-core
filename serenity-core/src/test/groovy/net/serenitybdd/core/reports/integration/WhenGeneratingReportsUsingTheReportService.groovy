package net.serenitybdd.core.reports.integration

import net.serenitybdd.core.SerenityReports
import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import spock.lang.Specification

import java.nio.file.Files

class WhenGeneratingReportsUsingTheReportService extends Specification {

    File outputDir;

    def environmentVariables = new MockEnvironmentVariables();
    def configuration = new SystemPropertiesConfiguration(environmentVariables);

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
            outputDir.list().findAll { it.endsWith(".xml")}.size() == 1
            outputDir.list().findAll { it.endsWith(".json")}.size() == 1
    }
}
