package net.thucydides.model.reports;

import com.google.common.base.Splitter;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.domain.TestType;
import net.thucydides.model.requirements.reports.CompoundDuration;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ResultChecker {

    private static final String WITH_NO_TAGS = "";
    private final File outputDirectory;
    private final List<TestTag> tags;
    private final AsciiColors asciiColors;

    private final static Logger logger = LoggerFactory.getLogger(ResultChecker.class);

    public ResultChecker(File outputDirectory) {
        this(outputDirectory, WITH_NO_TAGS);
    }

    public ResultChecker(File outputDirectory, String tags) {
        this(outputDirectory, tags, SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    public ResultChecker(File outputDirectory, String tags, EnvironmentVariables environmentVariables) {
        this.outputDirectory = outputDirectory;
        this.tags = tagsFrom(tags);
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
    private static final int COLUMN_WIDTH = 30;
    private void logOutcomesFrom(TestOutcomes testOutcomes) {


        logger.info(white("------------------------------------------------",0));
        logger.info(white("| SERENITY TESTS: ", COLUMN_WIDTH) + "  | " + colored(testOutcomes.getResult(), testOutcomes.getResult().toString()));
        logger.info(white("------------------------------------------------",0));
        logger.info(
                resultLine(white(
                 "Test scenarios executed",COLUMN_WIDTH), white(Long.toString(testOutcomes.getScenarioCount()),0)));
        logger.info(
                resultLine(white(
                        "Total Test cases executed",COLUMN_WIDTH), white(Long.toString(testOutcomes.getTestCaseCount()),0)));
        if (testOutcomes.ofType(TestType.MANUAL).getTestCaseCount() > 0) {
            logger.info(
                    resultLine(white(
                            "Automated Test cases executed", COLUMN_WIDTH), white(Long.toString(testOutcomes.ofType(TestType.AUTOMATED).getTestCaseCount()), 0)));
            logger.info(
                    resultLine(white(
                            "Manual Test cases executed", COLUMN_WIDTH), white(Long.toString(testOutcomes.ofType(TestType.MANUAL).getTestCaseCount()), 0)));
        }
        logger.info(
                resultLine(green("Tests passed",COLUMN_WIDTH), green(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.SUCCESS)),0))
        );
        logger.info(
                resultLine(red("Tests failed",COLUMN_WIDTH), red(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.FAILURE)),0))
        );
        logger.info(
                resultLine(yellow("Tests with errors",COLUMN_WIDTH), yellow(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.ERROR)),0))
        );
        logger.info(
                resultLine(purple("Tests compromised",COLUMN_WIDTH), purple(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.COMPROMISED)),0))
        );
        logger.info(
                resultLine(purple("Tests aborted",COLUMN_WIDTH), purple(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.ABORTED)),0))
        );
        logger.info(
                resultLine(cyan("Tests pending",COLUMN_WIDTH), cyan(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.PENDING)),0))
        );
        logger.info(
                resultLine(grey("Tests ignored/skipped",COLUMN_WIDTH), grey(Integer.toString(testOutcomes.count(TestType.ANY).withResult(TestResult.IGNORED)
                                                                                           + testOutcomes.count(TestType.ANY).withResult(TestResult.SKIPPED)),0))
        );

        logger.info("------------------------------- | --------------");
        logger.info(resultLine("Total Duration", CompoundDuration.of(testOutcomes.getDuration())));
        logger.info(resultLine("Fastest test took", CompoundDuration.of(testOutcomes.getFastestTestDuration())));
        logger.info(resultLine("Slowest test took", CompoundDuration.of(testOutcomes.getSlowestTestDuration())));
        logger.info("------------------------------------------------");
        logger.info("");

        Path index = outputDirectory.toPath().resolve("index.html");

        logger.info(white("SERENITY REPORTS",0));
        logger.info("  - Full Report: " + index.toUri());
    }

    private String resultLine(String label, String value) {
        return  "| " + label + "| " + value;
    }

    private String colored(TestResult result, String text) {
        switch (result) {
            case SUCCESS:
                return green(text,0);
            case FAILURE:
                return red(text,0);
            case ERROR:
                return yellow(text,0);
            case PENDING:
                return cyan(text,0);
            default:
                return text;
        }
    }

    private String red(String text, int width) {
        return asciiColors.bold().red(StringUtils.rightPad(text, width));
    }

    private String green(String text, int width) {
        return asciiColors.bold().green(StringUtils.rightPad(text, width));
    }

    private String cyan(String text, int width) {
        return asciiColors.bold().cyan(StringUtils.rightPad(text, width));
    }

    private String grey(String text, int width) {
        return asciiColors.bold().grey(StringUtils.rightPad(text, width));
    }

    private String yellow(String text, int width) {
        return asciiColors.bold().yellow(StringUtils.rightPad(text, width));
    }

    private String purple(String text, int width) {
        return asciiColors.bold().magenta(StringUtils.rightPad(text, width));
    }

    private String white(String text, int width) {
        return asciiColors.bold().white(StringUtils.rightPad(text, width));
    }


    private Optional<TestOutcomes> loadOutcomes() {
        TestOutcomes outcomes = null;
        try {
            outcomes = TestOutcomeLoader.loadTestOutcomes()
                                        .inFormat(OutcomeFormat.JSON)
                                        .from(outputDirectory)
                                        .filteredByEnvironmentTags();
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
