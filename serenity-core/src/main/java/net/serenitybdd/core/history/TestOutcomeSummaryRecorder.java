package net.serenitybdd.core.history;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestLoader;
import net.thucydides.core.reports.json.JSONTestOutcomeReporter;
import net.thucydides.core.reports.json.gson.GsonPreviousOutcomeConverter;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

class TestOutcomeSummaryRecorder {

    private final Path historyDirectory;
    private final Boolean deletePreviousHistory;
    private final AcceptanceTestLoader testOutcomeReporter = new JSONTestOutcomeReporter();
    private final GsonPreviousOutcomeConverter previousOutcomeConverter;
    private static final Logger LOGGER = LoggerFactory.getLogger(TestOutcomeSummaryRecorder.class);

    public TestOutcomeSummaryRecorder(Path historyDirectory, Boolean deletePreviousHistory) {
        this.historyDirectory = historyDirectory;
        this.deletePreviousHistory = deletePreviousHistory;
        previousOutcomeConverter = new GsonPreviousOutcomeConverter(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public void recordOutcomeSummariesFrom(Path sourceDirectory) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourceDirectory)) {
            for (Path path : directoryStream) {
                storeOutcomesIn(testOutcomeReporter.loadReportFrom(path).asSet());
            }
        } catch (IOException ex) {
            LOGGER.warn("Unable to store test outcome for posterity", ex);
        }
    }

    private void storeOutcomesIn(Set<TestOutcome> testOutcomes) throws IOException {
        for(TestOutcome testOutcome : testOutcomes) {
            PreviousTestOutcome summary = PreviousTestOutcome.from(testOutcome);
//            try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(temporary))){
//                previousOutcomeConverter.toJson(summary, outputStream);
//                outputStream.flush();
//            }
        }
    }

}
