package net.serenitybdd.model.history;

import java.nio.file.Path;
import java.util.List;

public interface TestOutcomeSummaryRecorder {
    void recordOutcomeSummariesFrom(Path sourceDirectory);

    List<PreviousTestOutcome> loadSummaries();
}
