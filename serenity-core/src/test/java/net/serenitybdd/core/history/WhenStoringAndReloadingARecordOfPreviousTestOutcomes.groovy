package net.serenitybdd.core.history

import net.serenitybdd.model.history.FileSystemTestOutcomeSummaryRecorder
import net.serenitybdd.model.history.PreviousTestOutcome
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class WhenStoringAndReloadingARecordOfPreviousTestOutcomes extends Specification {

    def "test outcome summaries can be reloaded after being stored"() {
        given:
            Path sourceDirectoryPath = new File("src/test/resources/sample-big-report").toPath()
            Path historyDirectory = Files.createTempDirectory("tmp")
        FileSystemTestOutcomeSummaryRecorder recorder = new FileSystemTestOutcomeSummaryRecorder(historyDirectory, true)
            recorder.recordOutcomeSummariesFrom(sourceDirectoryPath)
        when:
            List<PreviousTestOutcome> outcomes = recorder.loadSummaries()
        then:
            outcomes.size() == 16
    }
}
