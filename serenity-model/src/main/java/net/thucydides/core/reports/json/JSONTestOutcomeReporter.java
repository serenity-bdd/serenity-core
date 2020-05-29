package net.thucydides.core.reports.json;

import com.google.common.base.Preconditions;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestLoader;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.OutcomeFormat;
import net.thucydides.core.reports.io.SafelyMoveFiles;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class JSONTestOutcomeReporter implements AcceptanceTestReporter, AcceptanceTestLoader {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(JSONTestOutcomeReporter.class);

    private File outputDirectory;

    private transient String qualifier;

    private final EnvironmentVariables environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();

    private final String encoding;

    @Override
    public String getName() {
        return "json";
    }

    JSONConverter jsonConverter;

    public JSONTestOutcomeReporter() {
        encoding = ThucydidesSystemProperty.THUCYDIDES_REPORT_ENCODING.from(environmentVariables, StandardCharsets.UTF_8.name());
        jsonConverter = Injectors.getInjector().getInstance(JSONConverter.class);
    }

    @Override
    public File generateReportFor(TestOutcome testOutcome) throws IOException {
        TestOutcome storedTestOutcome = testOutcome.withQualifier(qualifier);
        Preconditions.checkNotNull(outputDirectory);
        String reportFilename = reportFor(storedTestOutcome);
        String unique = UUID.randomUUID().toString();
        File temporary = new File(getOutputDirectory(), reportFilename.concat(unique));
        File report = new File(getOutputDirectory(), reportFilename);
        report.createNewFile();

        LOGGER.debug("Generating JSON report for {} to file {} (using temp file {})", testOutcome.getTitle(), report.getAbsolutePath(), temporary.getAbsolutePath());

        try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(temporary))){
            jsonConverter.toJson(storedTestOutcome, outputStream);
            outputStream.flush();
        }

        SafelyMoveFiles.withMaxRetriesOf(3).from(temporary.toPath()).to(report.toPath());

        return report;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    private String reportFor(final TestOutcome testOutcome) {
        return testOutcome.withQualifier(qualifier).getReportName(ReportType.JSON);
    }

    @Override
    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    public void setResourceDirectory(String resourceDirectoryPath) {
    }

    @Override
    public Optional<TestOutcome> loadReportFrom(final Path reportFile) {
        return loadReportFrom(reportFile.toFile());
    }

    @Override
    public Optional<TestOutcome> loadReportFrom(final File reportFile) {
        if (!reportFile.getName().toLowerCase().endsWith(".json")) {
            return Optional.empty();
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(reportFile), encoding))) {
            return jsonConverter.fromJson(in);
        } catch (Throwable e) {
            LOGGER.debug("This file was not a valid JSON Serenity test report: " + reportFile.getName()
                    + System.lineSeparator() + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<TestOutcome> loadReportsFrom(final Path outputDirectory) {
        return loadReportsFrom(outputDirectory.toFile());
    }

    @Override
    public List<TestOutcome> loadReportsFrom(File outputDirectory) {
        File[] reportFiles = getAllJsonFilesFrom(outputDirectory);
        List<TestOutcome> testOutcomes = new ArrayList<>();
        if (reportFiles != null) {
            for (File reportFile : reportFiles) {
                testOutcomes.addAll(loadReportFrom(reportFile).map(Collections::singleton).orElse(Collections.emptySet()));
            }
        }
        return testOutcomes;
    }

    private File[] getAllJsonFilesFrom(final File reportsDirectory) {
        return reportsDirectory.listFiles(new JsonFilenameFilter());
    }

    private static final class JsonFilenameFilter implements FilenameFilter {
        public boolean accept(final File file, final String filename) {
            return filename.toLowerCase(Locale.getDefault()).endsWith(".json");
        }
    }

    @Override
    public Optional<OutcomeFormat> getFormat() {
        return Optional.of(OutcomeFormat.JSON);
    }
}