package net.thucydides.core.reports.adaptors

import net.thucydides.core.model.TestOutcome
import net.thucydides.core.reports.TestOutcomeAdaptorReporter
import spock.lang.Specification

import java.nio.file.Files

class WhenConvertingNonFilebasedResultsToThucydidesReports extends Specification {


    class MyDatabaseAdaptor implements TestOutcomeAdaptor {
        @Override
        List<TestOutcome> loadOutcomes() throws IOException {
            return [new TestOutcome("some method"), new TestOutcome("some other method")]
        }

        @Override
        List<TestOutcome> loadOutcomesFrom(File source) throws IOException {
            throw IllegalArgumentException("This method should not be called")
        }
    }


    File temporaryDirectory

    def setup() {
        temporaryDirectory = Files.createTempDirectory("tmp").toFile();
        temporaryDirectory.deleteOnExit();
    }

    def "should convert non-file-based data to Thucydides TestOutcome xml files"() {
        given:
            def reporter = new TestOutcomeAdaptorReporter()
            reporter.setOutputDirectory(temporaryDirectory);
        and:
            reporter.registerAdaptor(new MyDatabaseAdaptor())
        when:
            reporter.generateReports()
        then:
        generatedJsonFiles().size() == 2
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
}
