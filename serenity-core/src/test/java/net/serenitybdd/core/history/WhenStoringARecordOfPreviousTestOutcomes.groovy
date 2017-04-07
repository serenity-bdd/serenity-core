package net.serenitybdd.core.history

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Path

class WhenStoringARecordOfPreviousTestOutcomes extends Specification {

    @Rule
    TemporaryFolder folder = new TemporaryFolder();

    def "test outcomes can be stored in a summarised form for future reference"() {
        given:
            Path sourceDirectoryPath = new File("src/test/resources/sample-big-report").toPath();
            Path historyDirectory = folder.newFolder().toPath();
        and:
            TestOutcomeSummaryRecorder recorder = new TestOutcomeSummaryRecorder(historyDirectory, true)
        when:
            recorder.recordOutcomeSummariesFrom(sourceDirectoryPath)
        then:
            historyDirectory.toFile().listFiles().length > 0
    }
}