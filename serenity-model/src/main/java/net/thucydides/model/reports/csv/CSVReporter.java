package net.thucydides.model.reports.csv;

import com.google.common.base.Splitter;
import com.opencsv.CSVWriter;
import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.reports.ThucydidesReporter;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.util.Inflector;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Stores test outcomes as CSV files
 */
public class CSVReporter extends ThucydidesReporter {
    private static final String[] TITLE_LINE = {"Story", "Title", "Result", "Date", "Stability", "Duration (s)"};
    private static final String[] OF_STRINGS = new String[]{};

    private final List<String> extraColumns;
    private final String encoding;

    public CSVReporter(File outputDirectory) {
        this(outputDirectory, SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    public CSVReporter(File outputDirectory, EnvironmentVariables environmentVariables) {
        this.setOutputDirectory(outputDirectory);
        this.extraColumns = extraColumnsDefinedIn(environmentVariables);
        this.encoding = ThucydidesSystemProperty.SERENITY_REPORT_ENCODING.from(environmentVariables, StandardCharsets.UTF_8.name());
    }

    private List<String> extraColumnsDefinedIn(EnvironmentVariables environmentVariables) {
        String columns = ThucydidesSystemProperty.SERENITY_CSV_EXTRA_COLUMNS.from(environmentVariables, "");
        return Splitter.on(",").omitEmptyStrings().trimResults().splitToList(columns);
    }

    public File generateReportFor(TestOutcomes testOutcomes, String reportName) throws IOException {
        try (CSVWriter writer = new CSVWriter(
                new java.io.OutputStreamWriter(
                        new java.io.FileOutputStream(getOutputFile(reportName)), encoding))) {
            writeTitleRow(writer);
            writeEachRow(testOutcomes, writer);
        }
        return getOutputFile(reportName);
    }

    private void writeTitleRow(CSVWriter writer) {
        Inflector inflector = Inflector.getInstance();
        List<String> titles = new ArrayList<>();
        titles.addAll(Arrays.asList(TITLE_LINE));
        for (String extraColumn : extraColumns) {
            titles.add(inflector.of(extraColumn).asATitle().toString());
        }
        writer.writeNext(titles.toArray(OF_STRINGS));
    }

    private void writeEachRow(TestOutcomes testOutcomes, CSVWriter writer) {
        for (TestOutcome outcome : testOutcomes.getTests()) {
            writer.writeNext(withRowDataFrom(outcome));
        }
    }

    private Double passRateFor(TestOutcome outcome) {
        return 0.0;//outcome.getStatistics().getPassRate().overTheLast(5).testRuns();
    }

    private String[] withRowDataFrom(TestOutcome outcome) {
        List<? extends Serializable> defaultValues = NewList.of(
                blankIfNull(outcome.getStoryTitle()),
                blankIfNull(outcome.getTitle()),
                outcome.getResult(),
                blankIfNull(outcome.getTimestamp(DateTimeFormatter.ISO_ZONED_DATE_TIME)),
                passRateFor(outcome),
                outcome.getDurationInSeconds());
        List<String> cellValues =
                defaultValues.stream().map(Object::toString).collect(Collectors.toList());
        cellValues.addAll(extraValuesFrom(outcome));
        return cellValues.toArray(OF_STRINGS);
    }

    private String blankIfNull(String value) {
        return Optional.ofNullable(value).orElse("");
    }

    private Collection<String> extraValuesFrom(TestOutcome outcome) {
        List<String> extraValues = new ArrayList<>();

        for (String extraColumn : extraColumns) {
            extraValues.add(outcome.getTagValue(extraColumn).orElse(""));
        }
        return extraValues;
    }

    private File getOutputFile(String reportName) {
        return new File(getOutputDirectory(), reportName);
    }
}
