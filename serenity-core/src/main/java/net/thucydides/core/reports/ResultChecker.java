package net.thucydides.core.reports;

import com.google.common.base.Optional;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by john on 22/09/2014.
 */
public class ResultChecker {

    private final File outputDirectory;

    private final static Logger logger = LoggerFactory.getLogger(ResultChecker.class);

    public ResultChecker(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void checkTestResults() {
        Optional<TestOutcomes> outcomes = loadOutcomes();
        if (outcomes.isPresent()) {
            logOutcomesFrom(outcomes.get());
            checkTestResultsIn(outcomes.get());
        } else {
            handleMissingTestResults();
        }
    }

    private void logOutcomesFrom(TestOutcomes testOutcomes) {
        logger.info("----------------------");
        logger.info("SERENITY TEST OUTCOMES");
        logger.info("----------------------");

        logger.info("  - Tests executed: " + testOutcomes.getTotal());
        logger.info("  - Tests passed: " + testOutcomes.getPassingTests().getTotal());
        logger.info("  - Tests failed: " + testOutcomes.getFailingTests().getTotal());
        logger.info("  - Tests with errors: " + testOutcomes.getErrorTests().getTotal());
        logger.info("  - Tests pending: " + testOutcomes.getPendingTests().getTotal());
        logger.info("  - Tests compromised: " + testOutcomes.getCompromisedTests().getTotal());

    }

    private void checkTestResultsIn(TestOutcomes testOutcomes) {

        switch (testOutcomes.getResult()) {
            case ERROR: throw new TestOutcomesError(testOutcomeSummary(testOutcomes));
            case FAILURE: throw new TestOutcomesFailures(testOutcomeSummary(testOutcomes));
            case COMPROMISED: throw new TestOutcomesCompromised(testOutcomeSummary(testOutcomes));
        }
    }

    private String testOutcomeSummary(TestOutcomes testOutcomes) {
        int errors = testOutcomes.count(TestType.ANY).withResult(TestResult.ERROR);
        int failures = testOutcomes.count(TestType.ANY).withResult(TestResult.FAILURE);
        int compromised = testOutcomes.count(TestType.ANY).withResult(TestResult.COMPROMISED);
        String errorText = (errors > 0) ? "ERROR COUNT: " + errors : "";
        String failureText = (failures > 0) ? "FAILURE COUNT: " + failures : "";
        String compromisedText = (compromised > 0) ? "COMPROMISED COUNT: " + failures : "";
        return "THUCYDIDES TEST FAILURES: " + errorText + " " + failureText + " " + compromisedText;
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
