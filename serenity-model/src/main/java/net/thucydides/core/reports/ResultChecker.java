package net.thucydides.core.reports;

import net.serenitybdd.core.strings.Joiner;
import com.google.common.base.Splitter;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.model.TestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ResultChecker {

    private static final String WITH_NO_TAGS = "";
    private final File outputDirectory;

    private final List<TestTag> tags;

    private final static Logger logger = LoggerFactory.getLogger(ResultChecker.class);

    public ResultChecker(File outputDirectory) {
        this(outputDirectory, WITH_NO_TAGS);
    }

    public ResultChecker(File outputDirectory, String tags) {
        this.outputDirectory = outputDirectory;
        this.tags = tagsFrom(tags);
    }

    private List<TestTag> tagsFrom(String tags) {
        List<String> tagValues = Splitter.on(",").trimResults().splitToList(tags);
        return tagValues.stream().map(TestTag::withValue).collect(Collectors.toList());
    }

    public TestResult checkTestResults() {
        Optional<TestOutcomes> outcomes = loadOutcomes();

        if (outcomes.isPresent()) {
            logOutcomesFrom(outcomes.get());
            return outcomes.get().getResult();
        }

        return TestResult.UNDEFINED;
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

    private String testOutcomeSummary(TestOutcomes testOutcomes) {
        int errors = testOutcomes.count(TestType.ANY).withResult(TestResult.ERROR);
        int failures = testOutcomes.count(TestType.ANY).withResult(TestResult.FAILURE);
        int compromised = testOutcomes.count(TestType.ANY).withResult(TestResult.COMPROMISED);

        return Joiner.on(" ").join("THUCYDIDES TEST FAILURES:",
                OutcomeSummary.forOutcome(TestResult.ERROR).withCount(errors),
                OutcomeSummary.forOutcome(TestResult.FAILURE).withCount(failures),
                OutcomeSummary.forOutcome(TestResult.COMPROMISED).withCount(compromised));
    }

    private Optional<TestOutcomes> loadOutcomes() {
        TestOutcomes outcomes = null;
        try {
            outcomes = TestOutcomeLoader.loadTestOutcomes().inFormat(OutcomeFormat.JSON).from(outputDirectory);
            if (outcomes.getTotal() == 0) {
                outcomes = TestOutcomeLoader.loadTestOutcomes().inFormat(OutcomeFormat.XML).from(outputDirectory);
            }

            if (thereAreTagsIn(tags)) {
                outcomes = outcomes.withTags(tags);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(outcomes);
    }

    private boolean thereAreTagsIn(List<TestTag> tags) {
        return !(tags.isEmpty() || isBlank(tags.get(0).getName()));
    }
}
