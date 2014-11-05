package net.thucydides.core.reports.csv;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.ThucydidesReporter;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Stores test outcomes as CSV files
 */
public class CSVReporter extends ThucydidesReporter {
    private static final String[] TITLE_LINE = {"Story", "Title", "Result", "Date", "Stability", "Duration (s)"};
    private static final String[] OF_STRINGS = new String[]{};

    private final List<String> extraColumns;
    private final String encoding;

    public CSVReporter(File outputDirectory) {
        this(outputDirectory, Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    public CSVReporter(File outputDirectory, EnvironmentVariables environmentVariables) {
        this.setOutputDirectory(outputDirectory);
        this.extraColumns = extraColumnsDefinedIn(environmentVariables);
        this.encoding = ThucydidesSystemProperty.THUCYDIDES_REPORT_ENCODING.from(environmentVariables, java.nio.charset.Charset.defaultCharset().name());
    }

    private List<String> extraColumnsDefinedIn(EnvironmentVariables environmentVariables) {
        String columns = ThucydidesSystemProperty.THUCYDIDES_CSV_EXTRA_COLUMNS.from(environmentVariables,"");
        return ImmutableList.copyOf(Splitter.on(",").omitEmptyStrings().trimResults().split(columns));
    }

    public File generateReportFor(TestOutcomes testOutcomes, String reportName) throws IOException {
        CSVWriter writer = new CSVWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(getOutputFile(reportName)), encoding));
        writeTitleRow(writer);
        writeEachRow(testOutcomes.withHistory(), writer);
        writer.close();
        return getOutputFile(reportName);
    }

    private void writeTitleRow(CSVWriter writer) {
        Inflector inflector = Inflector.getInstance();
        List<String> titles = new ArrayList<String>();
        titles.addAll(Arrays.asList(TITLE_LINE));
        for(String extraColumn : extraColumns) {
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
        List<? extends Serializable> defaultValues = ImmutableList.of(outcome.getStoryTitle(),
                                                                      outcome.getTitle(),
                                                                      outcome.getResult(),
                                                                      outcome.getStartTime(),
                                                                      passRateFor(outcome),
                                                                      outcome.getDurationInSeconds());
        List<String> cellValues = extract(defaultValues, on(Object.class).toString());
        cellValues.addAll(extraValuesFrom(outcome));
        return cellValues.toArray(OF_STRINGS);
    }

    private Collection<String> extraValuesFrom(TestOutcome outcome) {
        List<String> extraValues = Lists.newArrayList();

        for(String extraColumn : extraColumns) {
            extraValues.add(outcome.getTagValue(extraColumn).or(""));
        }
        return extraValues;
    }

    private File getOutputFile(String reportName) {
        return new File(getOutputDirectory(), reportName);
    }
}
