package net.thucydides.core.reports.adaptors

import com.github.goldin.spock.extensions.tempdir.TempDir
import net.thucydides.core.reports.TestOutcomeAdaptorReporter
import net.thucydides.core.reports.adaptors.lettuce.LettuceXUnitAdaptor
import net.thucydides.core.reports.adaptors.xunit.DefaultXUnitAdaptor
import spock.lang.Specification

import static net.thucydides.core.util.TestResources.fileInClasspathCalled

class WhenConvertingXUnitResultsToThucydidesReports extends Specification {

    @TempDir File temporaryDirectory

    def "should convert xUnit files to thucydides TestOutcome xml files"() {
        given:
            def xunitFileDirectory = fileInClasspathCalled("/xunit-sample-output")
            def xunitReporter = new TestOutcomeAdaptorReporter()
            xunitReporter.setOutputDirectory(temporaryDirectory);
        and:
            xunitReporter.registerAdaptor(new DefaultXUnitAdaptor())
        when:
            xunitReporter.generateReportsFrom(xunitFileDirectory)
        then:
            generatedXmlFiles().size() == 3
            generatedHtmlFiles().size() == 3
    }

    def generatedXmlFiles() {
        temporaryDirectory.list(new FilenameFilter() {
            @Override
            boolean accept(File dir, String name) {
                name.endsWith(".xml")
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
    def "should convert xUnit files from lettuce to thucydides TestOutcome xml files"() {
        given:
            def xunitFileDirectory = fileInClasspathCalled("/lettuce-xunit-reports/normal")
            def xunitReporter = new TestOutcomeAdaptorReporter()
            xunitReporter.setOutputDirectory(temporaryDirectory);
        and:
            xunitReporter.registerAdaptor(new LettuceXUnitAdaptor())
        when:
            xunitReporter.generateReportsFrom(xunitFileDirectory)
        then:
            generatedXmlFiles().size() == 4
            generatedHtmlFiles().size() == 4
    }

}