package net.thucydides.core.reports.html;

import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.csv.CSVReporter;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_GENERATE_CSV_REPORTS;

public abstract class BaseReportingTask implements ReportingTask {

    final FreemarkerContext freemarker;
    final EnvironmentVariables environmentVariables;
    final File outputDirectory;

    protected static final Logger LOGGER = LoggerFactory.getLogger(ReportingTask.class);

    public BaseReportingTask(FreemarkerContext freemarker,
                             EnvironmentVariables environmentVariables,
                             File outputDirectory) {
        this.freemarker = freemarker;
        this.environmentVariables = environmentVariables;
        this.outputDirectory = outputDirectory;
    }

    public abstract void generateReports() throws IOException;

    protected void generateReportPage(final Map<String, Object> context,
                                      final String template,
                                      final String outputFile) throws IOException {

        Path outputPath = outputDirectory.toPath().resolve(outputFile);
        long start = System.currentTimeMillis();
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            mergeTemplate(template).withContext(context).to(writer);
        }
    }

    protected Merger mergeTemplate(final String templateFile) {
        return new Merger(templateFile);
    }


    protected Optional<String> generateCSVReportFor(TestOutcomes testOutcomes, String reportName) throws IOException {

        if (csvReportsAreActivated()) {
            CSVReporter csvReporter = new CSVReporter(outputDirectory, environmentVariables);
            csvReporter.generateReportFor(testOutcomes, reportName);
            return Optional.of(reportName);
        }

        return Optional.empty();
    }

    private boolean csvReportsAreActivated() {
        return SERENITY_GENERATE_CSV_REPORTS.booleanFrom(environmentVariables, true);
    }

}
