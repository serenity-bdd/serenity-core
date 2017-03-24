package net.thucydides.core.model;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.serenitybdd.core.model.FailureDetails;
import net.serenitybdd.core.time.SystemClock;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.images.ResizableImage;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.failures.FailureAnalysis;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.model.results.MergeStepResultStrategy;
import net.thucydides.core.model.results.StepResultMergeStragegy;
import net.thucydides.core.model.screenshots.Screenshot;
import net.thucydides.core.model.stacktrace.FailureCause;
import net.thucydides.core.model.stacktrace.RootCauseAnalyzer;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.reports.json.JSONConverter;
import net.thucydides.core.reports.saucelabs.LinkGenerator;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.statistics.service.TagProvider;
import net.thucydides.core.statistics.service.TagProviderService;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepFailureException;
import net.thucydides.core.steps.TestFailureCause;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import net.thucydides.core.util.NameConverter;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static ch.lambdaj.Lambda.*;
import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Lists.partition;
import static com.google.common.collect.Lists.reverse;
import static net.thucydides.core.model.ReportType.HTML;
import static net.thucydides.core.model.ReportType.ROOT;
import static net.thucydides.core.model.TestResult.*;
import static net.thucydides.core.util.NameConverter.withNoArguments;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hamcrest.Matchers.is;

/**
 * Represents the results of a test (or "scenario") execution. This
 * includes the narrative steps taken during the test, screenshots at each step,
 * the results of each step, and the overall result. A test scenario
 * can be associated with a user story using the UserStory annotation.
 * <p/>
 * A TestOutcome is stored after a test is executed. When the aggregate reports
 * are generated, the test outcome files are loaded into memory and processed.
 *
 * @author johnsmart
 */
public class TestOutcome {

    private static final String ISSUES = "issues";
    private static final String NEW_LINE = System.getProperty("line.separator");


    /**
     * The name of the method implementing this test.
     */
    @NotNull
    private String name;

    /**
     * A unique identifier for this test, if available
     */
    private String id;

    /**
     * The class containing the test method, if the test is implemented in a Java class.
     */
    private transient Class<?> testCase;

    private String testCaseName;

    /**
     * The list of steps recorded in this test execution.
     * Each step can contain other nested steps.
     */
    private final List<TestStep> testSteps = new ArrayList<>();

    /**
     * A test can be linked to the user story it tests using the Story annotation.
     */
    private Story userStory;

    private Optional<TestTag> featureTag = Optional.absent();

    private String title;
    private String description;
    private String backgroundTitle;
    private String backgroundDescription;

    /**
     *
     */
    private List<String> coreIssues;
    private List<String> additionalIssues;

    private List<String> coreVersions;
    private List<String> additionalVersions;

    private Set<TestTag> tags;

    /**
     * When did this test start.
     */
    private Long startTime;

    /**
     * How long did it last in milliseconds.
     */
    private long duration;

    /**
     * When did the current test batch start
     */
    private Date testRunTimestamp;

    /**
     * Identifies the project associated with this test.
     */
    private String project;

    private FailureCause testFailureCause;
    private TestFailureCause flakyTestFailureCause;
    private String testFailureClassname;
    private String testFailureMessage;
    private String testFailureSummary;

    /**
     * An externally-provided field that identifies the project that this test belongs to.
     * Read from the serenity.project.key system property.
     */
    private String projectKey;

    /**
     * Used to determine what result should be returned if there are no steps in this test.
     */
    private TestResult annotatedResult = null;


    /**
     * The session ID for this test, is a remote web driver was used.
     * If the tests are run on SauceLabs, this is used to generate a link to the corresponding report and video.
     */
    private String sessionId;

    /**
     * The driver used to run this test if it is a web test.
     */
    private String driver;

    /**
     * Keeps track of step groups.
     * If not empty, the top of the stack contains the step corresponding to the current step group - new steps should
     * be added here.
     */
    private transient Stack<TestStep> groupStack = new Stack<>();

    private transient IssueTracking issueTracking;

    private transient EnvironmentVariables environmentVariables;

    private transient LinkGenerator linkGenerator;

    /**
     * Test statistics, read from the statistics database.
     * This data is only loaded when required, and added to the TestOutcome using the corresponding setter.
     */
//    private TestStatistics statistics;

    /**
     * Returns a set of tag provider classes that are used to determine the tags to associate with a test outcome.
     */
    private transient TagProviderService tagProviderService;

    /**
     * An optional qualifier used to distinguish different runs of this test in data-driven tests.
     */
    private Optional<String> qualifier;

    /**
     * Used to store the table of examples used in an example-driven test outcome.
     */
    private DataTable dataTable;

    /**
     * Indicates that this is an imported manual test.
     */
    private boolean manual;

    /**
     * Indicates the test source e.g : junit/jbehave/cucumber
     */
    private String testSource;

    /**
     * Fields used for serialization
     */
    TestResult result;
    List<String> issues;
    List<String> versions;


    private static final Logger LOGGER = LoggerFactory.getLogger(TestOutcome.class);

    private TestOutcome() {
        groupStack = new Stack<>();
        this.additionalIssues = Lists.newArrayList();
        this.additionalVersions = Lists.newArrayList();
        this.issueTracking = Injectors.getInjector().getInstance(IssueTracking.class);
        this.linkGenerator = Injectors.getInjector().getInstance(LinkGenerator.class);
        this.qualifier = Optional.absent();
        this.groupStack = new Stack<>();
    }

    /**
     * The title is immutable once set. For convenience, you can create a test
     * run directly with a title using this constructor.
     *
     * @param name The name of the Java method that implements this test.
     */
    public TestOutcome(final String name) {
        this(name, null);
    }

    public TestOutcome(final String name, final Class<?> testCase) {
        this(name, testCase, ConfiguredEnvironment.getEnvironmentVariables());
    }

    private static String identifierFrom(String testName, Class<?> testCase, Story userStory) {
        String qualifier = null;
        if (testCase != null) {
            qualifier = testCase.getName();
        }

        if (userStory != null) {
            qualifier = userStory.getId();
        }

        return ((qualifier != null) ? qualifier + ":" : "") + testName;
    }

    /**
     * Create a test outcome based on a test method in a test class.
     * The requirement type will be derived if possible using the class package.
     *
     * @param name
     * @param testCase
     */
    public TestOutcome(final String name, final Class<?> testCase, EnvironmentVariables environmentVariables) {
        startTime = now().toDate().getTime();
        this.name = name;
        this.id = identifierFrom(name, testCase, userStory);
        this.testCase = testCase;
        this.testCaseName = nameOf(testCase);
        this.additionalIssues = Lists.newArrayList();
        this.additionalVersions = Lists.newArrayList();
        this.issueTracking = Injectors.getInjector().getInstance(IssueTracking.class);
        this.linkGenerator = Injectors.getInjector().getInstance(LinkGenerator.class);
        this.qualifier = Optional.absent();
        this.environmentVariables = environmentVariables;
        if (testCase != null) {
            setUserStory(leafRequirementDefinedIn().testCase(testCase));
        }
    }

    public static TestOutcomeWithEnvironmentBuilder inEnvironment(EnvironmentVariables environmentVariables) {
        return new TestOutcomeWithEnvironmentBuilder(environmentVariables);
    }

    private PackageBasedLeafRequirements leafRequirementDefinedIn() {
        return new PackageBasedLeafRequirements(getEnvironmentVariables());
    }

    /**
     * Fix the values of synthetic fields for serialization purposes
     */
    public void calculateDynamicFieldValues() {
        getTitle();
        this.result = getResult();
        this.issues = getIssues();
        this.versions = getVersions();
        this.tags = getTags();
    }

    private String nameOf(Class<?> testCase) {
        if (testCase != null) {
            return testCase.getCanonicalName();
        } else {
            return null;
        }
    }


    private TagProviderService getTagProviderService() {
        if (tagProviderService == null) {
            tagProviderService = Injectors.getInjector().getInstance(TagProviderService.class);
        }
        return tagProviderService;
    }

    public TestOutcome usingIssueTracking(IssueTracking issueTracking) {
        this.issueTracking = issueTracking;
        return this;
    }

    public TestOutcome asManualTest() {
        this.manual = true;
        addTag(TestTag.withName("Manual").andType("External Tests"));
        return this;
    }

    public TestOutcome withTestSource(String testSource) {
        this.testSource = testSource;
        return this;
    }


    public void setEnvironmentVariables(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public EnvironmentVariables getEnvironmentVariables() {
        if (environmentVariables == null) {
            environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get();
        }
        return environmentVariables;
    }

    protected TestOutcome(final String name, final Class<?> testCase, final Story userStory) {
        this(name, testCase, userStory, ConfiguredEnvironment.getEnvironmentVariables());
    }

    /**
     * A test outcome should relate to a particular test class or user story class.
     *
     * @param name      The name of the Java method implementing this test, if the test is a JUnit or TestNG test (for example)
     * @param testCase  The test class that contains this test method, if the test is a JUnit or TestNG test
     * @param userStory If the test is not implemented by a Java class (e.g. an easyb story), we may just use the Story class to
     *                  represent the story in which the test is implemented.
     */
    protected TestOutcome(final String name, final Class<?> testCase, final Story userStory, EnvironmentVariables environmentVariables) {
        startTime = now().toDate().getTime();
        this.name = name;
        this.id = identifierFrom(name, testCase, userStory);
        this.testCase = testCase;
        this.testCaseName = nameOf(testCase);
        this.additionalIssues = Lists.newArrayList();
        this.additionalVersions = Lists.newArrayList();
        if ((testCase != null) || (userStory != null)) {
            setUserStory(storyDefinedIn(testCase).or(userStory));
        }
        this.issueTracking = Injectors.getInjector().getInstance(IssueTracking.class);
        this.linkGenerator = Injectors.getInjector().getInstance(LinkGenerator.class);
        this.environmentVariables = environmentVariables;
        this.projectKey = ThucydidesSystemProperty.THUCYDIDES_PROJECT_KEY.from(environmentVariables, "");
    }

    private Optional<Story> storyDefinedIn(Class<?> testCase) {
        if (testCase == null) { return Optional.absent(); }
        return Optional.of(leafRequirementDefinedIn().testCase(testCase));
    }

    public TestOutcome copy() {
        return new TestOutcome(this.startTime,
                this.duration,
                this.title,
                this.description,
                this.name,
                this.id,
                this.testCase,
                this.testSteps,
                this.coreIssues,
                this.additionalIssues,
                this.tags,
                this.userStory,
                this.testFailureCause,
                this.testFailureClassname,
                this.testFailureMessage,
                this.testFailureSummary,
                this.annotatedResult,
                this.dataTable,
                this.qualifier,
                this.driver,
                this.manual,
                this.projectKey,
                this.environmentVariables);
    }

    protected TestOutcome(final Long startTime,
                          final long duration,
                          final String title,
                          final String description,
                          final String name,
                          final String id,
                          final Class<?> testCase,
                          final List<TestStep> testSteps,
                          final List<String> issues,
                          final List<String> additionalIssues,
                          final Set<TestTag> tags,
                          final Story userStory,
                          final FailureCause testFailureCause,
                          final String testFailureClassname,
                          final String testFailureMessage,
                          final String testFailureSummary,
                          final TestResult annotatedResult,
                          final DataTable dataTable,
                          final Optional<String> qualifier,
                          final String driver,
                          final boolean manualTest,
                          final String projectKey,
                          final EnvironmentVariables environmentVariables) {
        this.startTime = startTime;
        this.duration = duration;
        this.title = title;
        this.description = description;
        this.name = name;
        this.id = id;
        this.testCase = testCase;
        this.testCaseName = nameOf(testCase);
        addSteps(testSteps);
        this.coreIssues = removeDuplicates(issues);
        this.additionalVersions = removeDuplicates(additionalVersions);
        this.additionalIssues = additionalIssues;
        this.tags = tags;
        setUserStory(userStory);
        this.testFailureCause = testFailureCause;
        this.testFailureClassname = testFailureClassname;
        this.testFailureMessage = testFailureMessage;
        this.testFailureSummary = testFailureSummary;
        this.qualifier = qualifier;
        this.annotatedResult = annotatedResult;
        this.dataTable = dataTable;
        this.issueTracking = Injectors.getInjector().getInstance(IssueTracking.class);
        this.linkGenerator = Injectors.getInjector().getInstance(LinkGenerator.class);
        this.driver = driver;
        this.manual = manualTest;
        this.projectKey = projectKey;
        this.environmentVariables = environmentVariables;
    }

    private List<String> removeDuplicates(List<String> issues) {
        List<String> issuesWithNoDuplicates = Lists.newArrayList();
        if (issues != null) {
            for (String issue : issues) {
                if (!issuesWithNoDuplicates.contains(issue)) {
                    issuesWithNoDuplicates.add(issue);
                }
            }
        }
        return issuesWithNoDuplicates;
    }

    /**
     * Create a new test outcome instance for a given test class or user story.
     *
     * @param methodName The name of the Java method implementing this test,
     * @param testCase   The  JUnit or TestNG test class that contains this test method
     * @return A new TestOutcome object for this test.
     */
    public static TestOutcome forTest(final String methodName, final Class<?> testCase) {
        return new TestOutcome(methodName, testCase);
    }

    public TestOutcome withQualifier(String qualifier) {
        if (qualifier != null) {
            return new TestOutcome(this.startTime,
                    this.duration,
                    this.title,
                    this.description,
                    this.name,
                    this.id,
                    this.testCase,
                    this.testSteps,
                    this.coreIssues,
                    this.additionalIssues,
                    this.tags,
                    this.userStory,
                    this.testFailureCause,
                    this.testFailureClassname,
                    this.testFailureMessage,
                    this.testFailureSummary,
                    this.annotatedResult,
                    this.dataTable,
                    Optional.fromNullable(qualifier),
                    this.driver,
                    this.manual,
                    this.projectKey,
                    this.environmentVariables);
        } else {
            return this;
        }
    }

    public TestOutcome withIssues(List<String> issues) {
        return new TestOutcome(this.startTime,
                this.duration,
                this.title,
                this.description,
                this.name,
                this.id,
                this.testCase,
                this.testSteps,
                (issues == null) ? issues : ImmutableList.copyOf(issues),
                this.additionalIssues,
                this.tags,
                this.userStory,
                this.testFailureCause,
                this.testFailureClassname,
                this.testFailureMessage,
                this.testFailureSummary,
                this.annotatedResult,
                this.dataTable,
                this.qualifier,
                this.driver,
                this.manual,
                this.projectKey,
                this.environmentVariables);
    }

    public TestOutcome withTags(Set<TestTag> tags) {
        return new TestOutcome(this.startTime,
                this.duration,
                this.title,
                this.description,
                this.name,
                this.id,
                this.testCase,
                this.testSteps,
                this.coreIssues,
                this.additionalIssues,
                tags,
                this.userStory,
                this.testFailureCause,
                this.testFailureClassname,
                this.testFailureMessage,
                this.testFailureSummary,
                this.annotatedResult,
                this.dataTable,
                this.qualifier,
                this.driver,
                this.manual,
                this.projectKey,
                this.environmentVariables);
    }

    public TestOutcome withMethodName(String methodName) {
        if (methodName != null) {
            return new TestOutcome(this.startTime,
                    this.duration,
                    this.title,
                    this.description,
                    methodName,
                    identifierFrom(methodName, testCase, userStory),
                    this.testCase,
                    this.getTestSteps(),
                    this.coreIssues,
                    this.additionalIssues,
                    this.tags,
                    this.userStory,
                    this.testFailureCause,
                    this.testFailureClassname,
                    this.testFailureMessage,
                    this.testFailureSummary,
                    this.annotatedResult,
                    this.dataTable,
                    this.qualifier,
                    this.driver,
                    this.manual,
                    this.projectKey,
                    this.environmentVariables);
        } else {
            return this;
        }
    }

    /**
     * @return The name of the Java method implementing this test, if the test is implemented in Java.
     */
    public String getName() {
        return name;
    }

    public static TestOutcome forTestInStory(final String testName, final Story story) {
        return new TestOutcome(testName, null, story);
    }

    public static TestOutcome forTestInStory(final String testName, final Class<?> testCase, final Story story) {
        return new TestOutcome(testName, testCase, story);
    }

    @Override
    public String toString() {
        return getTitle() + ":" + join(extract(testSteps, on(TestStep.class).toString()));
    }

    /**
     * Return the human-readable name for this test.
     * This is derived from the test name for tests using a Java implementation, or can also be defined using
     * the Title annotation.
     *
     * @return the human-readable name for this test.
     */
    public String getTitle() {
        if (title == null) {
            title = obtainQualifiedTitleFromAnnotationOrMethodName();
        }
        return title;
    }

    public String getTitle(boolean qualified) {
        return (qualified) ? getTitle() : getFormatter().stripQualifications(getTitle());
    }

    public TitleBuilder getUnqualified() {
        return new TitleBuilder(this, false);
    }

    public TitleBuilder getQualified() {
        return new TitleBuilder(this, true);
    }

    public void setAllStepsTo(TestResult result) {
        for (TestStep step : testSteps) {
            step.setResult(result);
        }
    }

    public void addDataFrom(DataTable newDataTable) {
        if (dataTable == null) {
            dataTable = DataTable.withHeaders(newDataTable.getHeaders()).build();
        }
        dataTable.addRows(newDataTable.getRows());
    }

    public void clearForcedResult() {
        annotatedResult = null;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }

    public void resetFailingStepsCausedBy(Class<? extends Throwable> expected) {
        for (TestStep step : testSteps) {
            resetFailingStepsIn(step).causedBy(expected);
        }
        clearTestFailure();
    }

    private void clearTestFailure() {
        testFailureCause = null;
        testFailureClassname = null;
        testFailureMessage = null;
        annotatedResult = null;
    }

    private StepResetBuilder resetFailingStepsIn(TestStep step) {
        return new StepResetBuilder(step);
    }

    public StepReplacer replace(List<TestStep> stepsToMerge) {
        return new StepReplacer(stepsToMerge);
    }

    public void mergeMostRecentSteps(int maxStepsToMerge) {
        checkArgument(maxStepsToMerge > 0);

        List<TestStep> stepsToMerge = getLast(maxStepsToMerge).steps();
        TestStep mergedStep = merge(stepsToMerge);
        replace(stepsToMerge).with(mergedStep);
    }

    private GetLastStepBuilder getLast(int maxCount) {
        return new GetLastStepBuilder(maxCount);
    }

    public void updateOverallResults() {
        updateOverallResultsFor(testSteps);
    }

    private void updateOverallResultsFor(List<TestStep> testSteps) {
        for (TestStep testStep : testSteps) {
            updateOverallResultsFor(testStep.getChildren());
            updateOverallResultsFor(testStep);
        }
    }

    private void updateOverallResultsFor(TestStep testStep) {
        testStep.updateOverallResult();
    }

    public Optional<TestStep> getFailingStep() {
        List<TestStep> stepsInReverseOrder = Lists.newArrayList(getFlattenedTestSteps());
        Collections.reverse(stepsInReverseOrder);
        for (TestStep step : stepsInReverseOrder) {
            if (step.isError() || step.isFailure()) {
                return Optional.of(step);
            }
        }
        return Optional.absent();
    }

    public String getId() {
        updateIdIfNotDefinedForLegacyPersistedFormats();
        return id;
    }

    public String getParentId() {
        if (id != null && id.contains(";")) { return Splitter.on(";").splitToList(id).get(0); }

        return null;
    }

    private void updateIdIfNotDefinedForLegacyPersistedFormats() {
        if (id == null) {
            id = identifierFrom(testCaseName, testCase, userStory);
        }
    }

    public TestOutcome withId(String id) {
        return new TestOutcome(this.startTime,
                this.duration,
                this.title,
                this.description,
                this.name,
                id,
                this.testCase,
                this.testSteps,
                this.coreIssues,
                this.additionalIssues,
                tags,
                this.userStory,
                this.testFailureCause,
                this.testFailureClassname,
                this.testFailureMessage,
                this.testFailureSummary,
                this.annotatedResult,
                this.dataTable,
                this.qualifier,
                this.driver,
                this.manual,
                this.projectKey,
                this.environmentVariables);
    }

    public void updateTopLevelStepResultsTo(TestResult result) {
        for(TestStep step : testSteps) {
            step.setResult(result);
        }
        if (dataTable != null) {
            dataTable.updateRowResultsTo(result);
        }
    }

    public String getTestFailureSummary() {
        return testFailureSummary;
    }

    public TestFailureCause getFlakyTestFailureCause() {
        return flakyTestFailureCause;
    }

    public void setFlakyTestFailureCause(TestFailureCause flakyTestFailureCause) {
        this.flakyTestFailureCause = flakyTestFailureCause;
    }

    private static class TestOutcomeWithEnvironmentBuilder {
        private final EnvironmentVariables environmentVariables;

        public TestOutcomeWithEnvironmentBuilder(EnvironmentVariables environmentVariables) {
            this.environmentVariables = environmentVariables;
        }

        public Object forTest(String methodName, Class<?> testCase) {
            return new TestOutcome(methodName, testCase, environmentVariables);
        }

        public Object forTest(String methodName, Class<?> testCase, Story story) {
            return new TestOutcome(methodName, testCase, story, environmentVariables);
        }

    }

    private class GetLastStepBuilder {

        int maxCount;

        public GetLastStepBuilder(int maxCount) {
            this.maxCount = maxCount;
        }

        public List<TestStep> steps() {

            List<List<TestStep>> testStepPartitions = partition(reverse(getTestSteps()), maxCount);
            return reverse(testStepPartitions.get(0));
        }
    }

    private TestStep merge(List<TestStep> stepsToMerge) {
        TestStep mergedStep = stepsToMerge.get(0);
        for (TestStep nextStep : stepsToMerge.subList(1, stepsToMerge.size())) {
            mergedStep = mergeStep(mergedStep).into(nextStep);
        }
        return mergedStep;
    }

    private StepMergeBuilder mergeStep(TestStep step) {
        return new StepMergeBuilder(step);
    }

    class StepMergeBuilder {
        private final TestStep previousStep;

        private StepMergeBuilder(TestStep step) {
            this.previousStep = step;
        }

        public TestStep into(TestStep nextStep) {
            TestStep mergedStep = nextStep.addChildStep(previousStep);
            if (nextStep.getResult() == SKIPPED && (wasUnsuccessful(previousStep))) {
                nextStep.setResult(UNDEFINED);
            }
            mergedStep.setResult(merge(nextStep.getResult()).with(previousStep.getResult()));
            return mergedStep;
        }

        private StepResultMergeStragegy merge(TestResult nextStepResult) {
            return MergeStepResultStrategy.whenNextStepResultIs(nextStepResult);
        }
    }

    private boolean wasUnsuccessful(TestStep previousStep) {
        return (previousStep.getResult() == ERROR || previousStep.getResult() == FAILURE || previousStep.getResult() == COMPROMISED);
    }

    public boolean isTitleWithIssues() {
        return (!getTitle().equalsIgnoreCase(getUnqualified().getTitleWithLinks()));
    }

    public class TitleBuilder {
        private final boolean qualified;
        private final TestOutcome testOutcome;

        public TitleBuilder(TestOutcome testOutcome, boolean qualified) {
            this.testOutcome = testOutcome;
            this.qualified = qualified;
        }

        public String getTitleWithLinks() {
            String title = Inflector.getInstance().of(getTitle()).asATitle().toString();
            return getFormatter().addLinks(title);
        }

        public String getTitle() {
            return testOutcome.getTitle(qualified);
        }

    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBackgroundDescription(String description) {
        this.backgroundDescription = description.trim();
    }

    public void setBackgroundTitle(String title) {
        this.backgroundTitle = title.trim();
    }

    public String getDescription() {
        return description;
    }

    public String getBackgroundDescription() {
        return backgroundDescription;
    }

    public String getBackgroundTitle() {
        return backgroundTitle;
    }

    /**
     * Tests may have a description.
     * This can be defined with the scenarios (e.g. in the .feature files for Cucumber)
     * or defined elsewhere, such as in JIRA for manual tests.
     */
    public Optional<String> getDescriptionText() {
        if (getDescription() != null) {
            return Optional.of(description);
        } else if (title != null) {
            return getDescriptionFrom(title);
        } else {
            return Optional.absent();
        }
    }

    private Optional<String> getDescriptionFrom(String storedTitle) {
        List<String> multilineTitle = Lists.newArrayList(Splitter.on(Pattern.compile("\r?\n")).split(storedTitle));
        if (multilineTitle.size() > 1) {
            multilineTitle.remove(0);
            return Optional.of(Joiner.on(NEW_LINE).join(multilineTitle));
        } else {
            return Optional.absent();
        }

    }

    public String toJson() {
        JSONConverter jsonConverter = Injectors.getInjector().getInstance(JSONConverter.class);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            jsonConverter.toJson(this, outputStream);
            return outputStream.toString();
        } catch (IOException e) {
            LOGGER.error("serialization error for testOutcome with name \"" + this.getName() + "\"", e);
            return "";
        }
    }

    public String getTitleWithLinks() {
        return getFormatter().addLinks(getTitle());
    }

    private Formatter getFormatter() {
        return new Formatter(issueTracking);
    }

    private String obtainQualifiedTitleFromAnnotationOrMethodName() {
        String title = "";
        if ((qualifier != null) && (qualifier.isPresent())) {
            title = qualified(getBaseTitleFromAnnotationOrMethodName());
        } else {
            title = getBaseTitleFromAnnotationOrMethodName();
        }

        return title;
    }

    private String getBaseTitleFromAnnotationOrMethodName() {
        Optional<String> annotatedTitle = TestAnnotations.forClass(testCase).getAnnotatedTitleForMethod(name);
        return annotatedTitle.or(NameConverter.humanize(withNoArguments(name)));
    }

    private String qualified(String rootTitle) {
        return rootTitle + " [" + qualifier.get() + "]";
    }

    public String getStoryTitle() {
        return (userStory != null) ? getTitleFrom(userStory) : "";
    }

    public String getPath() {
        if (userStory != null) {
            return userStory.getPath();
        } else {
            return null;
        }
    }

    public String getPathId() {
        if (userStory != null) {
            return userStory.getId();
        } else {
            return getPath();
        }
    }


    private String getTitleFrom(final Story userStory) {
        return userStory.getName() == null ? "" : userStory.getName();
    }

    public String getReportName(final ReportType type) {
        return ReportNamer.forReportType(type).getNormalizedTestNameFor(this);
    }

    public String getSimpleReportName(final ReportType type) {
        ReportNamer reportNamer = ReportNamer.forReportType(type);
        return reportNamer.getSimpleTestNameFor(this);
    }

    public String getHtmlReport() {
        return getReportName(HTML);
    }

    public String getReportName() {
        return getReportName(ROOT);
    }

    public String getScreenshotReportName() {
        return getReportName(ROOT) + "_screenshots";
    }

    /**
     * An acceptance test is made up of a series of steps. Each step is in fact
     * a small test, which follows on from the previous one. The outcome of the
     * acceptance test as a whole depends on the outcome of all of the steps.
     *
     * @return A list of top-level test steps for this test.
     */
    public List<TestStep> getTestSteps() {
        return ImmutableList.copyOf(testSteps);
    }

    public boolean hasScreenshots() {
        return !getScreenshots().isEmpty();
    }

    public boolean hasRestQueries() {
        for (TestStep step : getFlattenedTestSteps()) {
            if (step.hasRestQuery()) {
                return true;
            }
        }
        return false;
    }

    public List<ScreenshotAndHtmlSource> getScreenshotAndHtmlSources() {
        List<TestStep> testStepsWithScreenshots = getFlattenedTestSteps();
//                select(getFlattenedTestSteps(),
//                having(on(TestStep.class).needsScreenshots()));

        return flatten(extract(testStepsWithScreenshots, on(TestStep.class).getScreenshots()));
    }

    public List<Screenshot> getScreenshots() {
        List<Screenshot> screenshots = new ArrayList<Screenshot>();

        List<TestStep> testStepsWithScreenshots = getFlattenedTestSteps();// select(getFlattenedTestSteps(),
//                having(on(TestStep.class).needsScreenshots()));

        for (TestStep currentStep : testStepsWithScreenshots) {
            screenshots.addAll(screenshotsIn(currentStep));
        }

        return ImmutableList.copyOf(screenshots);
    }

    private List<Screenshot> screenshotsIn(TestStep currentStep) {
        return convert(currentStep.getScreenshots(), toScreenshotsFor(currentStep));
    }

    private Converter<ScreenshotAndHtmlSource, Screenshot> toScreenshotsFor(final TestStep currentStep) {
        return new Converter<ScreenshotAndHtmlSource, Screenshot>() {
            public Screenshot convert(ScreenshotAndHtmlSource from) {
                return new Screenshot(from.getScreenshot().getName(),
                        currentStep.getDescription(),
                        widthOf(from.getScreenshot()),
                        currentStep.getException());
            }
        };
    }

    private int widthOf(final File screenshot) {
        try {
            return new ResizableImage(screenshot).getWidth();
        } catch (IOException e) {
            return ThucydidesSystemProperty.DEFAULT_WIDTH;
        }
    }

    public boolean hasNonStepFailure() {
        boolean stepsContainFailure = false;
        for (TestStep step : getFlattenedTestSteps()) {
            if (step.getResult() == FAILURE || step.getResult() == ERROR || step.getResult() == COMPROMISED) {
                stepsContainFailure = true;
            }
        }
        return (!stepsContainFailure && (getResult() == ERROR || getResult() == FAILURE || getResult() == COMPROMISED));
    }

    public List<TestStep> getFlattenedTestSteps() {
        List<TestStep> flattenedTestSteps = new ArrayList<>();
        for (TestStep step : getTestSteps()) {
            flattenedTestSteps.add(step);
            if (step.isAGroup()) {
                flattenedTestSteps.addAll(step.getFlattenedSteps());
            }
        }
        return flattenedTestSteps;
    }

    public List<TestStep> getLeafTestSteps() {
        List<TestStep> leafTestSteps = new ArrayList<TestStep>();
        for (TestStep step : getTestSteps()) {
            if (step.isAGroup()) {
                leafTestSteps.addAll(step.getLeafTestSteps());
            } else {
                leafTestSteps.add(step);
            }
        }
        return ImmutableList.copyOf(leafTestSteps);
    }

    /**
     * The outcome of the acceptance test, based on the outcome of the test
     * steps. If any steps fail, the test as a whole is considered a failure. If
     * any steps are pending, the test as a whole is considered pending. If all
     * of the steps are ignored, the test will be considered 'ignored'. If all
     * of the tests succeed except the ignored tests, the test is a success.
     * The test result can also be overridden using the 'setResult()' method.
     *
     * @return The outcome of this test.
     */
    public TestResult getResult() {
        if ((IGNORED == annotatedResult) || (SKIPPED == annotatedResult) || PENDING == annotatedResult) {
            return annotatedResult;
        }

        TestResult testResultFromFailureClassname = testResultFromFailureClassname();

        List<TestResult> overallResults = Lists.newArrayList(getCurrentTestResults());
        overallResults.add(testResultFromFailureClassname);

        TestResult testResultFromSteps = TestResultList.overallResultFrom(overallResults);
        return (annotatedResult != null) ? TestResultList.overallResultFrom(ImmutableList.of(testResultFromSteps, annotatedResult)) : testResultFromSteps;
    }

    private TestResult testResultFromFailureClassname() {
        if (testFailureClassname != null) {
            try {
                return new FailureAnalysis().resultFor(Class.forName(testFailureClassname));
            } catch (ReflectiveOperationException e) {
                return TestResult.ERROR;
            }
        }
        return TestResult.UNDEFINED;
    }

    public TestOutcome recordSteps(final List<TestStep> steps) {
        for (TestStep step : steps) {
            recordStep(step);
        }
        return this;
    }

    /**
     * Add a test step to this acceptance test.
     *
     * @param step a completed step to be added to this test outcome.
     * @return this TestOucome insstance - this is a convenience to allow method chaining.
     */
    public TestOutcome recordStep(final TestStep step) {
        checkNotNull(step.getDescription(), "The test step description was not defined.");
        if (inGroup()) {
            getCurrentStepGroup().addChildStep(step);
            renumberTestSteps();
        } else {
            addStep(step);
        }
        return this;
    }

    private void addStep(TestStep step) {
        testSteps.add(step);
        renumberTestSteps();
    }

    private void addSteps(List<TestStep> steps) {
        testSteps.addAll(steps);
        renumberTestSteps();
    }

    private void renumberTestSteps() {
        int count = 1;
        for (TestStep step : testSteps) {
            count = step.renumberFrom(count);
        }
    }

    private TestStep getCurrentStepGroup() {
        return groupStack.peek();
    }

    private boolean inGroup() {
        return !groupStack.empty();
    }

    /**
     * Get the feature that includes the user story tested by this test.
     * If no user story is defined, no feature can be returned, so the method returns null.
     * If a user story has been defined without a class (for example, one that has been reloaded),
     * the feature will be built using the feature name and id in the user story.
     *
     * @return The Feature defined for this TestOutcome, if any
     */
    public ApplicationFeature getFeature() {
        if ((getUserStory() != null) && (getUserStory().getFeature() != null)) {
            return getUserStory().getFeature();
        } else {
            return null;
        }
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    private List<TestResult> getCurrentTestResults() {
        List<TestResult> testResults = Lists.newArrayList();
        for (TestStep step : testSteps) {
            testResults.add(step.getResult());
        }
        return testResults;
    }

    /**
     * Creates a new step with this name and immediately turns it into a step group.
     */
    @Deprecated
    public void startGroup(final String groupName) {
        recordStep(new TestStep(groupName));
        startGroup();
    }

    public Optional<String> getQualifier() {
        return qualifier;
    }

    /**
     * Turns the current step into a group. Subsequent steps will be added as children of the current step.
     */
    public void startGroup() {
        if (!testSteps.isEmpty()) {
            groupStack.push(currentStep());
        }
    }

    /**
     * Finish the current group. Subsequent steps will be added after the current step.
     */
    public void endGroup() {
        if (!groupStack.isEmpty()) {
            groupStack.pop();
        }
    }

    /**
     * @return The current step is the last step in the step list, or the last step in the children of the current step group.
     */
    public TestStep currentStep() {
        checkState(!testSteps.isEmpty());

        if (!inGroup()) {
            return lastStepIn(testSteps);
        } else {
            TestStep currentStepGroup = groupStack.peek();
            return lastStepIn(currentStepGroup.getChildren());
        }
    }

    public TestStep lastStep() {
        checkState(!testSteps.isEmpty());

        if (!inGroup()) {
            return lastStepIn(testSteps);
        } else {
            TestStep currentStepGroup = groupStack.peek();
            return lastStepIn(currentStepGroup.getChildren());
        }

    }

    private TestStep lastStepIn(final List<TestStep> testSteps) {
        return testSteps.get(testSteps.size() - 1);
    }

    public TestStep currentGroup() {
        checkState(inGroup());
        return groupStack.peek();
    }

    public void setUserStory(Story story) {
        this.userStory = story;
        this.featureTag = FeatureTagAsDefined.in(story, getPath());
    }

    public void determineTestFailureCause(Throwable cause) {
        if (cause != null) {
            RootCauseAnalyzer rootCauseAnalyser = new RootCauseAnalyzer(SerenityManagedException.detachedCopyOf(cause));
            FailureCause rootCause = rootCauseAnalyser.getRootCause();
            this.testFailureClassname = rootCauseAnalyser.getRootCause().getErrorType();
            this.testFailureMessage = rootCauseAnalyser.getMessage();
            this.testFailureCause = rootCause;
            this.testFailureSummary = failureSummaryFrom(rootCause);
            this.setAnnotatedResult(new FailureAnalysis().resultFor(rootCause.exceptionClass()));
        } else {
            noTestFailureIsDefined();
        }
    }

    private String failureSummaryFrom(FailureCause rootCause) {
        return String.format("%s;%s;%s;%s",
                             getResult(),
                             rootCause.getErrorType(),
                             rootCause.getMessage(),
                             stackTraceSourceFrom(rootCause));
    }

    private String stackTraceSourceFrom(FailureCause rootCause) {
        if (rootCause.getStackTrace().length == 0) { return ""; }

        return rootCause.getStackTrace()[0].getFileName();
    }

    private void noTestFailureIsDefined() {
        this.testFailureCause = null;
        this.testFailureClassname = "";
        this.testFailureMessage = "";
        this.testFailureSummary = "";
    }

    public void appendTestFailure(TestFailureCause failureCause) {
        if (!failureCause.isDefined()) {
            noTestFailureIsDefined();
            return;
        }

        if (noStepHasFailedSoFar()) {
            this.testFailureClassname = failureCause.getRootCause().getErrorType();
            this.testFailureMessage = failureCause.getTestFailureMessage();
            this.testFailureCause = failureCause.getRootCause();
            this.testFailureSummary = failureSummaryFrom(failureCause.getRootCause());
            this.setAnnotatedResult(failureCause.getAnnotatedResult());
        } else if (isMoreSevereThanPreviousErrors(failureCause)) {
            this.testFailureClassname = AssertionError.class.getName();
            this.testFailureMessage = this.testFailureMessage + System.lineSeparator() + failureCause.getTestFailureMessage();
            this.testFailureSummary = failureSummaryFrom(failureCause.getRootCause());
            this.setAnnotatedResult(TestResultComparison.overallResultFor(this.getAnnotatedResult(), failureCause.getAnnotatedResult()));
        }

    }

    private boolean isMoreSevereThanPreviousErrors(TestFailureCause failureCause) {
        TestResult latestFailure = new FailureAnalysis().resultFor(this.getTestFailureCause().exceptionClass());
        return latestFailure.isMoreSevereThan(getResult());
    }

    public Optional<TestStep> testStepWithDescription(String expectedDescription) {
        for (TestStep step : reverse(getFlattenedTestSteps())) {
            if (step.getDescription().equalsIgnoreCase(expectedDescription)) {
                return Optional.of(step);
            }
        }
        return Optional.absent();
    }

    private boolean noStepHasFailedSoFar() {
        return this.testFailureCause == null;
    }

    public FailureCause getTestFailureCause() {
        return testFailureCause;
    }

    public FailureCause getNestedTestFailureCause() {
        for (TestStep step : getFlattenedTestSteps()) {
            if (step.getException() != null) {
                return step.getException();
            }
        }
        return getTestFailureCause();
    }

    public Optional<TestStep> firstStepWithErrorMessage() {
        for (TestStep step : getFlattenedTestSteps()) {
            if (isNotBlank(step.getErrorMessage())) {
                return Optional.of(step);
            }
        }
        return Optional.absent();
    }

    public Optional<String> testFailureMessage() {
        return Optional.fromNullable(testFailureMessage);

    }

    public String getErrorMessage() {
        if (firstStepWithErrorMessage().isPresent()) {
            return firstStepWithErrorMessage().get().getErrorMessage();
        }
        return testFailureMessage().or("");
    }

    public String getConciseErrorMessage() {
        if (firstStepWithErrorMessage().isPresent()) {
            return firstStepWithErrorMessage().get().getConciseErrorMessage();
        }
        return testFailureMessage().or("");
    }

    public void setTestFailureMessage(String testFailureMessage) {
        this.testFailureMessage = testFailureMessage;
    }

    public String getTestFailureMessage() {
        return testFailureMessage;
    }

    public String getTestFailureClassname() {
        return testFailureClassname;
    }

    public void setAnnotatedResult(final TestResult annotatedResult) {
        if (this.annotatedResult != PENDING){
            this.annotatedResult = (this.annotatedResult == null) ?
                    annotatedResult : TestResultComparison.overallResultFor(this.annotatedResult, annotatedResult);
        }
    }

    public void setResult(final TestResult annotatedResult) {
        this.annotatedResult = annotatedResult;
    }

    public TestResult getAnnotatedResult() {
        return annotatedResult;
    }

    public List<String> getAdditionalVersions() {
        return additionalVersions;
    }

    public List<String> getAdditionalIssues() {
        return additionalIssues;
    }

    private List<String> issues() {
        if (!thereAre(coreIssues)) {
            coreIssues = removeDuplicates(readIssues());
        }
        return coreIssues;
    }

    public List<String> getIssues() {
        List<String> allIssues = new ArrayList(issues());
        if (thereAre(additionalIssues)) {
            allIssues.addAll(additionalIssues);
        }
        return ImmutableList.copyOf(allIssues);
    }

    private List<String> versions() {
        if (!thereAre(coreVersions)) {
            coreVersions = removeDuplicates(readVersions());
        }
        return coreVersions;
    }

    private List<String> readVersions() {
        return TestOutcomeAnnotationReader.readVersionsIn(this);
    }


    public List<String> getVersions() {
        List<String> allVersions = new ArrayList(versions());
        if (thereAre(additionalVersions)) {
            allVersions.addAll(additionalVersions);
        }
        addVersionsDefinedInTagsTo(allVersions);
        return ImmutableList.copyOf(allVersions);
    }

    private void addVersionsDefinedInTagsTo(List<String> allVersions) {
        for (TestTag tag : getTags()) {
            if (tag.getType().equalsIgnoreCase("version") && (!allVersions.contains(tag.getName()))) {
                allVersions.add(tag.getName());
            }
        }
    }

    public Class<?> getTestCase() {
        if (testCase == null) {
            testCase = findTestCaseFromName(testCaseName);
        }
        return testCase;
    }

    private Class<?> findTestCaseFromName(String testCaseName) {
        try {
            return (testCaseName != null) ? Class.forName(testCaseName) : null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    private boolean thereAre(Collection<String> anyIssues) {
        return ((anyIssues != null) && (!anyIssues.isEmpty()));
    }

    public TestOutcome addVersion(String version) {
        if (!getVersions().contains(version)) {
            additionalVersions.add(version);
        }
        return this;
    }

    public TestOutcome addVersions(List<String> versions) {
        for (String version : versions) {
            addVersion(version);
        }
        return this;
    }

    public TestOutcome forProject(String project) {
        this.project = project;
        return this;
    }

    public String getProject() {
        return project;
    }

    public TestOutcome inTestRunTimestamped(DateTime testRunTimestamp) {
        setTestRunTimestamp(testRunTimestamp);
        return this;
    }

    public void setTestRunTimestamp(DateTime testRunTimestamp) {
        this.testRunTimestamp = testRunTimestamp.toDate();
    }


    public void addIssues(List<String> issues) {
        additionalIssues.addAll(issues);
    }

    private List<String> readIssues() {
        return TestOutcomeAnnotationReader.readIssuesIn(this);
    }

    public String getFormattedIssues() {
        Set<String> issues = Sets.newHashSet(getIssues());
        if (!issues.isEmpty()) {
            List<String> orderedIssues = sort(issues, on(String.class));
            return "(" + getFormatter().addLinks(join(orderedIssues, ", ")) + ")";
        } else {
            return "";
        }
    }

    public void isRelatedToIssue(String issue) {
        if (!issues().contains(issue)) {
            issues().add(issue);
        }
    }

    public void addFailingExternalStep(Throwable testFailureCause) {
        // Add as a sibling of the last deepest group
        addFailingStepAsSibling(testSteps, testFailureCause);
    }

    public void addFailingStepAsSibling(List<TestStep> testStepList, Throwable testFailureCause) {
        if (testStepList.isEmpty()) {
            addStep(failingStep(testFailureCause));
        } else {
            TestStep lastStep = lastStepIn(testStepList);
            if (lastStep.hasChildren()) {
                addFailingStepAsSibling(lastStep.children(), testFailureCause);
            } else {
                testStepList.add(failingStep(testFailureCause));
            }
        }
    }

    private TestStep failingStep(Throwable testFailureCause) {
        TestStep failingStep = new TestStep("Failure");
        failingStep.failedWith(testFailureCause);
        return failingStep;
    }

    public void lastStepFailedWith(StepFailure failure) {
        lastStepFailedWith(failure.getException());
    }

    public void lastStepFailedWith(Throwable testFailureCause) {
        determineTestFailureCause(testFailureCause);
        TestStep lastTestStep = testSteps.get(testSteps.size() - 1);
        lastTestStep.failedWith(new StepFailureException(testFailureCause.getMessage(), testFailureCause));
    }


    public Set<TestTag> getTags() {
        if (tags == null) {
            tags = getTagsUsingTagProviders(getTagProviderService().getTagProviders(getTestSource()));
        }
        return tags;
    }

    public void addUserStoryFeatureTo(Set<TestTag> augmentedTags) {
        if (userStory != null && userStory.getFeature() != null) {
            augmentedTags.add(TestTag.withName(userStory.getFeature().getName()).andType("feature"));
        }
    }

    private Set<TestTag> getTagsUsingTagProviders(List<TagProvider> tagProviders) {
        Set<TestTag> tags = Sets.newHashSet();
        for (TagProvider tagProvider : tagProviders) {
            try {
                tags.addAll(tagProvider.getTagsFor(this));
            } catch (Throwable theTagProviderFailedButThereIsntMuchWeCanDoAboutIt) {
                LOGGER.error("Tag provider " + tagProvider + " failure",
                        theTagProviderFailedButThereIsntMuchWeCanDoAboutIt);
            }
        }
        tags = removeRedundantTagsFrom(tags);
        return ImmutableSet.copyOf(tags);
    }

    private Set<TestTag> removeRedundantTagsFrom(Set<TestTag> tags) {
        Set<TestTag> optimizedTags = Sets.newHashSet();
        for (TestTag tag : tags) {
            if (!aMoreSpecificTagExistsThan(tag).in(tags)) {
                optimizedTags.add(tag);
            }
        }
        return optimizedTags;
    }

    private SpecificTagFinder aMoreSpecificTagExistsThan(TestTag tag) {
        return new SpecificTagFinder(tag);
    }

    public void setTags(Set<TestTag> tags) {
        this.tags = Sets.newHashSet(tags);
    }


    public void addTags(List<TestTag> tags) {
        Set<TestTag> updatedTags = Sets.newHashSet(getTags());
        updatedTags.addAll(tags);
        this.tags = ImmutableSet.copyOf(updatedTags);
    }

    public void addTag(TestTag tag) {
        Set<TestTag> updatedTags = Sets.newHashSet(getTags());
        updatedTags.add(tag);
        this.tags = ImmutableSet.copyOf(updatedTags);
    }

    public List<String> getIssueKeys() {
        return convert(getIssues(), toIssueKeys());
    }

    private Converter<String, String> toIssueKeys() {
        return new Converter<String, String>() {

            public String convert(String issueNumber) {
                String issueKey = issueNumber;
                if (issueKey.startsWith("#")) {
                    issueKey = issueKey.substring(1);
                }
                if (StringUtils.isNumeric(issueKey) && (getProjectPrefix() != null)) {
                    Joiner joiner = Joiner.on("-");
                    issueKey = joiner.join(getProjectPrefix(), issueKey);
                }
                return issueKey;
            }


        };
    }

    private String getProjectPrefix() {
        return ThucydidesSystemProperty.THUCYDIDES_PROJECT_KEY.from(getEnvironmentVariables());
    }

    public String getQualifiedMethodName() {
        if ((qualifier != null) && (qualifier.isPresent())) {
            String qualifierWithoutSpaces = qualifier.get().replaceAll(" ", "_");
            return getName() + "_" + qualifierWithoutSpaces;
        } else {
            return getName();
        }
    }

    public String getQualifiedId() {
        if ((qualifier != null) && (qualifier.isPresent())) {
            String qualifierWithoutSpaces = qualifier.get().replaceAll(" ", "_");
            return getId() + "_" + qualifierWithoutSpaces;
        } else {
            return getId();
        }
    }
    /**
     * Returns the name of the test prefixed by the name of the story.
     */
    public String getCompleteName() {
        if (StringUtils.isNotEmpty(getStoryTitle())) {
            return getStoryTitle() + ":" + getName();
        } else {
            return getTestCase() + ":" + getName();
        }
    }

    public void useExamplesFrom(DataTable table) {
        this.dataTable = table;
    }


    public void addNewExamplesFrom(DataTable table) {
        List<DataTableRow> updatedRows = table.getRows();
        if (table.getSize() > dataTable.getSize()) {
            for (int rowNumber = dataTable.getSize(); rowNumber < updatedRows.size(); rowNumber++) {
                dataTable.appendRow(updatedRows.get(rowNumber));
            }
        }
    }

    public void moveToNextRow() {
        if (dataTable != null && !dataTable.atLastRow()) {
            dataTable.nextRow();
        }
    }

    public void updateCurrentRowResult(TestResult result) {
        dataTable.currentRow().hasResult(result);
    }

    public boolean dataIsPredefined() {
        return dataTable.hasPredefinedRows();
    }

    public void addRow(Map<String, ?> data) {
        dataTable.addRow(data);
    }

    public void addRow(DataTableRow dataTableRow) {
        dataTable.addRow(dataTableRow);
    }


    public int getTestCount() {
        return isDataDriven() ? getDataTable().getSize() : 1;
    }

    public int getImplementedTestCount() {
        return (getStepCount() > 0) ? getTestCount() : 0;
    }

    public int countResults(TestResult expectedResult) {
        return countResults(expectedResult, TestType.ANY);
    }

    public int countResults(TestResult expectedResult, TestType expectedType) {
        if (annotatedResult != null && !annotatedResult.executedResultsCount()) {
            return annotatedResultCount(expectedResult, expectedType);
        }

        if (isDataDriven()) {
            return countDataRowsWithResult(expectedResult, expectedType);
        }

        return (getResult() == expectedResult) && (typeCompatibleWith(expectedType)) ? 1 : 0;
    }

    private int annotatedResultCount(TestResult expectedResult, TestType expectedType) {
        if ((annotatedResult == expectedResult) && (typeCompatibleWith(expectedType))) {
            return (isDataDriven()) ? dataTable.getSize() : 1;
        } else {
            return 0;
        }
    }

    public boolean typeCompatibleWith(TestType testType) {
        switch (testType) {
            case MANUAL:
                return isManual();
            case AUTOMATED:
                return !isManual();
            default:
                return true;
        }
    }

    private int countDataRowsWithResult(TestResult expectedResult, TestType expectedType) {
        int matchingRowCount = 0;
        if (typeCompatibleWith(expectedType)) {
            for (DataTableRow row : getDataTable().getRows()) {
                matchingRowCount += (row.getResult() == expectedResult) ? 1 : 0;
            }
        }
        return matchingRowCount;
//        List<DataTableRow> matchingRows
//                = filter(having(on(DataTableRow.class).getResult(), is(expectedResult)), getDataTable().getRows());
//        return matchingRows.size();
    }

    public int countNestedStepsWithResult(TestResult expectedResult, TestType testType) {
        if (isDataDriven()) {
            return countDataRowStepsWithResult(expectedResult, testType);
        } else {
            return (getResult() == expectedResult) && (typeCompatibleWith(testType)) ? getNestedStepCount() : 0;
        }
    }

    private int countDataRowStepsWithResult(TestResult expectedResult, TestType testType) {
        int rowsWithResult = countDataRowsWithResult(expectedResult, testType);
        int totalRows = getDataTable().getSize();
        int totalSteps = getNestedStepCount();
        return totalSteps * rowsWithResult / totalRows;
    }

    public Optional<String> getTagValue(String tagType) {
        if (tagType.equalsIgnoreCase(ISSUES) && !getIssueKeys().isEmpty()) {
            return Optional.of(Joiner.on(",").join(getIssueKeys()));
        } else {
            for (TestTag tag : getTags()) {
                if (tag.getType().equalsIgnoreCase(tagType)) {
                    return Optional.of(tag.getName());
                }
            }
        }
        return Optional.absent();
    }

    public boolean hasIssue(String issue) {
        return getIssues().contains(issue);
    }

    public boolean hasTag(TestTag tag) {
        return getTags().contains(tag);
    }


    public boolean hasAMoreGeneralFormOfTag(TestTag specificTag) {
        for (TestTag tag : getTags()) {
            if (specificTag.isAsOrMoreSpecificThan(tag)) {
                return true;
            }
        }
        return false;
    }


    public void setStartTime(DateTime startTime) {
        this.startTime = startTime.toDate().getTime();
    }

    public void clearStartTime() {
        this.startTime = null;
    }

    public boolean isManual() {
        return manual;
    }

    public boolean isStartTimeNotDefined() {
        return this.startTime == null;
    }

    private SystemClock getSystemClock() {
        return Injectors.getInjector().getInstance(SystemClock.class);
    }

    private DateTime now() {
        return getSystemClock().getCurrentTime();
    }

    public OptionalElements has() {
        return new OptionalElements(this);
    }

    public static class OptionalElements {

        private final TestOutcome testOutcome;

        public OptionalElements(TestOutcome testOutcome) {
            this.testOutcome = testOutcome;
        }

        public boolean testRunTimestamp() {
            return testOutcome.testRunTimestamp != null;
        }
    }

    public Integer getStepCount() {
        return testSteps.size();
    }

    public Integer getRunningStepCount() {
        return runningStepCountOf(testSteps);
    }

    private Integer runningStepCountOf(List<TestStep> steps) {
        if (tailOf(steps).isPresent() && tailOf(steps).get().hasChildren()) {
            return runningStepCountOf(tailOf(steps).get().getChildren());
        } else {
            return steps.size();
        }
    }

    private Optional<TestStep> tailOf(List<TestStep> testSteps) {
        return (testSteps.isEmpty()) ? Optional.<TestStep>absent() : Optional.of(testSteps.get(testSteps.size() - 1));
    }

    public Integer getNestedStepCount() {
        return getFlattenedTestSteps().size();
    }

    public Integer getSuccessCount() {
        return count(successfulSteps()).in(getLeafTestSteps());
    }

    public Integer getFailureCount() {
        return count(failingSteps()).in(getLeafTestSteps());
    }

    public Integer getErrorCount() {
        return count(errorSteps()).in(getLeafTestSteps());
    }

    public Integer getCompromisedCount() {
        return count(compromisedSteps()).in(getLeafTestSteps());
    }

    public Integer getIgnoredCount() {
        return count(ignoredSteps()).in(getLeafTestSteps());
    }

    public Integer getSkippedOrIgnoredCount() {
        return getIgnoredCount() + getSkippedCount();
    }

    public Integer getSkippedCount() {
        return count(skippedSteps()).in(getLeafTestSteps());
    }

    public Integer getPendingCount() {
        List<TestStep> allTestSteps = getLeafTestSteps();
        return select(allTestSteps, having(on(TestStep.class).isPending())).size();
    }

    public Boolean isSuccess() {
        return (getResult() == SUCCESS);
    }

    public Boolean isFailure() {
        return (getResult() == FAILURE);
    }

    public Boolean isCompromised() {
        return (getResult() == COMPROMISED);
    }

    public Boolean isError() {
        return (getResult() == ERROR);
    }

    public Boolean isPending() {
        return (getResult() == PENDING); //((getResult() == PENDING) || (getStepCount() == 0));
    }

    public Boolean isSkipped() {
        return (getResult() == SKIPPED) || (getResult() == IGNORED);
    }

    public Story getUserStory() {
        return userStory;
    }

    public void recordDuration() {
        setDuration(System.currentTimeMillis() - startTime);
    }

    public void setDuration(final long duration) {
        this.duration = duration;
    }

    public Long getDuration() {
        if ((duration == 0) && (testSteps.size() > 0)) {
            return sum(testSteps, on(TestStep.class).getDuration());
        } else {
            return duration;
        }
    }

    /**
     * @return The total duration of all of the tests in this set in milliseconds.
     */
    public double getDurationInSeconds() {
        return TestDuration.of(getDuration()).inSeconds();
    }

    /**
     * Returns the link to the associated video (e.g. from Saucelabs) for this test.
     *
     * @return a URL.
     */
    public String getVideoLink() {
        return linkGenerator.linkFor(this);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    StepCountBuilder count(StepFilter filter) {
        return new StepCountBuilder(filter);
    }

    public static class StepCountBuilder {
        private final StepFilter filter;

        public StepCountBuilder(StepFilter filter) {
            this.filter = filter;
        }

        int in(List<TestStep> steps) {
            int count = 0;
            for (TestStep step : steps) {
                if (filter.apply(step)) {
                    count++;
                }
            }
            return count;
        }
    }


    public Integer countTestSteps() {
        return countLeafStepsIn(testSteps);
    }

    private Integer countLeafStepsIn(List<TestStep> testSteps) {
        int leafCount = 0;
        for (TestStep step : testSteps) {
            if (step.isAGroup()) {
                leafCount += countLeafStepsIn(step.getChildren());
            } else {
                leafCount++;
            }
        }
        return leafCount;
    }

    abstract class StepFilter {
        abstract boolean apply(TestStep step);

    }

    StepFilter successfulSteps() {
        return new StepFilter() {
            @Override
            boolean apply(TestStep step) {
                return step.isSuccessful();
            }
        };
    }

    StepFilter failingSteps() {
        return new StepFilter() {
            @Override
            boolean apply(TestStep step) {
                return step.isFailure();
            }
        };
    }

    StepFilter errorSteps() {
        return new StepFilter() {
            @Override
            boolean apply(TestStep step) {
                return step.isError();
            }
        };
    }

    StepFilter compromisedSteps() {
        return new StepFilter() {
            @Override
            boolean apply(TestStep step) {
                return step.isCompromised();
            }
        };
    }

    StepFilter ignoredSteps() {
        return new StepFilter() {
            @Override
            boolean apply(TestStep step) {
                return step.isIgnored();
            }
        };
    }

    StepFilter skippedSteps() {
        return new StepFilter() {
            @Override
            boolean apply(TestStep step) {
                return step.isSkipped();
            }
        };
    }

    public DateTime getStartTime() {
        return new DateTime(startTime);
    }

    public DateTime getTestRunTimestamp() {
        return new DateTime(testRunTimestamp);
    }

    public boolean isDataDriven() {
        return dataTable != null;
    }

    final static private List<String> NO_HEADERS = Lists.newArrayList();

    private List<TestStep> getStepChildren() {
        List<TestStep> firstLevel = firstNonPreconditionStepChildren();
        if (firstLevel.size() > 0 && firstLevel.get(0).getDescription().matches("^\\[\\d+\\]\\s\\{.+")) {
            firstLevel = firstLevel.get(0).getChildren();
        }
        return firstLevel;
    }


    public List<String> getExampleFields() {
        return (isDataDriven()) ? getDataTable().getHeaders() : NO_HEADERS;
    }

    private List<TestStep> firstNonPreconditionStepChildren() {

        for(TestStep step : getTestSteps()) {
            if (!step.isAPrecondition() && step.hasChildren()) {
                return step.getChildren();
            }
        }
        return new ArrayList<>();
    }

    public String getDataDrivenSampleScenario() {
        if (!isDataDriven() || getTestSteps().isEmpty() || !atLeastOneStepHasChildren()) {
            return "";
        }

        if (dataTable.scenarioOutline().isPresent()) {
            return dataTable.scenarioOutline().get();
        }

        StringBuilder sampleScenario = new StringBuilder();
        for (TestStep step : getStepChildren()) {
            sampleScenario.append(
                    withPlaceholderSubstitutes(step.getDescription()))
                    .append("\n");
        }
        return sampleScenario.length() > 1 ? sampleScenario.substring(0, sampleScenario.length() - 1) : "";
    }

    private String withPlaceholderSubstitutes(String stepName) {
        if (dataTable == null || dataTable.getRows().isEmpty()) { return stepName; }

        return dataTable.restoreVariablesIn(stepName);
    }


    private boolean atLeastOneStepHasChildren() {
        return !filter(having(on(TestStep.class).hasChildren(), is(true)), getTestSteps()).isEmpty();
    }

    public DataTable getDataTable() {
        return dataTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestOutcome that = (TestOutcome) o;

        if (manual != that.manual) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null) return false;
        if (testCaseName != null ? !testCaseName.equals(that.testCaseName) : that.testCaseName != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (userStory != null ? !userStory.equals(that.userStory) : that.userStory != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (testCase != null ? testCase.hashCode() : 0);
        result = 31 * result + (userStory != null ? userStory.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        result = 31 * result + (manual ? 1 : 0);
        return result;
    }


    public Optional<TestTag> getFeatureTag() {
        if (!featureTag.isPresent()) {
            featureTag = FeatureTagAsDefined.in(userStory, getPath());
        }
        return featureTag;
    }

    private class StepResetBuilder {
        TestStep step;

        public StepResetBuilder(TestStep step) {
            this.step = step;
        }

        public void causedBy(Class<? extends Throwable> expected) {
            if (step.getException().getErrorType().equals(expected.getName())) {
                step.clearException();
                step.setResult(TestResult.SUCCESS);
            }
            for (TestStep childStep : step.getChildren()) {
                resetFailingStepsIn(childStep).causedBy(expected);
            }
        }
    }

    private static class SpecificTagFinder {
        private final TestTag tag;

        public SpecificTagFinder(TestTag tag) {
            this.tag = tag;
        }

        public boolean in(Set<TestTag> tags) {
            for (TestTag otherTag : tags) {
                if ((otherTag != tag) && (otherTag.isAsOrMoreSpecificThan(tag))) {
                    return true;
                }
            }
            return false;
        }
    }

    public class StepReplacer {
        List<TestStep> stepsToReplace;

        public StepReplacer(List<TestStep> stepsToReplace) {
            this.stepsToReplace = stepsToReplace;
        }

        public void with(TestStep mergedStep) {
            removeSteps(stepsToReplace);
            addStep(mergedStep);
            renumberTestSteps();
        }
    }

    private void removeSteps(List<TestStep> stepsToReplace) {
        List<TestStep> currentTestSteps = ImmutableList.copyOf(testSteps);
        for (TestStep testStep : currentTestSteps) {
            if (stepsToReplace.contains(testStep)) {
                testSteps.remove(testStep);
            }
        }
    }

    public FailureDetails getFailureDetails() {
        return new FailureDetails(this);
    }

    public String getTestSource() {
        return testSource;
    }

    public void setTestSource(String testSource) {
        this.testSource = testSource;
    }

}
