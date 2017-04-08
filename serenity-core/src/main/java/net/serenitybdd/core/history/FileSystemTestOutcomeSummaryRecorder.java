package net.serenitybdd.core.history;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestLoader;
import net.thucydides.core.reports.json.JSONTestOutcomeReporter;
import net.thucydides.core.reports.json.gson.GsonPreviousOutcomeConverter;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_HISTORY_DIRECTORY;

public class FileSystemTestOutcomeSummaryRecorder implements TestOutcomeSummaryRecorder {

    private final Path historyDirectory;
    private final Boolean deletePreviousHistory;
    private final AcceptanceTestLoader testOutcomeReporter = new JSONTestOutcomeReporter();
    private final GsonPreviousOutcomeConverter previousOutcomeConverter;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemTestOutcomeSummaryRecorder.class);

    private static Map<Boolean, PrepareHistoryDirectory> DELETE_STRATEGY =
            ImmutableMap.of(
                    false, new LeaveDirectoryContents(),
                    true, new ClearDirectoryContents()
            );


    @Inject
    public FileSystemTestOutcomeSummaryRecorder(EnvironmentVariables environmentVariables) {
        this(Paths.get(SERENITY_HISTORY_DIRECTORY.from(environmentVariables, "history"),""), false);
    }

    /**
     * Used mainly from Maven
     */
    public FileSystemTestOutcomeSummaryRecorder(Path historyDirectory, Boolean deletePreviousHistory) {
        this.historyDirectory = historyDirectory;
        this.deletePreviousHistory = deletePreviousHistory;
        previousOutcomeConverter = new GsonPreviousOutcomeConverter(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    @Override
    public void recordOutcomeSummariesFrom(Path sourceDirectory) {

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourceDirectory)) {

            usingDeleteStrategyFor(deletePreviousHistory).prepareHistoryDirectory(historyDirectory);

            for (Path path : directoryStream) {
                storeOutcomesFrom(testOutcomeReporter.loadReportFrom(path).asSet());
            }
        } catch (IOException ex) {
            LOGGER.warn("Unable to store test outcome for posterity", ex);
        }
    }

    private void storeOutcomesFrom(Set<TestOutcome> testOutcomes) throws IOException {
        for (TestOutcome testOutcome : testOutcomes) {
            PreviousTestOutcome summary = PreviousTestOutcome.from(testOutcome);
            File summaryFile = summaryFileFor(testOutcome);

            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(summaryFile))) {
                previousOutcomeConverter.toJson(summary, outputStream);
                outputStream.flush();
            }
        }
    }

    @Override
    public List<PreviousTestOutcome> loadSummaries() {
        List<PreviousTestOutcome> previousTestOutcomes = new ArrayList<>();

        if (historyDirectory.toFile().exists()) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(historyDirectory)) {
                for (Path path : directoryStream) {
                    previousTestOutcomes.addAll(previousTestOutcomesFrom(path).asSet());
                }
            } catch (IOException ex) {
                LOGGER.warn("Unable to store test outcome for posterity", ex);
            }
        }

        return previousTestOutcomes;
    }

    private Optional<PreviousTestOutcome> previousTestOutcomesFrom(Path source) {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(source.toFile()))) {
            return previousOutcomeConverter.fromJson(inputStream);
        } catch (IOException e) {
            return Optional.absent();
        }
    }

    private PrepareHistoryDirectory usingDeleteStrategyFor(Boolean deletePreviousHistory) {
        return DELETE_STRATEGY.get(deletePreviousHistory);
    }

    private File summaryFileFor(TestOutcome testOutcome) {
        String summaryFilename = ReportNamer.forReportType(ReportType.JSON).withPrefix("summary-").getNormalizedTestNameFor(testOutcome);
        return historyDirectory.resolve(summaryFilename).toFile();
    }

}
