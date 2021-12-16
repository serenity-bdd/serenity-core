package net.serenitybdd.cucumber.suiteslicing;


import net.serenitybdd.core.time.Stopwatch;
import net.thucydides.core.util.Inflector;
import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.serenitybdd.cucumber.suiteslicing.SerenityCSVHeader.*;

public class SingleRunTestStatistics implements TestStatistics {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleRunTestStatistics.class);

    private final List<TestScenarioResult> records;
    private String fileName;
    private String[] headers;


    private SingleRunTestStatistics(String fileName) {
        Stopwatch timer = Stopwatch.started();
        this.fileName = fileName;
        this.headers = new String[]{STORY, TITLE, RESULT, DATE, STABILITY, DURATION};
        this.records = records();
        LOGGER.debug("Loaded {} records from {} in {}", records.size(), fileName, timer.executionTimeFormatted());
    }

    public static TestStatistics fromFileName(String fileName) {
        return new SingleRunTestStatistics(fileName);
    }

    @Override
    public BigDecimal scenarioWeightFor(String feature, String scenario) {
        return records.stream()
            .filter(record -> record.feature.equals(feature) && record.scenario.equals(scenario))
            .map(TestScenarioResult::duration)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("no result found for feature %s and scenario %s", feature, scenario)));
    }

    @Override
    public List<TestScenarioResult> records() {
        try (Reader bufferedReader = new BufferedReader(new InputStreamReader(checkNotNull(getClass().getResourceAsStream(fileName), fileName + " could not be found")))) {
            return CSVFormat.DEFAULT
                .withHeader(headers)
                .withSkipHeaderRecord(true)
                .parse(bufferedReader)
                .getRecords().stream()
                .map(TestScenarioResult::createFromCSV).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(fileName + " could not be opened", e);
        }
    }

    public String toString() {
        return Inflector.getInstance().kebabCase(this.getClass().getSimpleName());
    }

}
