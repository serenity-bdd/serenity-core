package net.thucydides.core.steps;


import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.logging.LoggingLevel;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.failures.FailureAnalysis;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.NameConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ConsoleLoggingListener implements StepListener {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[91m";
    public static final String ANSI_GREEN = "\u001B[92m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[95m";
    public static final String ANSI_CYAN = "\u001B[96m";

    public static final String SERENITY_BIG_BANNER =
            "\n\n-------------------------------------------------------------------------------------\n" +
            "     _______. _______ .______       _______ .__   __.  __  .___________.____    ____ \n" +
            "    /       ||   ____||   _  \\     |   ____||  \\ |  | |  | |           |\\   \\  /   / \n" +
            "   |   (----`|  |__   |  |_)  |    |  |__   |   \\|  | |  | `---|  |----` \\   \\/   /  \n" +
            "    \\   \\    |   __|  |      /     |   __|  |  . `  | |  |     |  |       \\_    _/   \n" +
            ".----)   |   |  |____ |  |\\  \\----.|  |____ |  |\\   | |  |     |  |         |  |     \n" +
            "|_______/    |_______|| _| `._____||_______||__| \\__| |__|     |__|         |__|    \n" +
            "-------------------------------------------------------------------------------------\n";

    public static final String SERENITY_SMALL_BANNER =
            "\n--------------\n" +
            "- SERENITY   -\n" +
            "--------------";

    // STAR WARS
    private static final List<String> BANNER_HEADINGS = ImmutableList.of(
            "SERENITY TESTS",
            SERENITY_SMALL_BANNER,
            SERENITY_BIG_BANNER);

    // Standard
    private static final List<String> TEST_STARTED_HEADINGS = ImmutableList.of(
            "",
            "\n----------------\n" +
            "- TEST STARTED -\n" +
            "----------------",
             "\n _____ _____ ____ _____   ____ _____  _    ____ _____ _____ ____  \n" +
                "|_   _| ____/ ___|_   _| / ___|_   _|/ \\  |  _ \\_   _| ____|  _ \\ \n" +
                "  | | |  _| \\___ \\ | |   \\___ \\ | | / _ \\ | |_) || | |  _| | | | |\n" +
                "  | | | |___ ___) || |    ___) || |/ ___ \\|  _ < | | | |___| |_| |\n" +
                "  |_| |_____|____/ |_|   |____/ |_/_/   \\_\\_| \\_\\|_| |_____|____/ \n" +
                "                                                                  \n");

    private static final List<String> TEST_PASSED_HEADINGS = ImmutableList.of(
            "",
            "\n---------------\n" +
            "- TEST PASSED -\n" +
            "---------------",
            "\n        __    _____ _____ ____ _____   ____   _    ____  ____  _____ ____  \n" +
                    "  _     \\ \\  |_   _| ____/ ___|_   _| |  _ \\ / \\  / ___|/ ___|| ____|  _ \\ \n" +
                    " (_)_____| |   | | |  _| \\___ \\ | |   | |_) / _ \\ \\___ \\\\___ \\|  _| | | | |\n" +
                    "  _|_____| |   | | | |___ ___) || |   |  __/ ___ \\ ___) |___) | |___| |_| |\n" +
                    " (_)     | |   |_| |_____|____/ |_|   |_| /_/   \\_\\____/|____/|_____|____/ \n" +
                    "        /_/                                                                \n");

    private static final List<String> TEST_FAILED_HEADINGS =  ImmutableList.of(
            "",
            "\n----------------\n" +
                    "- TEST FAILED -\n" +
                    "----------------",
            "\n           __  _____ _____ ____ _____   _____ _    ___ _     _____ ____  \n" +
                    "  _       / / |_   _| ____/ ___|_   _| |  ___/ \\  |_ _| |   | ____|  _ \\ \n" +
                    " (_)_____| |    | | |  _| \\___ \\ | |   | |_ / _ \\  | || |   |  _| | | | |\n" +
                    "  _|_____| |    | | | |___ ___) || |   |  _/ ___ \\ | || |___| |___| |_| |\n" +
                    " (_)     | |    |_| |_____|____/ |_|   |_|/_/   \\_\\___|_____|_____|____/ \n" +
                    "          \\_\\                                                            \n");

    private static final List<String> TEST_ERROR_HEADINGS =  ImmutableList.of(
            "",
            "\n--------------------------\n" +
                    "- TEST FAILED WITH ERROR-\n" +
                    "--------------------------",
            "\n         __  _____ _____ ____ _____   _____ ____  ____   ___  ____  \n" +
                    " _      / / |_   _| ____/ ___|_   _| | ____|  _ \\|  _ \\ / _ \\|  _ \\ \n" +
                    "(_)____| |    | | |  _| \\___ \\ | |   |  _| | |_) | |_) | | | | |_) |\n" +
                    " |_____| |    | | | |___ ___) || |   | |___|  _ <|  _ <| |_| |  _ < \n" +
                    "(_)    | |    |_| |_____|____/ |_|   |_____|_| \\_\\_| \\_\\\\___/|_| \\_\\\n" +
                    "        \\_\\                                                         \n");

    private static final List<String> TEST_COMPROMISED_HEADINGS =  ImmutableList.of(
            "",
            "\n--------------------------\n" +
                    "- TEST COMPROMISED -\n" +
                    "--------------------------",
            "\n         __  _____ _____ ____ _____ \n" +
                    " _      / / |_   _| ____/ ___|_   _|\n" +
                    "(_)____| |    | | |  _| \\___ \\ | |  \n" +
                    " |_____| |    | | | |___ ___) || |  \n" +
                    "(_)    | |    |_| |_____|____/ |_|  \n" +
                    "        \\_\\                         \n" +
                    "  ____ ___  __  __ ____  ____   ___  __  __ ___ ____  _____ ____  \n" +
                    " / ___/ _ \\|  \\/  |  _ \\|  _ \\ / _ \\|  \\/  |_ _/ ___|| ____|  _ \\ \n" +
                    "| |  | | | | |\\/| | |_) | |_) | | | | |\\/| || |\\___ \\|  _| | | | |\n" +
                    "| |__| |_| | |  | |  __/|  _ <| |_| | |  | || | ___) | |___| |_| |\n" +
                    " \\____\\___/|_|  |_|_|   |_| \\_\\\\___/|_|  |_|___|____/|_____|____/\n");

    private static final List<String> TEST_SKIPPED_HEADINGS  = ImmutableList.of(
            "",
            "\n----------------\n" +
            "- TEST SKIPPED -\n" +
            "----------------",
            "\n            __  _____ _____ ____ _____   ____  _  _____ ____  ____  _____ ____  \n" +
                    "  _        / / |_   _| ____/ ___|_   _| / ___|| |/ /_ _|  _ \\|  _ \\| ____|  _ \\ \n" +
                    " (_)_____ / /    | | |  _| \\___ \\ | |   \\___ \\| ' / | || |_) | |_) |  _| | | | |\n" +
                    "  _|_____/ /     | | | |___ ___) || |    ___) | . \\ | ||  __/|  __/| |___| |_| |\n" +
                    " (_)    /_/      |_| |_____|____/ |_|   |____/|_|\\_\\___|_|   |_|   |_____|____/ \n" +
                    "                                                                                \n");

    private static final List<String> TEST_PENDING_HEADINGS  = ImmutableList.of(
            "",
            "\n----------------\n" +
                    "- TEST PENDING -\n" +
                    "----------------",
            "\n          __  _____ _____ ____ _____   ____  _____ _   _ ____ ___ _   _  ____ \n" +
                    " _       / / |_   _| ____/ ___|_   _| |  _ \\| ____| \\ | |  _ \\_ _| \\ | |/ ___|\n" +
                    "(_)____ / /    | | |  _| \\___ \\ | |   | |_) |  _| |  \\| | | | | ||  \\| | |  _ \n" +
                    " |_____/ /     | | | |___ ___) || |   |  __/| |___| |\\  | |_| | || |\\  | |_| |\n" +
                    "(_)   /_/      |_| |_____|____/ |_|   |_|   |_____|_| \\_|____/___|_| \\_|\\____|\n" +
                    "                                                                                \n");

    private static List<String>  FAILURE_HEADINGS  = ImmutableList.of(
            "",
            "\n-----------\n" +
            "- FAILURE -\n" +
            "-----------",
            "\n  _____ _    ___ _     _   _ ____  _____ \n" +
                    " |  ___/ \\  |_ _| |   | | | |  _ \\| ____|\n" +
                    " | |_ / _ \\  | || |   | | | | |_) |  _|  \n" +
                    " |  _/ ___ \\ | || |___| |_| |  _ <| |___ \n" +
                    " |_|/_/   \\_\\___|_____|\\___/|_| \\_\\_____|\n" +
                    "                                         \n");

    private static List<String>  ERROR_HEADINGS  = ImmutableList.of(
            "",
            "\n-----------\n" +
                    "- FAILED WITH ERROR -\n" +
                    "-----------",
            "\n _____ ____  ____   ___  ____  \n" +
                    "| ____|  _ \\|  _ \\ / _ \\|  _ \\ \n" +
                    "|  _| | |_) | |_) | | | | |_) |\n" +
                    "| |___|  _ <|  _ <| |_| |  _ < \n" +
                    "|_____|_| \\_\\_| \\_\\\\___/|_| \\_\\\n\n" +
                    "                                         \n");

    private final Logger logger;
    private final EnvironmentVariables environmentVariables;
    private final int headingStyle;
    private final FailureAnalysis analysis;

    private enum HeadingStyle { MINIMAL, NORMAL, ASCII}

    public ConsoleLoggingListener(EnvironmentVariables environmentVariables,
                                  Logger logger) {
        this.logger = logger;
        this.environmentVariables = environmentVariables;
        this.analysis = new FailureAnalysis(environmentVariables);


        String headerStyleValue = ThucydidesSystemProperty.THUCYDIDES_CONSOLE_HEADINGS.from(environmentVariables, HeadingStyle.ASCII.toString())
                                  .toUpperCase();

        headingStyle = headingStyleFrom(headerStyleValue);

        logBanner();
    }

    private int headingStyleFrom(String headerStyleValue) {
        try {
            return HeadingStyle.valueOf(headerStyleValue.toUpperCase()).ordinal();
        } catch(IllegalArgumentException e) {
            return 1;
        }
    }

    @Inject
    public ConsoleLoggingListener(EnvironmentVariables environmentVariables) {
        this(environmentVariables, LoggerFactory.getLogger(Serenity.class));
    }

    protected Logger getLogger() {
        return logger;
    }

    private void logBanner() {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(bannerHeading());
        }
    }

    private String bannerHeading() {
        return cyan(BANNER_HEADINGS.get(headingStyle));
    }

    private boolean loggingLevelIsAtLeast(LoggingLevel minimumLoggingLevel) {
        return (getLoggingLevel().compareTo(minimumLoggingLevel) >= 0);
    }

    private LoggingLevel getLoggingLevel() {
        String logLevel = ThucydidesSystemProperty.THUCYDIDES_LOGGING.from(environmentVariables,
                                                                LoggingLevel.NORMAL.name());

        return LoggingLevel.valueOf(logLevel);
    }


    public void testSuiteStarted(Class<?> storyClass) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info("Test Suite Started: {}", NameConverter.humanize(storyClass.getSimpleName()));
        }
    }


    public void testSuiteStarted(Story story) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info("Test Suite Started: {}", NameConverter.humanize(story.getName()));
        }
    }


    public void testSuiteFinished() {
    }

    public void testStarted(String description) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(testStartedHeadings() + "\nTEST STARTED: " + description + underline(TEST_STARTED_HEADINGS.get(headingStyle)));
        }
    }

    @Override
    public void testStarted(String description, String id) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(testStartedHeadings() + "\nTEST STARTED: " + description + underline(TEST_STARTED_HEADINGS.get(headingStyle)) + "(" + id + ")");
        }
    }

    private String testStartedHeadings() {
        return TEST_STARTED_HEADINGS.get(headingStyle);
    }

    private String underline(String banner) {
        StringBuilder underline = new StringBuilder();
        int endOfLine = banner.indexOf('\n', 1);
        if (endOfLine >= 0) {
            underline.append(StringUtils.repeat('-', endOfLine));
        } else {
            underline.append(StringUtils.repeat('-', banner.length()));
        }
        return "\n" + underline.toString();
    }

    public void testFinished(TestOutcome result) {
        if (result.isFailure()) {
            logFailure(result);
        }
        if (result.isError()) {
            logError(result);
        }
        if (result.isCompromised()) {
            logCompromised(result);
        }
        if (result.isPending()) {
            logPending(result);
        }
        if (result.isSkipped()) {
            logSkipped(result);
        }
        if (result.isSuccess()) {
            logSuccess(result);
        }
    }

    @Override
    public void testRetried() {
    }

    private void logFailure(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.QUIET)) {
            getLogger().error(red(testFailureHeading() + "\nTEST FAILED: {}"),
                    result.getTitle() + underline(TEST_FAILED_HEADINGS.get(headingStyle)));

            logRelatedIssues(result);
            logFailureCause(result);

            underline(FAILURE_HEADINGS.get(headingStyle));
        }
    }

    private void logError(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.QUIET)) {
            getLogger().error(red(testFailureHeading() + "\nTEST FAILED WITH ERROR: {}"),
                    result.getTitle() + underline(TEST_ERROR_HEADINGS.get(headingStyle)));

            logRelatedIssues(result);
            logFailureCause(result);

            underline(ERROR_HEADINGS.get(headingStyle));
        }
    }

    private void logCompromised(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.QUIET)) {
            getLogger().error(purple(testFailureHeading() + "\nTEST COMPROMISED: {}"),
                    result.getTitle() + underline(TEST_COMPROMISED_HEADINGS.get(headingStyle)));

            logRelatedIssues(result);
            logFailureCause(result);

            underline(ERROR_HEADINGS.get(headingStyle));
        }
    }
    private String testFailureHeading() {
        return TEST_FAILED_HEADINGS.get(headingStyle);
    }

    private void logRelatedIssues(TestOutcome result) {
        Joiner joiner = Joiner.on(",");
        getLogger().debug("RELATED ISSUES: {}", joiner.join(result.getIssueKeys()));

    }

    private void logFailureCause(TestOutcome result) {
        if (result.getNestedTestFailureCause() != null) {
            if (result.getFailingStep().isPresent()) {
                String failingStep = result.getFailingStep().get().unrendered().getDescription();
                getLogger().error(red("TEST FAILED AT STEP " + failingStep));
            }
            getLogger().error(result.getNestedTestFailureCause().getShortenedMessage());
        }
    }

    private void logPending(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(cyan(testPendingHeading() + "\nTEST PENDING: {}"), result.getTitle() + underline(testSkippedHeading()));

        }
    }

    private String testSkippedHeading() {
        return TEST_SKIPPED_HEADINGS.get(headingStyle);
    }

    private String testPendingHeading() {
        return TEST_PENDING_HEADINGS.get(headingStyle);
    }

    private void logSkipped(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(yellow(testSkippedHeading() + "\nTEST SKIPPED: {}"), result.getTitle() + underline(testSkippedHeading()));
        }
    }

    private void logSuccess(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(green(testPassedHeading() + "\nTEST PASSED: {}"), result.getTitle() + underline(testPassedHeading()));
        }
    }

    private String testPassedHeading() {
        return TEST_PASSED_HEADINGS.get(headingStyle);
    }

    public void stepStarted(ExecutedStepDescription description) {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            getLogger().info("STARTING STEP {}", description.getTitle());
        }
    }


    public void skippedStepStarted(ExecutedStepDescription description) {
        stepStarted(description);
    }

    public void stepFinished() {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            getLogger().info("FINISHING STEP");
        }
    }

    public void stepFailed(StepFailure failure) {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            String errorMessage = (failure.getException() != null) ? failure.getException().toString() : failure.getMessage();
            String failureType = analysis.resultFor(failure.getException()).name();
            getLogger().info(red("STEP {}: {}"), failureType, errorMessage);
        }
    }


    public void lastStepFailed(StepFailure failure) {
    }

    public void stepIgnored() {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            getLogger().info(yellow("IGNORING STEP"));
        }
    }

    public void stepPending() {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            getLogger().info(cyan("PENDING STEP"));
        }
    }


    public void stepPending(String message) {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            getLogger().info(cyan("PENDING STEP ({})"), message);
        }
    }


    public void testFailed(TestOutcome testOutcome, Throwable cause) {
    }


    public void testIgnored() {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(yellow("TEST IGNORED"));
        }
    }

    @Override
    public void testSkipped() {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(yellow("TEST SKIPPED"));
        }
    }

    @Override
    public void testPending() {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(cyan("TEST PENDING"));
        }
    }

    @Override
    public void testIsManual() {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info("TEST MANUAL");
        }
    }


    public void notifyScreenChange() {
    }

    public void useExamplesFrom(DataTable table) {
    }

    @Override
    public void addNewExamplesFrom(DataTable table) {

    }

    public void exampleStarted(Map<String,String> data) {
    }

    public void exampleFinished() {
    }

    @Override
    public void assumptionViolated(String message) {
        if (loggingLevelIsAtLeast(LoggingLevel.QUIET)) {
            getLogger().error(red("ASSUMPTION VIOLATED: " + message));
        }
    }

    @Override
    public void testRunFinished() {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            getLogger().info("FINISHING TEST RUN");
        }
    }

    private boolean showColoredOutput() {
        return ThucydidesSystemProperty.THUCYDIDES_CONSOLE_COLORS.booleanFrom(environmentVariables,false);
    }

    private String red(String text) {
        return (showColoredOutput()) ? ANSI_RED + text + ANSI_RESET : text;
    }

    private String green(String text) {
        return (showColoredOutput()) ? ANSI_GREEN + text + ANSI_RESET : text;
    }

    private String yellow(String text) {
        return (showColoredOutput()) ? ANSI_YELLOW + text + ANSI_RESET : text;
    }

    private String cyan(String text) {
        return (showColoredOutput()) ? ANSI_CYAN + text + ANSI_RESET : text;
    }

    private String purple(String text) {
        return (showColoredOutput()) ? ANSI_PURPLE + text + ANSI_RESET : text;
    }

}
