package net.thucydides.core.reports.adaptors

import net.thucydides.core.model.TestOutcome
import net.thucydides.core.reports.TestOutcomeAdaptorReporter
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

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

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        temporaryDirectory = temporaryFolder.newFolder()
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
        generatedHtmlFiles().size() == 2
    }

    def generatedJsonFiles() {
        temporaryDirectory.list(new FilenameFilter() {
            @Override
            boolean accept(File dir, String name) {
                name.endsWith(".json")
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