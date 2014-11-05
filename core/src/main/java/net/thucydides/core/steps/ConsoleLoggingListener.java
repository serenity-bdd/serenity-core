package net.thucydides.core.steps;


import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.thucydides.core.Thucydides;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.logging.LoggingLevel;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.NameConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ConsoleLoggingListener implements StepListener {

    // STAR WARS
    private static final List<String> BANNER_HEADINGS = ImmutableList.of(
            "\n--------------\n" +
            "- THUCYDIDES -\n" +
            "--------------",
            "\n\n-------------------------------------------------------------------------------------------------------\n" +
                    ".___________. __    __   __    __    ______ ____    ____  _______   __   _______   _______     _______.\n" +
                    "|           ||  |  |  | |  |  |  |  /      |\\   \\  /   / |       \\ |  | |       \\ |   ____|   /       |\n" +
                    "`---|  |----`|  |__|  | |  |  |  | |  ,----' \\   \\/   /  |  .--.  ||  | |  .--.  ||  |__     |   (----`\n" +
                    "    |  |     |   __   | |  |  |  | |  |       \\_    _/   |  |  |  ||  | |  |  |  ||   __|     \\   \\    \n" +
                    "    |  |     |  |  |  | |  `--'  | |  `----.    |  |     |  '--'  ||  | |  '--'  ||  |____.----)   |   \n" +
                    "    |__|     |__|  |__|  \\______/   \\______|    |__|     |_______/ |__| |_______/ |_______|_______/    \n" +
                    "                                                                                                       \n" +
                    "-------------------------------------------------------------------------------------------------------\n");

    // Standard
    private static final List<String> TEST_STARTED_HEADINGS = ImmutableList.of(
            "\n----------------\n" +
            "- TEST STARTED -\n" +
            "----------------",
            "\n  _____ _____ ____ _____   ____ _____  _    ____ _____ _____ ____  \n" +
                    " |_   _| ____/ ___|_   _| / ___|_   _|/ \\  |  _ \\_   _| ____|  _ \\ \n" +
                    "   | | |  _| \\___ \\ | |   \\___ \\ | | / _ \\ | |_) || | |  _| | | | |\n" +
                    "   | | | |___ ___) || |    ___) || |/ ___ \\|  _ < | | | |___| |_| |\n" +
                    "   |_| |_____|____/ |_|   |____/ |_/_/   \\_\\_| \\_\\|_| |_____|____/ \n" +
                    "                                                                   \n");

    private static final List<String> TEST_PASSED_HEADINGS = ImmutableList.of(
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
            "\n----------------\n" +
            "- TEST FAILED -\n" +
            "----------------",
            "\n           __  _____ _____ ____ _____   _____ _    ___ _     _____ ____  \n" +
                    "  _       / / |_   _| ____/ ___|_   _| |  ___/ \\  |_ _| |   | ____|  _ \\ \n" +
                    " (_)_____| |    | | |  _| \\___ \\ | |   | |_ / _ \\  | || |   |  _| | | | |\n" +
                    "  _|_____| |    | | | |___ ___) || |   |  _/ ___ \\ | || |___| |___| |_| |\n" +
                    " (_)     | |    |_| |_____|____/ |_|   |_|/_/   \\_\\___|_____|_____|____/ \n" +
                    "          \\_\\                                                            \n");

    private static final List<String> TEST_SKIPPED_HEADINGS  = ImmutableList.of(
            "\n----------------\n" +
            "- TEST SKIPPED -\n" +
            "----------------",
            "\n            __  _____ _____ ____ _____   ____  _  _____ ____  ____  _____ ____  \n" +
                    "  _        / / |_   _| ____/ ___|_   _| / ___|| |/ /_ _|  _ \\|  _ \\| ____|  _ \\ \n" +
                    " (_)_____ / /    | | |  _| \\___ \\ | |   \\___ \\| ' / | || |_) | |_) |  _| | | | |\n" +
                    "  _|_____/ /     | | | |___ ___) || |    ___) | . \\ | ||  __/|  __/| |___| |_| |\n" +
                    " (_)    /_/      |_| |_____|____/ |_|   |____/|_|\\_\\___|_|   |_|   |_____|____/ \n" +
                    "                                                                                \n");

    private static List<String>  FAILURE_HEADINGS  = ImmutableList.of(
            "\n-----------\n" +
            "- FAILURE -\n" +
            "-----------",
            "\n  _____ _    ___ _     _   _ ____  _____ \n" +
                    " |  ___/ \\  |_ _| |   | | | |  _ \\| ____|\n" +
                    " | |_ / _ \\  | || |   | | | | |_) |  _|  \n" +
                    " |  _/ ___ \\ | || |___| |_| |  _ <| |___ \n" +
                    " |_|/_/   \\_\\___|_____|\\___/|_| \\_\\_____|\n" +
                    "                                         \n");

    private final Logger logger;
    private final EnvironmentVariables environmentVariables;
    private final int headingStyle;

    private enum HeadingStyle { NORMAL, ASCII}

    public ConsoleLoggingListener(EnvironmentVariables environmentVariables,
                                  Logger logger) {
        this.logger = logger;
        this.environmentVariables = environmentVariables;
        String headerStyleValue = environmentVariables.getProperty(ThucydidesSystemProperty.THUCYDIDES_CONSOLE_HEADINGS,
                                                                   HeadingStyle.ASCII.toString()).toUpperCase();

        if (HeadingStyle.NORMAL.toString().equals(headerStyleValue)) {
            headingStyle = 0;
        } else {
            headingStyle = 1;
        }
        logBanner();
    }

    @Inject
    public ConsoleLoggingListener(EnvironmentVariables environmentVariables) {
        this(environmentVariables, LoggerFactory.getLogger(Thucydides.class));
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
        return BANNER_HEADINGS.get(headingStyle);
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
            getLogger().info("Test Suite Started: " + NameConverter.humanize(storyClass.getSimpleName()));
        }
    }

    
    public void testSuiteStarted(Story story) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info("Test Suite Started: " + NameConverter.humanize(story.getName()));
        }
    }

    
    public void testSuiteFinished() {
    }

    public void testStarted(String description) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(testStartedHeadings() + "\nTEST STARTED: " + description + underline(TEST_STARTED_HEADINGS.get(headingStyle)));
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
        } else if (result.isPending()) {
            logPending(result);
        } else if (result.isSkipped()) {
            logSkipped(result);
        } else if (result.isSuccess()) {
            logSuccess(result);
        }
    }

    @Override
    public void testRetried() {
    }

    private void logFailure(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(testFailureHeading() + "\nTEST FAILED: " + result.getTitle() + underline(TEST_FAILED_HEADINGS.get(headingStyle)));

            logRelatedIssues(result);
            logFailureCause(result);

            underline(FAILURE_HEADINGS.get(headingStyle));
        }
    }

    private String testFailureHeading() {
        return TEST_FAILED_HEADINGS.get(headingStyle);
    }

    private void logRelatedIssues(TestOutcome result) {
        Joiner joiner = Joiner.on(",");
        getLogger().info("RELATED ISSUES: " + joiner.join(result.getIssueKeys()));

    }

    private void logFailureCause(TestOutcome result) {
        if (result.getTestFailureMessage() != null) {
            getLogger().info(failureHeading() + "\n" + result.getTestFailureMessage());
        }
    }

    private String failureHeading() {
        return FAILURE_HEADINGS.get(headingStyle);
    }

    private void logPending(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(testSkippedHeading() + "\nTEST PENDING: " + result.getTitle() + underline(testSkippedHeading()));

        }
    }

    private String testSkippedHeading() {
        return TEST_SKIPPED_HEADINGS.get(headingStyle);
    }

    private void logSkipped(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(testSkippedHeading() + "\nTEST SKIPPED: " + result.getTitle() + underline(testSkippedHeading()));
        }
    }

    private void logSuccess(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(testPassedHeading() + "\nTEST PASSED: " + result.getTitle() + underline(testPassedHeading()));
        }
    }

    private String testPassedHeading() {
        return TEST_PASSED_HEADINGS.get(headingStyle);
    }

    public void stepStarted(ExecutedStepDescription description) {
        if (loggingLevelIsAtLeast(getLoggingLevel().VERBOSE)) {
            getLogger().info("STARTING STEP " + description.getTitle());
        }
    }

    
    public void skippedStepStarted(ExecutedStepDescription description) {
        stepStarted(description);
    }

    public void stepFinished() {
        if (loggingLevelIsAtLeast(getLoggingLevel().VERBOSE)) {
            getLogger().info("FINISHING STEP");
        }
    }

    public void stepFailed(StepFailure failure) {
        if (loggingLevelIsAtLeast(getLoggingLevel().VERBOSE)) {
            getLogger().info("STEP FAILED: " + failure.getMessage());
        }
    }

    
    public void lastStepFailed(StepFailure failure) {
    }

    public void stepIgnored() {
        if (loggingLevelIsAtLeast(getLoggingLevel().VERBOSE)) {
            getLogger().info("IGNORING STEP");
        }
    }

    public void stepPending() {
        if (loggingLevelIsAtLeast(getLoggingLevel().VERBOSE)) {
            getLogger().info("PENDING STEP");
        }
    }

    
    public void stepPending(String message) {
        if (loggingLevelIsAtLeast(getLoggingLevel().VERBOSE)) {
            getLogger().info("PENDING STEP " + "(" + message + ")");
        }
    }

    
    public void testFailed(TestOutcome testOutcome, Throwable cause) {
    }

    
    public void testIgnored() {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info("TEST IGNORED");
        }
    }

    @Override
    public void testSkipped() {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info("TEST SKIPPED");
        }
    }

    @Override
    public void testPending() {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info("TEST PENDING");
        }    }


    public void notifyScreenChange() {
    }

    public void useExamplesFrom(DataTable table) {
    }

    public void exampleStarted(Map<String,String> data) {
    }

    public void exampleFinished() {
    }

    @Override
    public void assumptionViolated(String message) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info("ASSUMPTION VIOLATED");
        }
    }
}
