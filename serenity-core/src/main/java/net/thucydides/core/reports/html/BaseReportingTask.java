package net.thucydides.core.reports.html;

import net.serenitybdd.core.time.Stopwatch;
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

        Stopwatch stopwatch = Stopwatch.started();

        LOGGER.debug("Generating report in {}", outputFile);

        Path outputPath = outputDirectory.toPath().resolve(outputFile);
        try(BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            mergeTemplate(template).withContext(context).to(writer);
            writer.flush();
        }

        LOGGER.debug("Generated report {} in {} ms", outputFile, stopwatch.stop());
    }

    protected Merger mergeTemplate(final String templateFile) {
        return new Merger(templateFile);
    }


    protected void generateCSVReportFor(TestOutcomes testOutcomes, String reportName) throws IOException {
        CSVReporter csvReporter = new CSVReporter(outputDirectory, environmentVariables);
        csvReporter.generateReportFor(testOutcomes, reportName);
    }
}
