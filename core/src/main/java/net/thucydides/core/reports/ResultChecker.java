package net.thucydides.core.reports;

import com.google.common.base.Optional;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestType;

import java.io.File;
import java.io.IOException;

/**
 * Created by john on 22/09/2014.
 */
public class ResultChecker {

    private final File outputDirectory;

    public ResultChecker(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void checkTestResults() {
        Optional<TestOutcomes> outcomes = loadOutcomes();
        if (outcomes.isPresent()) {
            checkTestResultsIn(outcomes.get());
        } else {
            handleMissingTestResults();
        }
    }

    private void checkTestResultsIn(TestOutcomes testOutcomes) {
        switch (testOutcomes.getResult()) {
            case ERROR: throw new TestOutcomesError(testOutcomeSummary(testOutcomes));
            case FAILURE: throw new TestOutcomesFailures(testOutcomeSummary(testOutcomes));
        }
    }

    private String testOutcomeSummary(TestOutcomes testOutcomes) {
        int errors = testOutcomes.count(TestType.ANY).withResult(TestResult.ERROR);
        int failures = testOutcomes.count(TestType.ANY).withResult(TestResult.FAILURE);
        String errorText = (errors > 0) ? "ERROR COUNT: " + errors : "";
        String failureText = (failures > 0) ? "FAILURE COUNT: " + failures : "";
        return "THUCYDIDES TEST FAILURES: " + errorText + " " + failureText;
    }

    private void handleMissingTestResults() {

    }

    private Optional<TestOutcomes> loadOutcomes() {
        TestOutcomes outcomes = null;
        try {
            outcomes = TestOutcomeLoader.loadTestOutcomes().inFormat(OutcomeFormat.JSON).from(outputDirectory);
            if (outcomes.getTotal() == 0) {
                outcomes = TestOutcomeLoader.loadTestOutcomes().inFormat(OutcomeFormat.XML).from(outputDirectory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.of(outcomes);
    }
}
