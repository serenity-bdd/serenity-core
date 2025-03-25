package net.thucydides.model.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import net.serenitybdd.model.di.ModelInfrastructure;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.model.exceptions.SerenityManagedException;
import net.serenitybdd.model.exceptions.TheErrorType;
import net.serenitybdd.model.model.FailureDetails;
import net.serenitybdd.model.strings.Joiner;
import net.serenitybdd.model.time.SystemClock;
import net.thucydides.model.ThucydidesSystemProperty;
import net.serenitybdd.annotations.TestAnnotations;
import net.thucydides.model.domain.failures.FailureAnalysis;
import net.thucydides.model.domain.features.ApplicationFeature;
import net.thucydides.model.domain.flags.Flag;
import net.thucydides.model.domain.flags.FlagProvider;
import net.thucydides.model.domain.formatters.ReportFormatter;
import net.thucydides.model.domain.results.MergeStepResultStrategy;
import net.thucydides.model.domain.results.StepResultMergeStragegy;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.issues.IssueKeyFormat;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.domain.screenshots.Screenshot;
import net.thucydides.model.domain.stacktrace.FailureCause;
import net.thucydides.model.domain.stacktrace.RootCauseAnalyzer;
import net.thucydides.model.reports.json.JSONConverter;
import net.thucydides.model.reports.remoteTesting.LinkGenerator;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.statistics.service.TagProvider;
import net.thucydides.model.statistics.service.TagProviderService;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.model.steps.StepFailureException;
import net.thucydides.model.steps.TestFailureCause;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.util.NameConverter;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.partition;
import static com.google.common.collect.Lists.reverse;
import static java.util.Arrays.asList;
import static net.thucydides.model.domain.TestType.ANY;
import static net.thucydides.model.domain.TestType.MANUAL;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Represents the results of a test (or "scenario") execution. This
 * includes the narrative steps taken during the test, screenshots at each step,
 * the results of each step, and the overall result. A test getscenario
 * can be associated with a user story using the UserStory annotation.
 * <p>
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
    private String name;

    /**
     * A unique identifier for this test, if available
     */
    private String id;

    /**
     * Unique identifier for the test scenario, which will be different to the id for parameterized tests.
     */
    private String scenarioId;

    /**
     * The class containing the test method, if the test is implemented in a Java class.
     */
    private transient Class<?> testCase;

    private String testCaseName;

    /**
     * The name of the test method in a JUnit test case.
     */
    private String methodName;

    /**
     * The list of steps recorded in this test execution.
     * Each step can contain other nested steps.
     */
    private List<TestStep> testSteps = Collections.synchronizedList(new ArrayList<>());

    /**
     * A test can be linked to the user story it tests using the Story annotation.
     */
    private Story userStory;

    private Optional<TestTag> featureTag = Optional.empty();

    private String title;
    private String description;
    private String backgroundTitle;
    private String backgroundDescription;

    /**
     *
     */
    private List<String> coreIssues;
    private final List<String> additionalIssues;

    private List<String> coreVersions;
    private List<String> additionalVersions;

    private Set<TestTag> tags;
    private transient Set<TestTag> allTags;

    /**
     * When did this test start.
     */
    private ZonedDateTime startTime;

    /**
     * When did this start finish
     */
    private ZonedDateTime endTime;

    /**
     * How long did it last in milliseconds.
     */
    private long duration;

    /**
     * When did the current test batch start
     */
    private ZonedDateTime testRunTimestamp;

    /**
     * Identifies the project associated with this test.
     */
    private String project;

    private Rule rule;

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
     * The last tested version for manual tests, defined by the @last-tested tag in Cucumber scenarios
     */
    private String lastTested;

    /**
     * True if a manual test has been marked as up-to-date. Only relevant if lastTested is defined.
     */
    private boolean isManualTestingUpToDate;

    /**
     * A relative or absolute link to test evidence related to the last manual test
     */
    private List<String> manualTestEvidence;

    /**
     * Keeps track of step groups.
     * If not empty, the top of the stack contains the step corresponding to the current step group - new steps should
     * be added here.
     */
    private transient Stack<TestStep> groupStack = new Stack<>();

    private transient IssueTracking issueTracking;

    private transient EnvironmentVariables environmentVariables;

    private final transient LinkGenerator linkGenerator;

    private transient FlagProvider flagProvider;

    /**
     * Returns a set of tag provider classes that are used to determine the tags to associate with a test outcome.
     */
    private transient TagProviderService tagProviderService;

    /**
     * An optional qualifier used to distinguish different runs of this test in data-driven tests.
     */
    private Optional<String> qualifier;

    private String context;

    /**
     * Used to store the table of examples used in an example-driven test outcome.
     */
    private DataTable dataTable;

    /**
     * Indicates that this is an imported manual test.
     */
    private boolean manual;

    /**
     * Indicates something interesting about this test.
     * Currently used mainly to indicate if a failing test represents a new failure.
     */
    private Set<? extends Flag> flags;

    /**
     * Indicates the test source e.g : junit/jbehave/cucumber
     */
    private String testSource;

    /**
     * The actors used in a Screenplay test
     */
    private final List<CastMember> actors;

    private ExternalLink externalLink;

    /**
     * An indication of the order of appearance that this scenario should appear in the story or feature.
     * Used for JUnit tests.
     */
    private Integer order;

    /**
     * Fields used for serialization
     */
    TestResult result;
    List<String> issues;
    List<String> versions;

    private transient List<TestStep> flattenedSteps = null;
    private transient List<TestStep> leafSteps = null;

    /**
     * Scenario outline text.
     */
    private String scenarioOutline;
    private String testOutlineName;
    private String testData;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestOutcome.class);
    private Double durationInSeconds;

    private TestOutcome() {
        groupStack = new Stack<>();
        this.additionalIssues = new ArrayList<>();
        this.additionalVersions = new ArrayList<>();
        this.actors = new CopyOnWriteArrayList<>();
        this.issueTracking = ModelInfrastructure.getIssueTracking();
        this.linkGenerator = ModelInfrastructure.getLinkGenerator();
        this.flagProvider = ModelInfrastructure.getFlagProvider();
        this.qualifier = Optional.empty();
        this.context = null;
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
        String identifer = null;
        if (testCase != null) {
            identifer = testCase.getName().replaceAll("\\$", ".");
        }

        if (userStory != null) {
            identifer = userStory.getId();
        }

        return ((identifer != null) ? identifer + ":" : "") + testName;
    }

    /**
     * Create a test outcome based on a test method in a test class.
     * The requirement type will be derived if possible using the class package.
     *
     * @param name
     * @param testCase
     */
    public TestOutcome(final String name, final Class<?> testCase, EnvironmentVariables environmentVariables) {
        startTime = ZonedDateTime.now();
        this.name = name;
        this.id = identifierFrom(name, testCase, userStory);
        this.scenarioId = id;
        this.testCase = testCase;
        this.testCaseName = nameOf(testCase);
        this.methodName = name;
        this.additionalIssues = new ArrayList<>();
        this.additionalVersions = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.issueTracking = ModelInfrastructure.getIssueTracking();
        this.linkGenerator = ModelInfrastructure.getLinkGenerator();
        this.flagProvider = ModelInfrastructure.getFlagProvider();
        this.qualifier = Optional.empty();
        this.environmentVariables = environmentVariables;
        this.context = null;//contextFrom(environmentVariables);
        if (this.userStory == null && testCase != null) {
            setUserStory(leafRequirementDefinedIn().testCase(testCase));
        }
    }

    public TestOutcome(final String name, final Class<?> testCase, String context, EnvironmentVariables environmentVariables) {
        startTime = ZonedDateTime.now();
        this.name = name;
        this.id = identifierFrom(name, testCase, userStory);
        this.scenarioId = id;
        this.testCase = testCase;
        this.methodName = name;
        this.testCaseName = nameOf(testCase);
        this.additionalIssues = new ArrayList<>();
        this.additionalVersions = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.issueTracking = ModelInfrastructure.getIssueTracking();
        this.linkGenerator = ModelInfrastructure.getLinkGenerator();
        this.flagProvider = ModelInfrastructure.getFlagProvider();
        this.qualifier = Optional.empty();
        this.environmentVariables = environmentVariables;
        this.context = context;
        if (testCase != null) {
            setUserStory(leafRequirementDefinedIn().testCase(testCase));
        }
    }

    private String contextFrom(EnvironmentVariables environmentVariables) {
        return (environmentVariables == null) ? null : ThucydidesSystemProperty.CONTEXT.from(environmentVariables);
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
//        this.allTags = addFeatureTagTo(this.tags);
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
            tagProviderService = ModelInfrastructure.getTagProviderService();
        }
        return tagProviderService;
    }

    public TestOutcome usingIssueTracking(IssueTracking issueTracking) {
        this.issueTracking = issueTracking;
        return this;
    }

    public TestOutcome setToManual() {
        this.manual = true;
        addTag(TestTag.withName("manual").andType("tag"));
        return this;
    }

    public TestOutcome withTestSource(String testSource) {
        this.testSource = testSource;
        return this;
    }


    /**
     * Set the current flag provider; only used for testing purposes.
     */
    protected TestOutcome withFlagProvider(FlagProvider flagProvider) {
        this.flagProvider = flagProvider;
        this.flags = null;
        return this;
    }

    public void setEnvironmentVariables(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.context = contextFrom(environmentVariables);
    }

    public EnvironmentVariables getEnvironmentVariables() {
        if (environmentVariables == null) {
            environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
            if (this.context == null) {
                this.context = contextFrom(environmentVariables);
            }
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
        startTime = ZonedDateTime.now();
        this.name = name;
        this.id = identifierFrom(name, testCase, userStory);
        this.scenarioId = id;
        this.testCase = testCase;
        this.methodName = name;
        this.testCaseName = nameOf(testCase);
        this.additionalIssues = new ArrayList<>();
        this.additionalVersions = new ArrayList<>();
        this.actors = new ArrayList<>();
        if (userStory != null) {
            setUserStory(userStory);
        } else if ((testCase != null) && storyDefinedIn(testCase).isPresent()) {
            setUserStory(storyDefinedIn(testCase).get());
        }
        this.issueTracking = ModelInfrastructure.getIssueTracking();
        this.linkGenerator = ModelInfrastructure.getLinkGenerator();
        this.flagProvider = ModelInfrastructure.getFlagProvider();
        this.environmentVariables = environmentVariables;
        this.context = contextFrom(environmentVariables);
        this.order = TestCaseOrder.definedIn(testCase, name);

        this.projectKey = ThucydidesSystemProperty.SERENITY_PROJECT_KEY.from(environmentVariables, "");
    }

    protected TestOutcome(ZonedDateTime startTime, final String name, final Class<?> testCase, final Story userStory, EnvironmentVariables environmentVariables) {
        this(name, testCase, userStory, environmentVariables);
        this.startTime = startTime;
    }

    private Optional<Story> storyDefinedIn(Class<?> testCase) {
        if (testCase == null) {
            return Optional.empty();
        }
        return Optional.of(leafRequirementDefinedIn().testCase(testCase));
    }

    public TestOutcome copy() {
        return new TestOutcome(this.startTime,
                this.duration,
                this.title,
                this.description,
                this.name,
                this.id,
                this.scenarioId,
                this.methodName,
                this.testCase,
                this.testSteps,
                this.coreIssues,
                this.additionalIssues,
                this.actors,
                this.tags,
                this.featureTag.orElse(null),
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
                this.isManualTestingUpToDate,
                this.lastTested,
                this.manualTestEvidence,
                this.projectKey,
                this.environmentVariables,
                this.externalLink,
                this.context,
                this.testSource,
                this.testOutlineName);
    }

    protected TestOutcome(final ZonedDateTime startTime,
                          final long duration,
                          final String title,
                          final String description,
                          final String name,
                          final String id,
                          final String scenarioId,
                          final String methodName,
                          final Class<?> testCase,
                          final List<TestStep> testSteps,
                          final List<String> issues,
                          final List<String> additionalIssues,
                          final List<CastMember> actors,
                          final Set<TestTag> tags,
                          final TestTag featureTag,
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
                          final boolean isManualTestingUpToDate,
                          final String lastTested,
                          final List<String> testEvidence,
                          final String projectKey,
                          final EnvironmentVariables environmentVariables,
                          final ExternalLink externalLink,
                          final String context,
                          final String testSource,
                          final String testOutlineName) {
        this.startTime = startTime;
        this.duration = duration;
        this.title = title;
        this.description = description;
        this.name = name;
        this.id = id;
        this.scenarioId = scenarioId;
        this.testCase = testCase;
        this.testCaseName = nameOf(testCase);
        this.methodName = methodName;
        setTestSteps(testSteps);
        this.coreIssues = removeDuplicates(issues);
        this.additionalVersions = removeDuplicates(additionalVersions);
        this.additionalIssues = additionalIssues;
        this.actors = actors;
        this.tags = tags;
        this.featureTag = Optional.ofNullable(featureTag);
        this.allTags = addFeatureTagTo(this.tags);
        setUserStory(userStory);
        this.testFailureCause = testFailureCause;
        this.testFailureClassname = testFailureClassname;
        this.testFailureMessage = testFailureMessage;
        this.testFailureSummary = testFailureSummary;
        this.qualifier = qualifier;
        this.annotatedResult = annotatedResult;
        this.dataTable = dataTable;
        this.issueTracking = ModelInfrastructure.getIssueTracking();
        this.linkGenerator = ModelInfrastructure.getLinkGenerator();
        this.flagProvider = ModelInfrastructure.getFlagProvider();
        this.driver = driver;
        this.manual = manualTest;
        this.manualTestEvidence = testEvidence;
        this.isManualTestingUpToDate = isManualTestingUpToDate;
        this.lastTested = lastTested;
        this.projectKey = projectKey;
        this.environmentVariables = environmentVariables;
        this.externalLink = externalLink;
        this.context = context;
        this.testSource = testSource;
        this.testOutlineName = testOutlineName;
    }

    List<String> calculateNestPath(Class<?> testCase) {
        List<String> nestPath = new ArrayList<>();
        if (testCase != null) {
            String testCaseName = testCase.getName();
            String className = testCaseName.substring(testCaseName.lastIndexOf(".") + 1);
            if (className.contains("$")) {
                nestPath = asList(className.split("\\$"));
            }
        }
        return nestPath;
    }

    private List<String> removeDuplicates(List<String> issues) {
        List<String> issuesWithNoDuplicates = new ArrayList<>();
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
                    this.scenarioId,
                    this.methodName,
                    this.testCase,
                    this.testSteps,
                    this.coreIssues,
                    this.additionalIssues,
                    this.actors,
                    this.tags,
                    this.featureTag.orElse(null),
                    this.userStory,
                    this.testFailureCause,
                    this.testFailureClassname,
                    this.testFailureMessage,
                    this.testFailureSummary,
                    this.annotatedResult,
                    this.dataTable,
                    Optional.ofNullable(qualifier),
                    this.driver,
                    this.manual,
                    this.isManualTestingUpToDate,
                    this.lastTested,
                    this.manualTestEvidence,
                    this.projectKey,
                    this.environmentVariables,
                    this.externalLink,
                    this.context,
                    this.testSource,
                    this.testOutlineName);
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
                this.scenarioId,
                this.methodName,
                this.testCase,
                this.testSteps,
                (issues == null) ? issues : new ArrayList<>(issues),
                this.additionalIssues,
                this.actors,
                this.tags,
                this.featureTag.orElse(null),
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
                this.isManualTestingUpToDate,
                this.lastTested,
                this.manualTestEvidence,
                this.projectKey,
                this.environmentVariables,
                this.externalLink,
                this.context,
                this.testSource,
                this.testOutlineName);
    }

    public TestOutcome withTags(Set<TestTag> tags) {
        return new TestOutcome(this.startTime,
                this.duration,
                this.title,
                this.description,
                this.name,
                this.id,
                this.scenarioId,
                this.methodName,
                this.testCase,
                this.testSteps,
                this.coreIssues,
                this.additionalIssues,
                this.actors,
                tags,
                this.featureTag.orElse(null),
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
                this.isManualTestingUpToDate,
                this.lastTested,
                this.manualTestEvidence,
                this.projectKey,
                this.environmentVariables,
                this.externalLink,
                this.context,
                this.testSource,
                this.testOutlineName);
    }

    public TestOutcome withStartTime(ZonedDateTime startTime) {
        return new TestOutcome(startTime,
                this.duration,
                this.title,
                this.description,
                this.name,
                this.id,
                this.scenarioId,
                this.methodName,
                this.testCase,
                this.testSteps,
                this.coreIssues,
                this.additionalIssues,
                this.actors,
                this.tags,
                this.featureTag.orElse(null),
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
                this.isManualTestingUpToDate,
                this.lastTested,
                this.manualTestEvidence,
                this.projectKey,
                this.environmentVariables,
                this.externalLink,
                this.context,
                this.testSource,
                this.testOutlineName);
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
        return getTitle() + ":" + testSteps.stream()
                .map(TestStep::toString)
                .collect(Collectors.joining(", "));
    }

    /**
     * Return the human-readable name for this test.
     * This is derived from the test name for tests using a Java implementation, or can also be defined using
     * the Title annotation.
     *
     * @return the human-readable name for this test.
     */
    public String getTitle() {
        if (this.title == null) {
            this.title = obtainQualifiedTitleFromAnnotationOrMethodName();
        }
        return this.title;
    }

    public String getTitle(boolean qualified) {
        return (qualified) ? getTitle() : getFormatter().stripQualifications(getTitle());
    }

    public TitleBuilder getUnqualified() {
        return new TitleBuilder(this, issueTracking, getEnvironmentVariables(), false);
    }

    public TitleBuilder getQualified() {
        return new TitleBuilder(this, issueTracking, getEnvironmentVariables(), true);
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

    public String getBrowser() {
        return (driver.contains(":")) ? driver.substring(0, driver.indexOf(":")) : driver;
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
        List<TestStep> flattenedSteps = getFlattenedTestSteps();
        for (int step = flattenedSteps.size() - 1; step >= 0; step--) {
            if (flattenedSteps.get(step).isError() || flattenedSteps.get(step).isFailure()) {
                return Optional.of(flattenedSteps.get(step));
            }
        }
        return Optional.empty();
    }

    public String getId() {
        updateIdIfNotDefinedForLegacyPersistedFormats();
        return id;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public String getParentId() {
        if (id != null && id.contains(";")) {
            return Splitter.on(";").splitToList(id).get(0);
        }

        return null;
    }

    private void updateIdIfNotDefinedForLegacyPersistedFormats() {
        if (id == null) {
            id = identifierFrom(testCaseName, testCase, userStory);
        }
    }

    public TestOutcome withId(String id) {
        this.id = id;
        return this;
    }

    public TestOutcome withScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
        return this;
    }

    public void updateTopLevelStepResultsTo(TestResult result) {
        for (TestStep step : testSteps) {
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

    public boolean hasTagWithName(String tagName) {
        for (TestTag tag : Optional.ofNullable(getAllTags()).orElse(Collections.emptySet())) {
            if (tag.getName().equalsIgnoreCase(tagName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTagWithType(String tagType) {
        return Optional.ofNullable(getAllTags()).orElse(Collections.emptySet())
                .stream()
                .anyMatch(tag -> tag.getType().equalsIgnoreCase(tagType));
    }

    public boolean hasTagWithTypes(List<String> tagTypes) {
        return Optional.ofNullable(getAllTags()).orElse(Collections.emptySet())
                .stream()
                .anyMatch(tag -> tagTypes.contains(tag.getType()));
    }

    public int getDataTableRowCount() {
        if (dataTable == null) {
            return 0;
        }
        return dataTable.getSize();
    }

    public int getTestStepCount() {
        return getTestSteps().size();
    }

    public void castActor(String name) {
        if (actors.stream().noneMatch(actor -> actor.getName().equalsIgnoreCase(name))) {
            actors.add(new CastMember(name));
        }
    }

    public void assignFact(String name, String fact) {
        if (actors.stream().noneMatch(actor -> actor.getName().equalsIgnoreCase(name))) {
            actors.add(new CastMember(name));
        }

        actors.stream().filter(actor -> actor.getName().equalsIgnoreCase(name)).forEach(
                crewMember -> crewMember.addFact(fact)
        );
    }

    public void assignAbility(String name, String ability) {
        if (actors.stream().noneMatch(actor -> actor.getName().equalsIgnoreCase(name))) {
            actors.add(new CastMember(name));
        }

        actors.stream().filter(actor -> actor.getName().equalsIgnoreCase(name)).forEach(
                crewMember -> crewMember.addAbility(ability)
        );
    }

    public void assignDescriptionToActor(String name, String description) {
        if (actors.stream().noneMatch(actor -> actor.getName().equalsIgnoreCase(name))) {
            actors.add(new CastMember(name));
        }

        actors.stream().filter(actor -> actor.getName().equalsIgnoreCase(name)).forEach(
                crewMember -> crewMember.withDescription(description)
        );
    }

    public void setManualTestEvidence(List<String> manualTestEvidence) {
        this.manualTestEvidence = manualTestEvidence;
    }

    public List<String> getManualTestEvidence() {
        return manualTestEvidence;
    }

    public List<ManualTestEvidence> getRenderedManualTestEvidence() {
        return manualTestEvidence.stream()
                .map(ManualTestEvidence::from)
                .collect(Collectors.toList());
    }

    public void setLink(ExternalLink externalLink) {
        if (isDataDriven()) {
            getLatestTopLevelTestStep().ifPresent(
                    latestStep -> latestStep.setExternalLink(externalLink)
            );
        } else {
            this.externalLink = externalLink;
        }
    }

    public boolean hasNoSteps() {
        return (testSteps == null || testSteps.isEmpty());
    }

    public TestOutcome withSteps(List<TestStep> childSteps) {
        updateTestSteps(childSteps);
        return this;
    }

    public boolean isUnsuccessful() {
        return (getResult() == TestResult.COMPROMISED || getResult() == TestResult.ERROR || getResult() == TestResult.FAILURE);
    }

    public boolean isAJUnit5Test() {
        return testSource == null || (testSource.equalsIgnoreCase("JUnit5"));
    }

    public void updateDataTableResult(int row, TestResult result) {
        if (dataTable != null && dataTable.getSize() > row) {
            dataTable.updateRowResult(row, result);
        }
    }

    public TestOutcome withTestMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public TestResult getStepResult(int stepNumber) {
        if (stepNumber >= getTestSteps().size()) {
            return TestResult.UNDEFINED;
        }
        List<TestResult> results = getTestSteps()
                .get(stepNumber)
                .getFlattenedSteps()
                .stream()
                .map(TestStep::getResult)
                .collect(Collectors.toList());

        return TestResultList.overallResultFrom(results);
    }

    public String getTestOutlineName() {
        return testOutlineName;
    }

    public void setTestOutlineName(String testOutlineName) {
        this.testOutlineName = testOutlineName;
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
            if (nextStep.getResult() == TestResult.SKIPPED && (wasUnsuccessful(previousStep))) {
                nextStep.setResult(TestResult.UNDEFINED);
            }
            mergedStep.setResult(merge(nextStep.getResult()).with(previousStep.getResult()));
            return mergedStep;
        }

        private StepResultMergeStragegy merge(TestResult nextStepResult) {
            return MergeStepResultStrategy.whenNextStepResultIs(nextStepResult);
        }
    }

    private boolean wasUnsuccessful(TestStep previousStep) {
        return (previousStep.getResult() == TestResult.ERROR || previousStep.getResult() == TestResult.FAILURE || previousStep.getResult() == TestResult.COMPROMISED);
    }

    public boolean isTitleWithIssues() {
        return (!getTitle().equalsIgnoreCase(getUnqualified().getTitleWithLinks()));
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
            return Optional.empty();
        }
    }

    private Optional<String> getDescriptionFrom(String storedTitle) {
        List<String> multilineTitle = new ArrayList(Splitter.on(Pattern.compile("\r?\n")).splitToList(storedTitle));
        if (multilineTitle.size() > 1) {
            multilineTitle.remove(0);
            return Optional.of(Joiner.on(NEW_LINE).join(multilineTitle));
        } else {
            return Optional.empty();
        }

    }

    public String toJson() {
        JSONConverter jsonConverter = ModelInfrastructure.getJsonConverter();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            jsonConverter.toJson(this, outputStream);
            return outputStream.toString(Charset.defaultCharset());
        } catch (IOException e) {
            LOGGER.error("serialization error for testOutcome with name \"" + this.getName() + "\"", e);
            return "";
        }
    }

    public String getTitleWithLinks() {
        return getFormatter().addLinks(getTitle());
    }

    private ReportFormatter getFormatter() {
        return new ReportFormatter(issueTracking);
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
        return annotatedTitle.orElse(NameConverter.humanize(NameConverter.withNoArguments(name)));
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
        return ReportNamer.forReportType(type).getNormalizedReportNameFor(this);
    }

    public String getSimpleReportName(final ReportType type) {
        ReportNamer reportNamer = ReportNamer.forReportType(type);
        return reportNamer.getSimpleTestNameFor(this);
    }

    public String getHtmlReport() {
        return getReportName(ReportType.HTML);
    }

    public String getReportName() {
        return getReportName(ReportType.ROOT);
    }

    public String getScreenshotReportName() {
        return getReportName(ReportType.ROOT) + "_screenshots";
    }

    /**
     * An acceptance test is made up of a series of steps. Each step is in fact
     * a small test, which follows on from the previous one. The outcome of the
     * acceptance test as a whole depends on the outcome of all of the steps.
     *
     * @return A list of top-level test steps for this test.
     */
    public List<TestStep> getTestSteps() {
        return annotatedStepsFrom(testSteps);
    }

    public Optional<TestStep> getLatestTopLevelTestStep() {
        int latestStep = testSteps.size() - 1;
        return (latestStep >= 0) ?
                Optional.of(annotatedStepsFrom(testSteps).get(latestStep))
                : Optional.empty();
    }

    private List<TestStep> annotatedStepsFrom(List<TestStep> testSteps) {
        // For manual tests, all top level steps respect the manual test result if defined
        if (isManual()) {
            return testSteps.stream().map(
                    step -> step.withResult(getResult()).asManual()
            ).collect(Collectors.toList());
        } else {
            return testSteps;
        }
    }

    public boolean hasScreenshots() {
        for (TestStep step : getFlattenedTestSteps()) {
            if (step.hasScreenshots()) {
                return true;
            }
        }
        return false;
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

        return testStepsWithScreenshots.stream()
                .flatMap(testStep -> testStep.getScreenshots().stream())
                .collect(Collectors.toList());
    }


    public List<Screenshot> getScreenshots() {

        List<Screenshot> screenshots = new ArrayList<>();

        List<TestStep> testStepsWithScreenshots = getFlattenedTestSteps();

        for (TestStep currentStep : testStepsWithScreenshots) {
            screenshots.addAll(currentStep.getRenderedScreenshots());
        }

        screenshots.sort(Comparator.comparing(Screenshot::getTimestamp));

        return screenshots;
    }


    /**
     * Find the first and last screenshots for each aggregate step, and every screenshots for leaf steps.
     */
    public List<Screenshot> getStepScreenshots() {

        List<Screenshot> screenshots = new ArrayList<>();

        testSteps.forEach(
                step -> screenshots.addAll(step.getRenderedScreenshots())
        );

        return screenshots;
    }


    public boolean hasNonStepFailure() {
        boolean stepsContainFailure = false;
        for (TestStep step : getFlattenedTestSteps()) {
            if (step.getResult() == TestResult.FAILURE || step.getResult() == TestResult.ERROR || step.getResult() == TestResult.COMPROMISED) {
                stepsContainFailure = true;
            }
        }
        return (!stepsContainFailure && (getResult() == TestResult.ERROR || getResult() == TestResult.FAILURE || getResult() == TestResult.COMPROMISED));
    }

    public List<TestStep> getFlattenedTestSteps() {
        if (flattenedSteps == null) {
            this.flattenedSteps = getLatestFlattenedTestSteps();
        }
        return flattenedSteps;
    }

    public List<TestStep> getLatestFlattenedTestSteps() {
        List<TestStep> testSteps = getTestSteps();
        return testSteps.stream()
                .flatMap(step -> Stream.concat(Stream.of(step),
                        step.isAGroup() ? step.getFlattenedSteps().stream() : Stream.empty()))
                .collect(Collectors.toList());
    }

    public List<TestStep> getLeafTestSteps() {
        if (leafSteps == null) {
            List<TestStep> leafTestSteps = new ArrayList<>();
            for (TestStep step : getTestSteps()) {
                if (step.isAGroup()) {
                    leafTestSteps.addAll(step.getLeafTestSteps());
                } else {
                    leafTestSteps.add(step);
                }
            }
            this.leafSteps = leafTestSteps;
        }
        return leafSteps;
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
        if (result != null) {
            return result;
        }

        if (isManual() && (annotatedResult != null)) {
            return annotatedResult;
        }

        if ((TestResult.IGNORED == annotatedResult) || (TestResult.SKIPPED == annotatedResult) || TestResult.PENDING == annotatedResult) {
            return annotatedResult;
        }

        TestResult testResultFromFailureClassname = testResultFromFailureClassname();

        List<TestResult> overallResults = new ArrayList<>(getCurrentTestResults());
        overallResults.add(testResultFromFailureClassname);

        TestResult testResultFromSteps = TestResultList.overallResultFrom(overallResults);
        return (annotatedResult != null) ? TestResultList.overallResultFrom(asList(testResultFromSteps, annotatedResult)) : testResultFromSteps;
    }

    private TestResult testResultFromFailureClassname() {
        if (StringUtils.isNotEmpty(testFailureClassname)) {
            try {
                return new FailureAnalysis().resultFor(Class.forName(testFailureClassname));
            } catch (ReflectiveOperationException e) {
                return HeuristicTestResult.from(testFailureClassname);
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

    public TestOutcome recordChildSteps(final List<TestStep> steps) {
        if (currentStep().isPresent()) {
            steps.forEach(step -> currentStep().get().addChildStep(step));
        } else {
            steps.forEach(step -> recordStep(step));
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
        Preconditions.checkNotNull(step.getDescription(), "The test step description was not defined.");
        if (inGroup()) {
            getCurrentStepGroup().addChildStep(step);
            renumberTestSteps();
        } else {
            addStep(step);
        }
        return this;
    }

    private void addStep(TestStep step) {
        List<TestStep> updatedSteps = new ArrayList<>(testSteps);
        updatedSteps.add(step);
        renumberTestSteps(updatedSteps);
        updateTestSteps(updatedSteps);
    }

    private void setTestSteps(List<TestStep> steps) {
        this.testSteps = steps;
        this.flattenedSteps = null;
        // renumberTestSteps(testSteps);
    }

    private void renumberTestSteps(List<TestStep> testSteps) {
        int count = 1;
        for (TestStep step : testSteps) {
            count = step.renumberFrom(count);
        }
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

    public boolean inGroup() {
        return !groupStack.empty();
    }

    public boolean isTopLevelGroup() {
        if (isDataDriven()) {
            return (groupStack.size() == 1);
        } else {
            return (groupStack.isEmpty());
        }
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

    private synchronized List<TestResult> getCurrentTestResults() {
        return testSteps.stream()
                .map(TestStep::getResult)
                .collect(Collectors.toList());
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
        currentStep().ifPresent(
                step -> groupStack.push(step)
        );
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
    public Optional<TestStep> currentStep() {
        if (testSteps.isEmpty()) {
            return Optional.empty();
        }

        if (!inGroup()) {
            return Optional.ofNullable(lastStepIn(testSteps));
        } else {
            TestStep currentStepGroup = groupStack.peek();
            return Optional.ofNullable(lastStepIn(currentStepGroup.getChildren()));
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
        if (testSteps.isEmpty()) {
            return null;
        }
        return testSteps.get(testSteps.size() - 1);
    }

    public TestStep currentGroup() {
        checkState(inGroup());
        return groupStack.peek();
    }

    public void setUserStory(Story story) {
        this.userStory = story;
//        this.featureTag = FeatureTagAsDefined.in(story, getPath());
    }

    public void determineTestFailureCause(Throwable cause) {
        // Find any previous failing steps (which can happen for soft assertions)
        List<FailureCause> stepFailureCauses = getLatestFlattenedTestSteps().stream()
                .map(TestStep::getException)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if ((cause != null) && !causeIsContainedIn(cause,stepFailureCauses)){
            // Add this new failure to the list
            RootCauseAnalyzer rootCauseAnalyser = new RootCauseAnalyzer(SerenityManagedException.detachedCopyOf(cause));
            FailureCause rootCause = rootCauseAnalyser.getRootCause();
            stepFailureCauses.add(rootCause);

            // Now there is at least one failure in this test
            if (stepFailureCauses.size() == 1) {
                // Single cause of failure
                this.testFailureClassname = rootCauseAnalyser.getRootCause().getErrorType();
                this.testFailureMessage = rootCauseAnalyser.getMessage();
                this.testFailureCause = rootCause;
                this.testFailureSummary = failureSummaryFrom(rootCause);
                this.setAnnotatedResult(new FailureAnalysis().resultFor(rootCause.exceptionClass()));
            } else {
                List<String> errorMessages = stepFailureCauses.stream()
                        .map(FailureCause::getMessage)
                        .collect(Collectors.toList());
                Throwable aggregatedErrors = new MultipleAssertionErrors(errorMessages);
                RootCauseAnalyzer aggregateRootCauseAnalyser = new RootCauseAnalyzer(aggregatedErrors);
                FailureCause aggregateRootCause = aggregateRootCauseAnalyser.getRootCause();
                this.testFailureClassname = aggregateRootCauseAnalyser.getRootCause().getErrorType();
                this.testFailureMessage = aggregateRootCauseAnalyser.getMessage();
                this.testFailureCause = aggregateRootCause;
                this.testFailureSummary = aggregatedErrors.getMessage();
                this.setAnnotatedResult(new FailureAnalysis().resultFor(aggregateRootCause.exceptionClass()));

            }
        }
        // Can this ever happen?
        if (stepFailureCauses.isEmpty()) {
            noTestFailureIsDefined();
        }
    }

    private boolean causeIsContainedIn(Throwable cause, List<FailureCause> stepFailureCauses) {
        return stepFailureCauses.stream()
                .map(FailureCause::getOriginalCause)
                .anyMatch(failureCause -> failureCause == cause);
    }

    private String failureSummaryFrom(FailureCause rootCause) {
        return String.format("%s;%s;%s;%s",
                getResult(),
                rootCause.getErrorType(),
                rootCause.getMessage(),
                stackTraceSourceFrom(rootCause));
    }

    private String stackTraceSourceFrom(FailureCause rootCause) {
        if (rootCause.getStackTrace().length == 0) {
            return "";
        }

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
        return Optional.empty();
    }

    private boolean noStepHasFailedSoFar() {
        return this.testFailureCause == null;
    }

    public FailureCause getTestFailureCause() {
        return testFailureCause;
    }

    public String getTestFailureErrorType() {
        if (getTestFailureCause() == null) {
            return "";
        }
        return getTestFailureCause().getErrorType();
    }

    public FailureCause getNestedTestFailureCause() {
        return getLatestFlattenedTestSteps().stream()
                .filter(step -> step.getException() != null)
                .map(TestStep::getException)
                .findFirst()
                .orElse(getTestFailureCause());
    }

    public Optional<TestStep> firstStepWithErrorMessage() {
        return getLatestFlattenedTestSteps().stream()
                .filter(step -> isNotBlank(step.getErrorMessage()))
                .distinct()
                .findFirst();
    }

    public List<String> stepErrorMessages() {
        return getLatestFlattenedTestSteps().stream()
                .map(TestStep::getErrorMessage)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    public Optional<String> testFailureMessage() {
        return Optional.ofNullable(testFailureMessage);

    }

    public String getErrorMessage() {

        List<String> stepErrorMessages = stepErrorMessages();
        if (stepErrorMessages.isEmpty()) {
            return testFailureMessage().orElse("");
        }
        if (stepErrorMessages.size() == 1) {
            return stepErrorMessages.get(0);
        }
        return stepErrorMessages.stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public String getConciseErrorMessage() {
        if (firstStepWithErrorMessage().isPresent()) {
            return firstStepWithErrorMessage().get().getConciseErrorMessage();
        }
        return testFailureMessage().orElse("");
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
        if (this.annotatedResult != TestResult.PENDING) {
            this.annotatedResult = (this.annotatedResult == null) ?
                    annotatedResult : TestResultComparison.overallResultFor(this.annotatedResult, annotatedResult);
        }
    }

    public void overrideAnnotatedResult(final TestResult annotatedResult) {
        this.annotatedResult = annotatedResult;
        this.result = annotatedResult;
    }

    public void setResult(final TestResult annotatedResult) {
        this.annotatedResult = annotatedResult;
    }

    public void overrideResult(final TestResult result) {
        this.result = result;
        this.annotatedResult = result;
    }

    public TestOutcome withResult(final TestResult annotatedResult) {
        this.setResult(annotatedResult);
        return this;
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
        return Stream.concat(
                        issues != null ? issues.stream() : issues().stream(),
                        thereAre(additionalIssues) ? additionalIssues.stream() : Stream.empty()
                )
                .distinct() // Ensure distinct values
                .sorted()   // Sort the values naturally
                .collect(Collectors.toList()); // Collect into a List
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
        List<String> allVersions = new ArrayList<>(versions());
        if (thereAre(additionalVersions)) {
            allVersions.addAll(additionalVersions);
        }
        addVersionsDefinedInTagsTo(allVersions);
        return allVersions;
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

    public String getMethodName() {
        return methodName;
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

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public String getProject() {
        return project;
    }

    public TestOutcome inTestRunTimestamped(ZonedDateTime testRunTimestamp) {
        setTestRunTimestamp(testRunTimestamp);
        return this;
    }

    public void setTestRunTimestamp(ZonedDateTime testRunTimestamp) {
        this.testRunTimestamp = testRunTimestamp;
    }


    public void addIssues(List<String> issues) {
        Set<String> aggreatateIssues = new HashSet<>(additionalIssues);
        aggreatateIssues.addAll(issues);

        additionalIssues.clear();
        additionalIssues.addAll(aggreatateIssues);
    }

    private List<String> readIssues() {
        return TestOutcomeAnnotationReader.readIssuesIn(this);
    }

    public String getFormattedIssues() {
        Set<String> issues = new HashSet<>(getIssues());
        if (!issues.isEmpty()) {
            List<String> orderedIssues = issues.stream().sorted().collect(Collectors.toList());
            String formattedIssues = orderedIssues.stream().collect(Collectors.joining(", "));
            return "(" + getFormatter().addLinks(formattedIssues) + ")";
        } else {
            return "";
        }
    }

    public void isRelatedToIssue(String issue) {
        if (!issues().contains(issue)) {
            issues().add(issue);
        }
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
        List<TestStep> leafSteps = getLatestFlattenedTestSteps();
        if (!leafSteps.isEmpty()) {
            TestStep lastTestStep = leafSteps.get(leafSteps.size() - 1);
            lastTestStep.failedWith(new StepFailureException(testFailureCause.getMessage(), testFailureCause));
        }
    }

    public void testFailedWith(Throwable cause) {
        determineTestFailureCause(cause);
    }

    public Set<TestTag> getTags() {
        if (tags == null) {
            tags = getTagsUsingTagProviders(getTagProviderService().getTagProviders(getTestSource()));
        }
        return tags;
    }

    public Set<TestTag> getAllTags() {
        return getTags();
//        if (allTags == null) {
//            allTags = addFeatureTagTo(getTags());
//        }
//        return allTags;
    }

    private Set<TestTag> addFeatureTagTo(Set<TestTag> tags) {
        Set<TestTag> allTags = (tags == null) ? new HashSet<>() : new HashSet<>(tags);
        getFeatureTag().ifPresent(allTags::add);
        return allTags;
    }

    public void addUserStoryFeatureTo(Set<TestTag> augmentedTags) {
        if (userStory != null && userStory.getFeature() != null) {
            augmentedTags.add(TestTag.withName(userStory.getFeature().getName()).andType("feature"));
        }
    }

    private Set<TestTag> getTagsUsingTagProviders(List<TagProvider> tagProviders) {
        Set<TestTag> tags = new HashSet<>();
        for (TagProvider tagProvider : tagProviders) {
            try {
                tags.addAll(tagProvider.getTagsFor(this));
            } catch (Throwable theTagProviderFailedButThereIsntMuchWeCanDoAboutIt) {
                LOGGER.error("Tag provider " + tagProvider + " failure",
                        theTagProviderFailedButThereIsntMuchWeCanDoAboutIt);
            }
        }
        tags = removeRedundantTagsFrom(tags);
        return new HashSet<>(tags);
    }

    private Set<TestTag> removeRedundantTagsFrom(Set<TestTag> tags) {
        Set<TestTag> optimizedTags = new HashSet<>();
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
        this.tags = new HashSet<>(tags);
    }


    public void addTags(List<TestTag> tags) {
        Set<TestTag> updatedTags = new HashSet<>(getTags());
        updatedTags.addAll(tags);
        this.tags = updatedTags;
        //this.allTags = addFeatureTagTo(this.tags);
    }

    public void addTag(TestTag tag) {
        Set<TestTag> updatedTags = (tags == null) ? new HashSet<>() : new HashSet<>(tags);
        updatedTags.add(tag);
        this.tags = updatedTags;
        //this.allTags = addFeatureTagTo(this.tags);
    }

    public List<String> getIssueKeys() {
        return getIssues().stream()
                .map(issue -> IssueKeyFormat.forEnvironment(getEnvironmentVariables()).andKey(issue))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
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
        return Joiner.on("_").skipNulls().join(getId(), getQualifierText(), context);
    }

    private String getQualifierText() {
        if ((qualifier != null) && (qualifier.isPresent())) {
            return qualifier.get().replaceAll(" ", "_");
        }
        return null;
    }

    public String getNonNullContext() {
        return (getContext() == null) ? "" : getContext();
    }

    public String getContext() {
        return context;
    }

    /**
     * Setting the context
     *
     * @param context
     */
    public void setContext(String context) {
        if (this.context == null) {
            this.context = context;
            addContextTagsFor(context);
        }
    }

    private void addContextTagsFor(String context) {
        if (context == null) {
            return;
        }
        List<String> contexts = Arrays.stream(context.split(",")).map(String::trim).map(StringUtils::capitalize).collect(Collectors.toList());
        contexts.forEach(
                contextElement -> addTag(TestTag.withName(contextElement).andType("context"))
        );
        if (contexts.size() > 1) {
            String allContexts = contexts.stream().collect(Collectors.joining(", "));
            addTag(TestTag.withName(allContexts).andType("context"));
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
        return isDataDriven() ? getTestStepCount() : 1;
    }

    public int getImplementedTestCount() {
        return (getStepCount() > 0) ? getTestCount() : 0;
    }

    public int countResults(TestResult expectedResult) {
        return countResults(expectedResult, ANY);
    }

    public int countResults(TestResult expectedResult, TestType expectedType) {
        if (isManual() && (expectedType == ANY || expectedType == MANUAL)) {
            return (getResult() == expectedResult) ? testCaseCount() : 0;
        } else if (annotatedResult != null && !annotatedResult.executedResultsCount()) {
            return annotatedResultCount(expectedResult, expectedType);
        }

        if (isDataDriven()) {
            return countDataRowsWithResult(expectedResult, expectedType);
        }

        return (getResult() == expectedResult) && (typeCompatibleWith(expectedType)) ? 1 : 0;
    }

    private int testCaseCount() {
        if (isDataDriven()) {
            return testSteps.size();
        } else {
            return 1;
        }
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
        if (!typeCompatibleWith(expectedType)) {
            return 0;
        }
        if (resultsAreUndefinedIn(getDataTable()) || this.isAJUnit5Test()) {
            for (TestStep step : getTestSteps()) {
                matchingRowCount += (step.getResult() == expectedResult) ? 1 : 0;
            }
        } else {
            for (DataTableRow row : getDataTable().getRows()) {
                matchingRowCount += (row.getResult() == expectedResult) ? 1 : 0;
            }
        }
        return matchingRowCount;
    }

    private boolean resultsAreUndefinedIn(DataTable dataTable) {
        return dataTable.getRows().stream().allMatch(row -> row.getResult() == TestResult.UNDEFINED);
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
        return Optional.empty();
    }

    public boolean hasIssue(String issue) {
        return getIssues().contains(issue);
    }

    public boolean hasTag(TestTag tag) {
        return getAllTags().contains(tag);
    }

    public boolean hasAMoreGeneralFormOfTag(TestTag specificTag) {
        return TestTags.of(getAllTags()).containsTagMatching(specificTag);
    }

    public boolean hasAMoreSpecificFormOfTag(TestTag generalTag) {
        return getAllTags().stream().anyMatch(
                tag -> tag.isAsOrMoreSpecificThan(generalTag)
        );
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public void clearStartTime() {
        this.startTime = null;
    }

    public boolean isManual() {
        return manual;
    }

    public String getLastTested() {
        return lastTested;
    }

    public void setLastTested(String lastTested) {
        this.lastTested = lastTested;
    }

    public boolean isManualTestingUpToDate() {
        return isManualTestingUpToDate;
    }

    public void setManualTestingUpToDate(Boolean upToDate) {
        this.isManualTestingUpToDate = upToDate;
    }

    public Set<? extends Flag> getFlags() {
        if (flags == null) {
            flags = flagProvider.getFlagsFor(this);
            addFlagTagsFor(flags);
        }
        return flags;
    }

    private void addFlagTagsFor(Set<? extends Flag> flags) {
        for (Flag flag : flags) {
            this.addTag(TestTag.withName(flag.getMessage()).andType("flag"));
        }
    }

    public boolean isStartTimeNotDefined() {
        return this.startTime == null;
    }

    private SystemClock getSystemClock() {
        return ModelInfrastructure.getClock();
    }

    private ZonedDateTime now() {
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
        return (testSteps.isEmpty()) ? Optional.empty() : Optional.of(testSteps.get(testSteps.size() - 1));
    }

    public Integer getNestedStepCount() {
        return getFlattenedTestSteps().size();
    }

    public Long getSuccessCount() {
        return count(TestStep.SUCCESSFUL_TESTSTEPS).in(getLeafTestSteps());
    }

    public Long getFailureCount() {
        return count(TestStep.FAILING_TESTSTEPS).in(getLeafTestSteps());
    }

    public Long getErrorCount() {
        return count(TestStep.ERROR_TESTSTEPS).in(getLeafTestSteps());
    }

    public Long getCompromisedCount() {
        return count(TestStep.COMPROMISED_TESTSTEPS).in(getLeafTestSteps());
    }

    public Long getIgnoredCount() {
        return count(TestStep.IGNORED_TESTSTEPS).in(getLeafTestSteps());
    }

    public Long getSkippedOrIgnoredCount() {
        return getIgnoredCount() + getSkippedCount();
    }

    public Long getSkippedCount() {
        return count(TestStep.SKIPPED_TESTSTEPS).in(getLeafTestSteps());
    }

    public Long getPendingCount() {
        return getLeafTestSteps()
                .stream()
                .filter(TestStep::isPending)
                .count();
    }

    public Boolean isSuccess() {
        return (getResult() == TestResult.SUCCESS);
    }

    public Boolean isFailure() {
        return (getResult() == TestResult.FAILURE);
    }

    public Boolean isCompromised() {
        return (getResult() == TestResult.COMPROMISED);
    }

    public Boolean isError() {
        return (getResult() == TestResult.ERROR);
    }

    public Boolean isPending() {
        return (getResult() == TestResult.PENDING); //((getResult() == PENDING) || (getStepCount() == 0));
    }

    public Boolean isSkipped() {
        return (getResult() == TestResult.SKIPPED) || (getResult() == TestResult.IGNORED) || (getResult() == TestResult.ABORTED);
    }

    public Story getUserStory() {
        return userStory;
    }

    public void recordDuration(ZonedDateTime finishTime) {
        this.endTime = finishTime;
        if (duration == 0) {
            long duration = ChronoUnit.MILLIS.between(startTime, finishTime);
            setDuration(duration);
        }
    }

    public void setDuration(final long duration) {
        this.duration = duration;
    }

    public Long getDuration() {
        // For data-driven tests, each top-level step is a separate test
        if (isDataDriven()) {
            return getDataDrivenDuration();
        } else {
            if (startTime != null && endTime != null) {
                return ChronoUnit.MILLIS.between(startTime, endTime);
            } else {
                return duration;
            }
        }
    }

    private long getDataDrivenDuration() {
        // The duration of a data-driven test is the difference between the earliest start time and the latest end time
        Optional<ZonedDateTime> earliestStartTime = getTestSteps().stream().map(TestStep::getStartTime).min(Comparator.naturalOrder());
        Optional<ZonedDateTime> latestEndTime = getDataDrivenEndTime();
        if (earliestStartTime.isPresent() && latestEndTime.isPresent()) {
            return ChronoUnit.MILLIS.between(earliestStartTime.get(), latestEndTime.get());
        }
        return 0;
    }

    private Optional<ZonedDateTime> getDataDrivenEndTime() {
        return getTestSteps().stream().map(TestStep::getEndTime).max(Comparator.naturalOrder());
    }

    public ZonedDateTime getEndTime() {
        if (isDataDriven()) {
            return getDataDrivenEndTime().orElse(null);
        } else {
            return endTime;
        }
    }

    /**
     * @return The total duration of all of the tests in this set in milliseconds.
     */
    public double getDurationInSeconds() {
        if (durationInSeconds == null) {
            durationInSeconds = TestDuration.of(getDuration()).inSeconds();
        }
        return durationInSeconds;
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

    private StepCountBuilder count(Predicate<TestStep> filter) {
        return new StepCountBuilder(filter);
    }

    public static class StepCountBuilder {
        private final Predicate<TestStep> stepFilter;

        private StepCountBuilder(Predicate<TestStep> filter) {
            this.stepFilter = filter;
        }

        long in(List<TestStep> steps) {
            return steps.stream()
                    .filter(stepFilter)
                    .count();
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

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public String getStartedAt() {
        return Optional.ofNullable(startTime).orElse(now())
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getTimestamp() {
        return Optional.ofNullable(startTime).orElse(now())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    public String getTimestamp(DateTimeFormatter formater) {
        return Optional.ofNullable(startTime).orElse(now())
                .format(formater);

    }

    public boolean isDataDriven() {
        return dataTable != null;
    }

    final static private List<String> NO_HEADERS = new ArrayList<>();

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

        for (TestStep step : getTestSteps()) {
            if (!step.isAPrecondition() && step.hasChildren()) {
                return step.getChildren();
            }
        }
        return new ArrayList<>();
    }

    public void useScenarioOutline(String scenarioOutline) {
        this.scenarioOutline = scenarioOutline;
    }

    public String getDataDrivenSampleScenario() {
        if (scenarioOutline == null) {
            scenarioOutline = buildScenarioOutline();
        }
        return scenarioOutline;
    }

    private String buildScenarioOutline() {
        if (!isDataDriven() || getTestSteps().isEmpty() || !atLeastOneStepHasChildren()) {
            return "";
        }

        if (dataTable.scenarioOutline().isPresent()) {
            return dataTable.scenarioOutline().get();
        }

        StringBuilder sampleScenario = new StringBuilder();
        for (TestStep step : getStepChildren()) {
            sampleScenario.append(withPlaceholderSubstitutes(step.getDescription()))
                    .append("\n");
        }
        return sampleScenario.length() > 1 ? sampleScenario.substring(0, sampleScenario.length() - 1) : "";
    }

    private String withPlaceholderSubstitutes(String stepName) {
        if (dataTable == null || dataTable.getRows().isEmpty()) {
            return stepName;
        }

        return dataTable.restoreVariablesIn(stepName);
    }


    private boolean atLeastOneStepHasChildren() {
        return getTestSteps().stream().anyMatch(TestStep::hasChildren);
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
        if (context != null ? !context.equals(that.context) : that.context != null) return false;
        if (testCaseName != null ? !testCaseName.equals(that.testCaseName) : that.testCaseName != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return userStory != null ? userStory.equals(that.userStory) : that.userStory == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        result = 31 * result + (context != null ? context.hashCode() : 0);
        result = 31 * result + (testCaseName != null ? testCaseName.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (userStory != null ? userStory.hashCode() : 0);
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
            if ((step.getException() != null)
                    && (step.getException().getErrorType() != null)
                    && TheErrorType.causedBy(step.getException().getErrorType()).isAKindOf(expected)) {
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
//        List<TestStep> currentTestSteps = new ArrayList<>(testSteps);
//        for (TestStep testStep : currentTestSteps) {
//            if (stepsToReplace.contains(testStep)) {
//                testSteps.remove(testStep);
//            }
//        }
//
        List<TestStep> updatedSteps = new ArrayList<>(testSteps);
        updatedSteps.removeAll(stepsToReplace);
        renumberTestSteps(updatedSteps);
        updateTestSteps(updatedSteps);

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

    public List<CastMember> getActors() {
        return actors;
    }

    public boolean hasEvidence() {
        return testSteps.stream().anyMatch(
                step -> step.getReportData().stream().anyMatch(ReportData::isEvidence)
        );
    }

    public List<ReportData> getEvidence() {
        return getFlattenedTestSteps().stream()
                .filter(step -> !step.getReportEvidence().isEmpty())
                .map(TestStep::getReportEvidence)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public TestOutcome withDataRowsfilteredbyTag(TestTag tag) {
        return withDataRowsfilteredbyTagsFrom(Collections.singleton(tag));
    }

    public TestOutcome withDataRowsfilteredbyTagsFrom(Collection<TestTag> filterTags) {
        if (!isDataDriven()) {
            return this;
        }

        if (!TestTags.of(dataTable.getTags()).containsTagMatchingOneOf(filterTags)) {
            return this;
        }
        if (testSteps.size() != dataTable.getSize()) {
            return this;
        }

        DataTable filteredDataTable = dataTable.containingOnlyRowsWithTagsFrom(filterTags);
        List<TestStep> filteredSteps = testSteps;// dataTable.filterStepsWithTagsFrom(testSteps, tags);

        Collection<TestTag> originalDataTableTags = dataTable.getTags();
        Collection<TestTag> filteredDataTableTags = filteredDataTable.getTags();

        Collection<TestTag> redundantTags = new HashSet<>(originalDataTableTags);
        redundantTags.removeAll(filteredDataTableTags);

        Set<TestTag> outcomeTagsWithoutRedundentTags = new HashSet<>(tags);
        outcomeTagsWithoutRedundentTags.removeAll(redundantTags);

        return new TestOutcome(startTime,
                duration,
                title,
                description,
                name,
                id,
                scenarioId,
                this.methodName,
                testCase,
                filteredSteps,
                issues,
                additionalIssues,
                actors,
                outcomeTagsWithoutRedundentTags,
                featureTag.orElse(null),
                userStory,
                testFailureCause,
                testFailureClassname,
                testFailureMessage,
                testFailureSummary,
                annotatedResult,
                filteredDataTable,
                qualifier,
                driver,
                manual,
                isManualTestingUpToDate,
                lastTested,
                manualTestEvidence,
                projectKey,
                environmentVariables,
                externalLink,
                context,
                testSource,
                this.testOutlineName);
    }

    public ExternalLink getExternalLink() {
        return externalLink;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Integer getOrder() {
        if (order == null) {
            return 0;
        }
        return order;
    }

    public TestOutcome fromStep(int index) {
        return fromStep(testSteps.get(index));
    }

    public TestOutcome fromStep(TestStep testStep) {
        List<TestStep> specifiedSteps = asList(testStep);

        TestOutcome specifiedOutcome = this.copy().withSteps(specifiedSteps);
        Optional<TestFailureCause> failureCause = TestFailureCause.from(specifiedOutcome.getFlattenedTestSteps());
        if (failureCause.isPresent()) {
            specifiedOutcome.appendTestFailure(failureCause.get());
        } else {
            specifiedOutcome.noTestFailureIsDefined();
        }
        return specifiedOutcome;
    }

    public TestOutcome withExamplesMatching(Predicate<TestStep> condition) {
        if (isDataDriven() && someStepsDoNotMatch(condition)) {
            return copy().removeTopLevelStepsNotMatching(condition);
        } else {
            return this;
        }
    }

    public TestOutcome withExamplesHavingResult(TestResult result) {
        if (!isDataDriven() || containsOnlyExamplesWithResult(result) || containsNoExamplesWithResult(result)) {
            return this;
        }
        return copy().removeTopLevelStepsNotHavingResult(result);
    }

    private boolean containsNoExamplesWithResult(TestResult result) {
        Set<TestResult> distinctResults = getDistinctResults();
        for (TestResult actualResult : result.expanded()) {
            if (distinctResults.contains(actualResult)) {
                return false;
            }
        }
        return true;
    }

    private boolean containsOnlyExamplesWithResult(TestResult result) {
        Set<TestResult> distinctResults = getDistinctResults();
        return distinctResults.size() == 1 && result.expanded().contains(distinctResults.iterator().next());
    }

    public List<TestOutcome> asTestCases() {
        if (isDataDriven()) {
            List<TestOutcome> testCases = new ArrayList<>();
            int rowNumber = 1;
            for (TestStep step : testSteps) {
                testCases.add(testCaseInStep(step, rowNumber++));
            }
            return testCases;
        } else {
            return asList(this);
        }
    }

    private TestOutcome testCaseInStep(TestStep childStep, int rowNumber) {
        TestOutcome testCaseOutcome = copy().withSteps(asList(childStep));
        testCaseOutcome.setTitle(stepTitleIn(childStep.getDescription()) + " [" + rowNumber + "]");
        testCaseOutcome.setTestData(testDataIn(childStep.getDescription()));
        return testCaseOutcome;
    }

    private void setTestData(String testData) {
        this.testData = testData;
    }

    public String getTestData() {
        return this.testData;
    }

    private static String stepTitleIn(String description) {
        return (description.contains("{")) ? (description.substring(0, description.indexOf("{"))) : description;
    }

    private static String testDataIn(String description) {
        return (description.contains("{")) ? trimmedDescription(description.substring(description.indexOf("{"))) : "";
    }

    private static String trimmedDescription(String description) {
        int parametersStart = description.indexOf("{") + 1;
        int parametersEnd = description.lastIndexOf("}");
        return description.substring(parametersStart, parametersEnd);
    }


    public TestOutcome removeTopLevelStepsNotMatching(Predicate<TestStep> condition) {
        List<TestStep> updatedSteps = new ArrayList<>();
        for (TestStep step : testSteps) {
            if (condition.test(step)) {
                updatedSteps.add(step);
            }
        }
        updateTestSteps(updatedSteps);
        return this;
    }

    public TestOutcome removeTopLevelStepsNotHavingResult(TestResult result) {
        List<TestStep> updatedSteps = new ArrayList<>();
        for (TestStep step : testSteps) {
            if (result.expanded().contains(step.getResult())) {
                updatedSteps.add(step);
            }
        }
        updateTestSteps(updatedSteps);
        return this;
    }

    private void updateTestSteps(List<TestStep> updatedSteps) {
        this.testSteps = updatedSteps;
        this.flattenedSteps = null;
        this.leafSteps = null;
    }

    private boolean someStepsDoNotMatch(Predicate<TestStep> condition) {
        return testSteps.stream().anyMatch(condition);
    }

    public boolean containsAtLeastOneOutcomeWithResult(TestResult expectedResult) {
        if (isDataDriven()) {
            for (TestStep step : testSteps) {
                if (expectedResult.expanded().contains(step.getResult())) {
                    return true;
                }
            }
            return false;
        } else {
            return expectedResult.expanded().contains(getResult());
        }
    }

    public Stream<TestResult> getAllResultsStream() {
        if (isDataDriven()) {
            return getTestSteps().stream().map(TestStep::getResult);
        } else {
            return Stream.of(getResult());
        }
    }

    public List<TestResult> getAllResults() {
        if (isManual() && isDataDriven()) {
            return testSteps.stream().map(step -> getResult()).collect(Collectors.toList());
        } else if (!isManual() && isDataDriven()) {
            return testSteps.stream().map(TestStep::getOverallResult).collect(Collectors.toList());
        }
        return Collections.singletonList(getResult());
    }

    public long getResultCount() {
        long count = 0;
        if (isDataDriven()) {
            count += testSteps.size();
        } else {
            count++;
        }
        return count;
    }

    public Set<TestResult> getDistinctResults() {
        Set<TestResult> results = new HashSet<>();
        if (isDataDriven()) {
            for (TestStep step : getTestSteps()) {
                results.add(step.getResult());
            }
        } else {
            results.add(getResult());
        }
        return results;
    }

    public boolean hasResult(TestResult result) {
        return getAllResults().contains(result);
    }
}
