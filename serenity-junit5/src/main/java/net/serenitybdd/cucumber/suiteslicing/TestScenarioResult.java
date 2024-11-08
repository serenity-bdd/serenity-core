package net.serenitybdd.cucumber.suiteslicing;

import org.apache.commons.csv.CSVRecord;

import java.math.BigDecimal;

import static net.serenitybdd.cucumber.suiteslicing.SerenityCSVHeader.*;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;


public class TestScenarioResult {

    public final String feature;
    public final String scenario;
    public final BigDecimal duration;
    public final String scenarioKey;

    public static TestScenarioResult createFromCSV(CSVRecord csvRecord) {
        return new TestScenarioResult(
            csvRecord.get(STORY),
            csvRecord.get(TITLE),
            new BigDecimal(csvRecord.get(DURATION)));
    }

    public BigDecimal duration() {
        return duration;
    }

    public TestScenarioResult(String feature, String scenario, BigDecimal duration) {
        this.feature = feature;
        this.scenario = scenario;
        this.scenarioKey = feature + ":" + scenario;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
