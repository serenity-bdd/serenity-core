package net.thucydides.core.reports.adaptors

import net.thucydides.core.reports.TestOutcomeAdaptorReporter
import net.thucydides.core.reports.adaptors.lettuce.LettuceXUnitAdaptor
import net.thucydides.core.reports.adaptors.xunit.DefaultXUnitAdaptor
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files

import static net.thucydides.core.util.TestResources.fileInClasspathCalled

class WhenConvertingXUnitResultsToThucydidesReports extends Specification {

   File temporaryDirectory

    def setup() {
        temporaryDirectory = Files.createTempDirectory("tmp").toFile()
        temporaryDirectory.deleteOnExit();
    }

    def "should convert xUnit files to thucydides TestOutcome json files"() {
        given:
            def xunitFileDirectory = fileInClasspathCalled("/xunit-sample-output")
            def xunitReporter = new TestOutcomeAdaptorReporter()
            xunitReporter.setOutputDirectory(temporaryDirectory);
        and:
            xunitReporter.registerAdaptor(new DefaultXUnitAdaptor())
        when:
            xunitReporter.generateReportsFrom(xunitFileDirectory)
        then:
            generatedJsonFiles().size() == 3
    }

    def generatedJsonFiles() {
        temporaryDirectory.list(new FilenameFilter() {
            @Override
            boolean accept(File dir, String name) {
                name.endsWith(".json") && !name.startsWith("manifest")
            }
        })
    }

   def generatedHtmlFiles() {
        temporaryDirectory.list(new FilenameFilter() {
            @Override
            boolean accept(File dir, String name) {
                name.endsWith(".html")
            }
        })
    }
    def "should convert xUnit files from lettuce to thucydides TestOutcome json files"() {
        given:
            def xunitFileDirectory = fileInClasspathCalled("/lettuce-xunit-reports/normal")
            def xunitReporter = new TestOutcomeAdaptorReporter()
            xunitReporter.setOutputDirectory(temporaryDirectory);
        and:
            xunitReporter.registerAdaptor(new LettuceXUnitAdaptor())
        when:
            xunitReporter.generateReportsFrom(xunitFileDirectory)
        then:
            generatedJsonFiles().size() == 4
    }

}
