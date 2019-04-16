package net.serenitybdd.core.history

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Path

class WhenStoringAndReloadingARecordOfPreviousTestOutcomes extends Specification {

    @Rule
    TemporaryFolder folder = new TemporaryFolder();

    def "test outcome summaries can be reloaded after being stored"() {
        given:
            Path sourceDirectoryPath = new File("src/test/resources/sample-big-report").toPath();
            Path historyDirectory = folder.newFolder().toPath();
            FileSystemTestOutcomeSummaryRecorder recorder = new FileSystemTestOutcomeSummaryRecorder(historyDirectory, true)
            recorder.recordOutcomeSummariesFrom(sourceDirectoryPath)
        when:
            List<PreviousTestOutcome> outcomes = recorder.loadSummaries();
        then:
            outcomes.size() == 16

    }
}