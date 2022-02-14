package net.thucydides.core.reports;

import com.google.common.base.Splitter;
import net.serenitybdd.core.strings.Joiner;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.model.TestType;
import net.thucydides.core.requirements.reports.CompoundDuration;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ResultChecker {

    private static final String WITH_NO_TAGS = "";
    private final File outputDirectory;
    private EnvironmentVariables environmentVariables;
    private final List<TestTag> tags;
    private final AsciiColors asciiColors;

    private final static Logger logger = LoggerFactory.getLogger(ResultChecker.class);

    public ResultChecker(File outputDirectory) {
        this(outputDirectory, WITH_NO_TAGS);
    }

    public ResultChecker(File outputDirectory, String tags) {
        this(outputDirectory, tags, Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public ResultChecker(File outputDirectory, String tags, EnvironmentVariables environmentVariables) {
        this.outputDirectory = outputDirectory;
        this.tags = tagsFrom(tags);
        this.environmentVariables = environmentVariables;
        this.asciiColors = new AsciiColors(environmentVariables);
    }

    private List<TestTag> tagsFrom(String tags) {
        List<String> tagValues = Splitter.on(",").trimResults().splitToList(tags);
        return tagValues.stream().map(TestTag::withValue).collect(Collectors.toList());
    }

    public TestResult checkTestResults(TestOutcomes outcomes) {

        if ((outcomes != null) && (!outcomes.isEmpty())) {
            logOutcomesFrom(outcomes);
            return outcomes.getResult();
        }

        return TestResult.UNDEFINED;
    }
    public TestResult checkTestResults() {
        Optional<TestOutcomes> outcomes = loadOutcomes();

        if (outcomes.isPresent()) {
            return checkTestResults(outcomes.get());
        }

        return TestResult.UNDEFINED;
    }

    private void logOutcomesFrom(TestOutcomes testOutcomes) {
        logger.info(white("-----------------------------------------"));
        logger.info(white(" SERENITY TESTS: ") + colored(testOutcomes.getResult(), testOutcomes.getResult().toString()));
        logger.info(white("-----------------------------------------"));
        logger.info(
                resultLine(white(
                 "Test cases executed    "), white(Integer.toString(testOutcomes.getOutcomes().size()))));
        logger.info(
                resultLine(white(
                 "Tests executed         "), white(Integer.toString(testOutcomes.getTotal()))));
        logger.info(
                resultLine(green(
                 "Tests passed           "), green(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.SUCCESS))))
        );
        logger.info(
                resultLine(red(
                 "Tests failed           "), red(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.FAILURE))))
        );
        logger.info(
                resultLine(yellow(
                 "Tests with errors      "), yellow(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.ERROR))))
        );
        logger.info(
                resultLine(purple(
                        "Tests compromised      "), purple(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.COMPROMISED))))
        );
        logger.info(
                resultLine(purple(
                        "Tests aborted          "), purple(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.ABORTED))))
        );
        logger.info(
                resultLine(cyan(
                  "Tests pending          "), cyan(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.PENDING))))
        );
        logger.info(
                resultLine(grey(
                  "Tests ignored/skipped  "), grey(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.IGNORED)
                                                                                        + testOutcomes.count(TestType.ANY).withResult(TestResult.SKIPPED))))
        );
        logger.info("------------------------ | --------------");
        logger.info(resultLine("Total Duration         ", CompoundDuration.of(testOutcomes.getDuration())));
        logger.info(resultLine("Fastest test took      ", CompoundDuration.of(testOutcomes.getFastestTestDuration())));
        logger.info(resultLine("Slowest test took      ", CompoundDuration.of(testOutcomes.getSlowestTestDuration())));
        logger.info("-----------------------------------------");
        logger.info("");

        Path index = outputDirectory.toPath().resolve("index.html");

        logger.info(white("SERENITY REPORTS"));
        logger.info("  - Full Report: " + index.toUri());
    }

    private String resultLine(String label, String value) {
        return  "| " + label + "| " + value;
    }

    private String pad(String text, int totalLength) {
        return StringUtils.rightPad(text, totalLength);
    }

    private String colored(TestResult result, String text) {
        switch (result) {
            case SUCCESS:
                return green(text);
            case FAILURE:
                return red(text);
            case ERROR:
                return yellow(text);
            case PENDING:
                return cyan(text);
            default:
                return text;
        }
    }

    private String red(String text) {
        return asciiColors.bold().red(text);
    }

    private String green(String text) {
        return asciiColors.bold().green(text);
    }

    private String cyan(String text) {
        return asciiColors.bold().cyan(text);
    }

    private String grey(String text) {
        return asciiColors.bold().grey(text);
    }

    private String yellow(String text) {
        return asciiColors.bold().yellow(text);
    }

    private String purple(String text) {
        return asciiColors.bold().magenta(text);
    }

    private String white(String text) {
        return asciiColors.bold().white(text);
    }

    private String testOutcomeSummary(TestOutcomes testOutcomes) {
        int errors = testOutcomes.count(TestType.ANY).withResult(TestResult.ERROR);
        int failures = testOutcomes.count(TestType.ANY).withResult(TestResult.FAILURE);
        int compromised = testOutcomes.count(TestType.ANY).withResult(TestResult.COMPROMISED);

        return Joiner.on(" ").join("SERENITY TEST FAILURES:",
                OutcomeSummary.forOutcome(TestResult.ERROR).withCount(errors),
                OutcomeSummary.forOutcome(TestResult.FAILURE).withCount(failures),
                OutcomeSummary.forOutcome(TestResult.COMPROMISED).withCount(compromised));
    }

    private Optional<TestOutcomes> loadOutcomes() {
        TestOutcomes outcomes = null;
        try {
            outcomes = TestOutcomeLoader.loadTestOutcomes()
                                        .inFormat(OutcomeFormat.JSON)
                                        .from(outputDirectory)
                                        .filteredByEnvironmentTags();
//
//            if (outcomes.getTotal() == 0) {
//                outcomes = TestOutcomeLoader.loadTestOutcomes()
//                                            .inFormat(OutcomeFormat.XML)
//                                            .from(outputDirectory)
//                                            .filteredByEnvironmentTags();
//            }

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
