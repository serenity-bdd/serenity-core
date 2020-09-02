package net.thucydides.core.logging;


import com.google.inject.Inject;
import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.strings.Joiner;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.failures.FailureAnalysis;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.NameConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiConsumer;

import static net.thucydides.core.logging.ConsoleEvent.*;

public class ConsoleLoggingListener implements StepListener {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[91m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_GREEN = "\u001B[92m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_PURPLE = "\u001B[95m";
    private static final String ANSI_CYAN = "\u001B[96m";

    public static final String SERENITY_BIG_BANNER =
            "\n\n-------------------------------------------------------------------------------------\n" +
                    "     _______. _______ .______       _______ .__   __.  __  .___________.____    ____ \n" +
                    "    /       ||   ____||   _  \\     |   ____||  \\ |  | |  | |           |\\   \\  /   / \n" +
                    "   |   (----`|  |__   |  |_)  |    |  |__   |   \\|  | |  | `---|  |----` \\   \\/   /  \n" +
                    "    \\   \\    |   __|  |      /     |   __|  |  . `  | |  |     |  |       \\_    _/   \n" +
                    ".----)   |   |  |____ |  |\\  \\----.|  |____ |  |\\   | |  |     |  |         |  |     \n" +
                    "|_______/    |_______|| _| `._____||_______||__| \\__| |__|     |__|         |__|    \n" +
                    "                                                                                     \n" +
                    " News and tutorials at http://www.serenity-bdd.info                                  \n" +
                    " Documentation at https://wakaleo.gitbooks.io/the-serenity-book/content/             \n" +
                    " Join the Serenity Community on Gitter: https://gitter.im/serenity-bdd/serenity-core \n" +
                    " Serenity BDD Support and Training at http://serenity-bdd.info/#/trainingandsupport  \n" +
                    "-------------------------------------------------------------------------------------\n";

    public static final String SERENITY_SMALL_BANNER =
            "\n\n____ ____ ____ ____ _  _ _ ___ _   _    ___  ___  ___  \n" +
                    "[__  |___ |__/ |___ |\\ | |  |   \\_/     |__] |  \\ |  \\ \n" +
                    "___] |___ |  \\ |___ | \\| |  |    |      |__] |__/ |__/ \n" +
                    "                                                                                     \n" +
                    " News and tutorials at http://www.serenity-bdd.info                                  \n" +
                    " Documentation at https://wakaleo.gitbooks.io/the-serenity-book/content/             \n" +
                    " Join the Serenity Community on Gitter: https://gitter.im/serenity-bdd/serenity-core \n" +
                    " Serenity BDD Support and Training at http://serenity-bdd.info/#/trainingandsupport  \n" +
                    "-------------------------------------------------------------------------------------\n";

    // MAIN BANNERS
    private static final List<String> BANNER_HEADINGS = NewList.of(
            SERENITY_SMALL_BANNER,
            SERENITY_SMALL_BANNER,
            SERENITY_BIG_BANNER);

    private final Logger logger;
    private final EnvironmentVariables environmentVariables;
    private final ConsoleHeadingStyle headingStyle;
    private final ConsoleHeadingStyle bannerStyle;
    private final FailureAnalysis analysis;
    private final ConsoleHeading consoleHeading;

    private ExecutedStepDescription currentStep;
    private Set<ExecutedStepDescription> flaggedSteps = new HashSet<>();
    private Set<TestOutcome> reportedOutcomes = new HashSet<>();

    private Stack<String> nestedSteps = new Stack<>();

    private enum HeadingStyle {MINIMAL, NORMAL, ASCII}

    public ConsoleLoggingListener(EnvironmentVariables environmentVariables,
                                  Logger logger) {
        this.logger = logger;
        this.environmentVariables = environmentVariables;
        this.analysis = new FailureAnalysis(environmentVariables);
        this.consoleHeading = new ConsoleHeading(environmentVariables);
        this.headingStyle = ConsoleHeadingStyle.definedIn(environmentVariables);
        this.bannerStyle = ConsoleHeadingStyle.bannerStyleDefinedIn(environmentVariables);

        logBanner();
    }

    @Inject
    public ConsoleLoggingListener(EnvironmentVariables environmentVariables) {
        this(environmentVariables, LoggerFactory.getLogger(""));
    }

    protected Logger getLogger() {
        return logger;
    }

    private void logBanner() {
        if (loggingLevelIsAtLeast(LoggingLevel.QUIET)) {
            getLogger().info(bannerHeading());
        }
    }

    private String bannerHeading() {
        return cyan(BANNER_HEADINGS.get(bannerStyle.getLevel()));
    }

    private boolean loggingLevelIsAtLeast(LoggingLevel minimumLoggingLevel) {
        return LoggingLevel.definedIn(environmentVariables).isAtLeast(minimumLoggingLevel);
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
        flaggedSteps.clear();
        reportedOutcomes.clear();
        nestedSteps.clear();
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(consoleHeading.bannerFor(TEST_STARTED, description));
        }
    }

    @Override
    public void testStarted(String description, String id) {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(consoleHeading.bannerFor(TEST_STARTED, description + "(" + id + ")"));
        }
    }

    public void testFinished(TestOutcome result) {
        if (reportedOutcomes.contains(result)) {
            return;
        } else {
            reportedOutcomes.add(result);
        }

        if (result.isManual()) {
            logManual(result);
            return;
        }

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
    public void testFinished(TestOutcome result, boolean isInDataDrivenTest) {
        testFinished(result);
    }

    @Override
    public void testRetried() {
    }

    private Map<TestResult, BiConsumer<Logger, String>> coloredLogs() {
        Map<TestResult, BiConsumer<Logger, String>> coloredLogs = new HashMap<>();
        coloredLogs.put(TestResult.SUCCESS, (log, msg) -> log.info(green(msg)));
        coloredLogs.put(TestResult.FAILURE, (log, msg) -> log.error(red(msg)));
        coloredLogs.put(TestResult.ERROR, (log, msg) -> log.error(red(msg)));
        coloredLogs.put(TestResult.PENDING, (log, msg) -> log.info(cyan(msg)));
        coloredLogs.put(TestResult.SKIPPED, (log, msg) -> log.info(yellow(msg)));
        coloredLogs.put(TestResult.IGNORED, (log, msg) -> log.info(yellow(msg)));
        coloredLogs.put(TestResult.COMPROMISED, (log, msg) -> log.error(purple(msg)));
        coloredLogs.put(TestResult.UNDEFINED, (log, msg) -> log.info(msg));

        return coloredLogs;
    }

    private void logManual(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.QUIET)) {

            String message = consoleHeading.bannerFor(ConsoleEvent.forTestResult(result.getResult()),
                    result.getTitle() + " (manual test)");

            coloredLogs().get(result.getResult()).accept(getLogger(), message);

            logRelatedIssues(result);
        }
    }

    private void logFailure(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.QUIET)) {
            getLogger().error(red(consoleHeading.bannerFor(TEST_FAILED, result.getTitle())));
            logRelatedIssues(result);
            logFailureCause(result);
        }
    }

    private void logError(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.QUIET)) {
            getLogger().error(red(consoleHeading.bannerFor(TEST_ERROR, result.getTitle())));
            logRelatedIssues(result);
            logFailureCause(result);

        }
    }

    private void logCompromised(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.QUIET)) {
            getLogger().error(red(consoleHeading.bannerFor(TEST_COMPROMISED, result.getTitle())));
            logRelatedIssues(result);
            logFailureCause(result);
        }
    }

    private void logRelatedIssues(TestOutcome result) {
        Joiner joiner = Joiner.on(",");
        getLogger().debug("RELATED ISSUES: {}", joiner.join(result.getIssueKeys()));

    }

    private void logFailureCause(TestOutcome result) {
        if (result.getNestedTestFailureCause() != null) {
            if (result.getFailingStep().isPresent()) {
                String failingStep = result.getFailingStep().get().unrendered().getDescription();
                getLogger().error(red("    Test failed at step: " + failingStep));
            }
            getLogger().error(red("    " + result.getNestedTestFailureCause().getShortenedMessage()));
        }
    }

    private void logPending(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.SUMMARY)) {
            getLogger().info(cyan(consoleHeading.bannerFor(TEST_PENDING, result.getTitle())));
        }
    }

    private void logSkipped(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.SUMMARY)) {
            getLogger().info(yellow(consoleHeading.bannerFor(TEST_SKIPPED, result.getTitle())));
        }
    }

    private void logSuccess(TestOutcome result) {
        if (loggingLevelIsAtLeast(LoggingLevel.SUMMARY)) {
            getLogger().info(green(consoleHeading.bannerFor(TEST_PASSED, result.getTitle())));
        }
    }

    public void stepStarted(ExecutedStepDescription description) {
        currentStep = description;
        nestedSteps.push(description.getName());
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            String indent = StringUtils.repeat("  ", nestedSteps.size());
            getLogger().info(white(indent + " * " + description.getTitle()));
        }
    }


    public void skippedStepStarted(ExecutedStepDescription description) {
        stepStarted(description);
    }

    public void stepFinished() {
        stepOut();
    }

    public void stepFailed(StepFailure failure) {

        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            String errorMessage = (failure.getException() != null) ? failure.getException().toString() : failure.getMessage();
            String failureType = analysis.resultFor(failure.getException()).name();
            getLogger().info(red("STEP {}: {}"), failureType, errorMessage);
        }
    }

    private void stepOut() {
        if (!nestedSteps.isEmpty()) {
            nestedSteps.pop();
        }
    }


    public void lastStepFailed(StepFailure failure) {
    }


    public void stepIgnored() {
        stepOut();
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE) && (!flaggedSteps.contains(currentStep))) {
            getLogger().info(yellow("      -> STEP IGNORED"));
            flaggedSteps.add(currentStep);
        }
    }

    public void stepPending() {
        stepOut();
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE) && (!flaggedSteps.contains(currentStep))) {
            getLogger().info(cyan("      -> STEP IS PENDING"));
            flaggedSteps.add(currentStep);
        }
    }


    public void stepPending(String message) {
        stepOut();
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE) && (!flaggedSteps.contains(currentStep))) {
            getLogger().info(cyan("      -> PENDING STEP ({})"), message);
            flaggedSteps.add(currentStep);
        }
    }


    public void testFailed(TestOutcome testOutcome, Throwable cause) {
    }


    public void testIgnored() {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(yellow("      -> TEST IGNORED"));
        }
    }

    @Override
    public void testSkipped() {
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info(yellow("      -> TEST SKIPPED"));
        }
    }

    @Override
    public void testPending() {
    }

    @Override
    public void testIsManual() {
    }


    public void notifyScreenChange() {
    }

    public void useExamplesFrom(DataTable table) {
    }

    @Override
    public void addNewExamplesFrom(DataTable table) {

    }

    public void exampleStarted(Map<String, String> data) {
    }

    public void exampleFinished() {
    }

    @Override
    public void assumptionViolated(String message) {
        if (loggingLevelIsAtLeast(LoggingLevel.QUIET)) {
            getLogger().error(red("      -> ASSUMPTION VIOLATED: " + message));
        }
    }

    @Override
    public void testRunFinished() {
        if (loggingLevelIsAtLeast(LoggingLevel.VERBOSE)) {
            getLogger().info("FINISHING TEST RUN");
        }
    }

    private boolean showColoredOutput() {
        return ThucydidesSystemProperty.SERENITY_CONSOLE_COLORS.booleanFrom(environmentVariables, false);
    }

    private String red(String text) {
        return (showColoredOutput()) ? ANSI_RED + text + ANSI_RESET : text;
    }

    private String grey(String text) {
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

    private String white(String text) {
        return text;
    }


}
