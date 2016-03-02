package net.thucydides.core.reports.json;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestLoader;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.OutcomeFormat;
import net.thucydides.core.reports.TestOutcomes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class JSONTestOutcomeReporter implements AcceptanceTestReporter, AcceptanceTestLoader {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(JSONTestOutcomeReporter.class);

    private File outputDirectory;

    private transient String qualifier;

    @Override
    public String getName() {
        return "json";
    }

    JSONConverter jsonConverter;

    public JSONTestOutcomeReporter() {
        jsonConverter = Injectors.getInjector().getInstance(JSONConverter.class);
    }

    @Override
    public File generateReportFor(TestOutcome testOutcome,
                                  TestOutcomes allTestOutcomes) throws IOException {
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
        Files.move(temporary.toPath(), report.toPath(),
                StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE
        );
        return report;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    private String reportFor(final TestOutcome testOutcome) {
        return testOutcome.withQualifier(qualifier).getReportName(
                ReportType.JSON);
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
        try (BufferedReader in = new BufferedReader(new FileReader(reportFile))) {
            TestOutcome fromJson = jsonConverter.fromJson(in);
            return Optional.fromNullable(fromJson);
        } catch (Throwable e) {
            LOGGER.warn("This file was not a valid JSON Serenity test report: " + reportFile.getName()
                    + System.lineSeparator() + e.getMessage());
            return Optional.absent();
        }
    }

    @Override
    public List<TestOutcome> loadReportsFrom(final Path outputDirectory) {
        return loadReportsFrom(outputDirectory.toFile());
    }

    @Override
    public List<TestOutcome> loadReportsFrom(File outputDirectory) {
        File[] reportFiles = getAllJsonFilesFrom(outputDirectory);
        List<TestOutcome> testOutcomes = Lists.newArrayList();
        if (reportFiles != null) {
            for (File reportFile : reportFiles) {
                testOutcomes.addAll(loadReportFrom(reportFile).asSet());
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