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

    public abstract void generateReportsFor(TestOutcomes testOutcomes) throws IOException;

    protected void generateReportPage(final Map<String, Object> context,
                                    final String template,
                                    final String outputFile) throws IOException {
        writeReportToOutputDirectory(outputFile,
                                     mergeTemplate(template).usingContext(context));
    }

    protected Merger mergeTemplate(final String templateFile) {
        return new Merger(templateFile);
    }


    protected void generateCSVReportFor(TestOutcomes testOutcomes, String reportName) throws IOException {
        CSVReporter csvReporter = new CSVReporter(outputDirectory, environmentVariables);
        csvReporter.generateReportFor(testOutcomes, reportName);
    }

    protected File writeReportToOutputDirectory(final String reportFilename, final String htmlContents) throws
            IOException {
        File report = new File(outputDirectory, reportFilename);
        writeToFile(htmlContents, report);
        return report;
    }

    private void writeToFile(String htmlContents, File report) throws IOException {
        String lines[] = htmlContents.split("\\r?\\n");
        try (BufferedWriter writer = Files.newBufferedWriter(report.toPath(), StandardCharsets.UTF_8)){
            for(String line : lines){
                writer.write(line);
                writer.newLine();
            }
        }
    }


}
