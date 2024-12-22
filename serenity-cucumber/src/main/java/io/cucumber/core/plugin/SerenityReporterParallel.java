package io.cucumber.core.plugin;

import com.google.common.collect.Lists;
import io.cucumber.messages.types.*;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.Plugin;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;
import io.cucumber.plugin.event.TestStep;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.TestStepStarted;
import io.cucumber.plugin.event.*;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.tagexpressions.Expression;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.SerenityListeners;
import net.serenitybdd.core.SerenityReports;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import net.serenitybdd.cucumber.events.SetTestManualEvent;
import net.serenitybdd.cucumber.events.StepFinishedWithResultEvent;
import net.serenitybdd.cucumber.formatting.ScenarioOutlineDescription;
import net.serenitybdd.cucumber.util.StepDefinitionAnnotationReader;
import net.thucydides.core.model.screenshots.StepDefinitionAnnotations;
import net.thucydides.model.domain.DataTable;
import net.thucydides.model.domain.Rule;
import net.thucydides.model.domain.*;
import net.thucydides.model.reports.ReportService;
import net.thucydides.model.requirements.FeatureFilePath;
import net.thucydides.model.requirements.model.cucumber.InvalidFeatureFileException;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.steps.*;
import net.thucydides.core.steps.events.*;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.TestSourceType;
import net.thucydides.model.webdriver.Configuration;
import net.thucydides.core.webdriver.SerenityWebdriverManager;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import static io.cucumber.core.plugin.TaggedScenario.*;
import static java.util.stream.Collectors.toList;
import static net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach.FEATURE;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Cucumber parallel reporter for Serenity.
 *
 * @author L.Carausu (liviu.carausu@gmail.com)
 */
public class SerenityReporterParallel implements Plugin, ConcurrentEventListener {

    private static final String OPEN_PARAM_CHAR = "\uff5f";
    private static final String CLOSE_PARAM_CHAR = "\uff60";

    private static final String SCENARIO_OUTLINE_NOT_KNOWN_YET = "";

    private final Configuration systemConfiguration;

    private final static String FEATURES_ROOT_PATH = "/features/";
    private final static String FEATURES_CLASSPATH_ROOT_PATH = ":features/";

    private final FeatureFileLoader featureLoader = new FeatureFileLoader();

    private LineFilters lineFilters;


    private static final Logger LOGGER = LoggerFactory.getLogger(SerenityReporter.class);

    private final Set<URI> contextURISet = new CopyOnWriteArraySet<>();

    /**
     * key = feature URI; value = ScenarioContextParallel
     */
    private final Map<URI, ScenarioContextParallel> localContexts = Collections.synchronizedMap(new HashMap<>());

    protected ScenarioContextParallel getContext(URI featureURI) {
        synchronized (localContexts) {
            return localContexts.computeIfAbsent(featureURI, uri -> new ScenarioContextParallel(featureURI));
        }
    }

    /**
     * Constructor automatically called by cucumber when class is specified as plugin
     * in @CucumberOptions.
     */
    public SerenityReporterParallel() {
        this.systemConfiguration = SerenityInfrastructure.getConfiguration();
    }

    public SerenityReporterParallel(Configuration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    private final FeaturePathFormatter featurePathFormatter = new FeaturePathFormatter();

    private StepEventBus getStepEventBus(URI featurePath) {
        URI prefixedPath = featurePathFormatter.featurePathWithPrefixIfNecessary(featurePath);
        return StepEventBus.eventBusFor(prefixedPath);
    }

    private void setStepEventBus(URI featurePath) {
        URI prefixedPath = featurePathFormatter.featurePathWithPrefixIfNecessary(featurePath);
        StepEventBus.setCurrentBusToEventBusFor(prefixedPath);
    }

    private void initialiseListenersFor(URI featurePath) {
        StepEventBus stepEventBus = getStepEventBus(featurePath);
        if (stepEventBus.isBaseStepListenerRegistered()) {
            return;
        }
        SerenityListeners listeners = new SerenityListeners(stepEventBus, systemConfiguration);
        getContext(featurePath).setStepEventBus(stepEventBus);
        getContext(featurePath).addBaseStepListener(listeners.getBaseStepListener());
    }

    private final EventHandler<TestSourceRead> testSourceReadHandler = this::handleTestSourceRead;
    private final EventHandler<TestCaseStarted> caseStartedHandler = this::handleTestCaseStarted;
    private final EventHandler<TestCaseFinished> caseFinishedHandler = this::handleTestCaseFinished;
    private final EventHandler<TestStepStarted> stepStartedHandler = this::handleTestStepStarted;
    private final EventHandler<TestStepFinished> stepFinishedHandler = this::handleTestStepFinished;
    private final EventHandler<TestRunStarted> runStartedHandler = this::handleTestRunStarted;
    private final EventHandler<TestRunFinished> runFinishedHandler = this::handleTestRunFinished;
    private final EventHandler<WriteEvent> writeEventHandler = this::handleWrite;

    protected void handleTestRunStarted(TestRunStarted event) {
        LOGGER.debug("SRP:handleTestRunStarted {} ", Thread.currentThread());
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestSourceRead.class, testSourceReadHandler);
        publisher.registerHandlerFor(TestRunStarted.class, runStartedHandler);
        publisher.registerHandlerFor(TestRunFinished.class, runFinishedHandler);
        publisher.registerHandlerFor(TestCaseStarted.class, caseStartedHandler);
        publisher.registerHandlerFor(TestCaseFinished.class, caseFinishedHandler);
        publisher.registerHandlerFor(TestStepStarted.class, stepStartedHandler);
        publisher.registerHandlerFor(TestStepFinished.class, stepFinishedHandler);
        publisher.registerHandlerFor(WriteEvent.class, writeEventHandler);
    }

    protected void handleTestSourceRead(TestSourceRead event) {
        LOGGER.debug("SRP:handleTestSourceRead {}", Thread.currentThread());
        featureLoader.addTestSourceReadEvent(event);
        URI featurePath = event.getUri();
        featureFrom(featurePath).ifPresent(
            feature -> {
                getContext(featurePath).setFeatureTags(feature.getTags());
                resetEventBusFor(featurePath);
                initialiseListenersFor(featurePath);
                configureDriver(feature, featurePath);
                Story userStory = userStoryFrom(feature, relativeUriFrom(event.getUri()));
                getContext(featurePath).stepEventBus().testSuiteStarted(userStory);
            }
        );
    }

    private void resetEventBusFor(URI featurePath) {
        StepEventBus.clearEventBusFor(featurePath);
    }

    private String relativeUriFrom(URI fullPathUri) {
        boolean useDecodedURI = systemConfiguration.getEnvironmentVariables().getPropertyAsBoolean("use.decoded.url", false);
        String pathURIAsString;
        if (useDecodedURI) {
            pathURIAsString = URLDecoder.decode(fullPathUri.toString(), StandardCharsets.UTF_8);
        } else {
            pathURIAsString = fullPathUri.toString();
        }

        if (pathURIAsString.contains(FEATURES_ROOT_PATH)) {
            return StringUtils.substringAfterLast(pathURIAsString, FEATURES_ROOT_PATH);
        } else {
            return pathURIAsString;
        }
    }

    protected Optional<Feature> featureFrom(URI featureFileUri) {

        LOGGER.debug("Running feature from " + featureFileUri.toString());
        if (!featureFileUri.toString().contains(FEATURES_ROOT_PATH) && !featureFileUri.toString().contains(FEATURES_CLASSPATH_ROOT_PATH)) {
            LOGGER.warn("Feature from " + featureFileUri + " is not under the 'features' directory. Requirements report will not be correctly generated!");
        }

        return featureLoader.featureFrom(featureFileUri);
    }

    private Story userStoryFrom(Feature feature, String featureFileUri) {
        String relativePath = new FeatureFilePath(systemConfiguration.getEnvironmentVariables()).relativePathFor(featureFileUri);
        String id = relativePath.replace(".feature", "");
        Story userStory = Story.withIdAndPath(id, feature.getName(), featureFileUri).asFeature();
        if (!isEmpty(feature.getDescription())) {
            userStory = userStory.withNarrative(feature.getDescription());
        }
        return userStory;
    }

    protected void handleTestCaseStarted(TestCaseStarted event) {
        try {
            TestCase testCase = event.getTestCase();
            LOGGER.debug("SRP:handleTestCaseStarted {} {} {} at line {}", testCase.getUri(), Thread.currentThread(),
                testCase.getId(), testCase.getLocation().getLine());
            TestSession.startSession(testCase.getId().toString(), getStepEventBus(event.getTestCase().getUri()));
            URI featurePath = testCase.getUri();
            contextURISet.add(featurePath);

            if (FeatureTracker.isNewFeature(event)) {
                // Shut down any drivers remaining open from a previous feature, if @singlebrowser is used.
                // Cucumber has no event to mark the start and end of a feature, so we need to do this here.
                if (RestartBrowserForEach.configuredIn(systemConfiguration.getEnvironmentVariables()).restartBrowserForANew(FEATURE)) {
                    ThucydidesWebDriverSupport.closeCurrentDrivers();
                }
                FeatureTracker.startNewFeature(event);
            }
            ConfigureDriverFromTags.forTags(event.getTestCase().getTags());

            String scenarioName = event.getTestCase().getName();
            TestSourcesModel.AstNode astNode = featureLoader.getAstNode(featurePath, event.getTestCase().getLocation().getLine());

            Optional<Feature> currentFeature = featureFrom(featurePath);

            String scenarioId = "";
            ScenarioContextParallel context = getContext(featurePath);
            if ((astNode != null) && currentFeature.isPresent()) {
                Scenario currentScenarioDefinition = TestSourcesModel.getScenarioDefinition(astNode);
                //initialiseListenersFor(featurePath,event.getTestCase(),getContext(featurePath).isAScenarioOutline());

                //the sources are read in parallel, global current feature cannot be used
                scenarioId = scenarioIdFrom(currentFeature.get().getName(), TestSourcesModel.convertToId(currentScenarioDefinition.getName()));
                context.setCurrentScenarioDefinitionFrom(scenarioId, astNode);
                boolean newScenario = !scenarioId.equals(context.getCurrentScenario(scenarioId));
                if (newScenario) {
                    configureDriver(currentFeature.get(), event.getTestCase().getUri());
                    if (!currentScenarioDefinition.getExamples().isEmpty()) {
                        TestSession.getTestSessionContext().setInDataDrivenTest(true);
                        context.startNewExample(scenarioId);
                        LOGGER.debug("SRP:startNewExample {} {} {} at line {} ", event.getTestCase().getUri(), Thread.currentThread(),
                            event.getTestCase().getId(), event.getTestCase().getLocation().getLine());
                        handleExamples(scenarioId, featurePath, currentFeature.get(),
                            context.currentScenarioOutline(scenarioId).getTags(),
                            context.currentScenarioOutline(scenarioId).getName(),
                            context.currentScenarioOutline(scenarioId).getExamples());
                    }
                    startOfScenarioLifeCycle(scenarioId, featurePath, event.getTestCase(), currentFeature.get(), scenarioName, context.getCurrentScenarioDefinition(scenarioId), event.getTestCase().getLocation().getLine());
                    context.setCurrentScenario(scenarioId, scenarioId);
                } else {
                    if (context.isAScenarioOutline(scenarioId)) {
                        startProcessingExampleLine(scenarioId, featurePath, event.getTestCase(), Long.valueOf(event.getTestCase().getLocation().getLine()), scenarioName);
                    }
                }
                final String scenarioIdForBackground = scenarioId;
                TestSourcesModel.getBackgroundForTestCase(astNode).ifPresent(background -> handleBackground(featurePath, scenarioIdForBackground, background));
                //
                // Check for tags
                //
                if (astNode.node instanceof Scenario) {
                    List<Tag> tags = ((Scenario) astNode.node).getTags();
                    TestResult annotatedTestResult = ScenarioTagProcessor.processScenarioTags(tags, event.getTestCase().getUri());
                    if (TaggedScenario.isManual(tags)) {
                        MANUAL_TEST_RESULTS_CACHE.put(testCase.getId(), annotatedTestResult);
                    }
                }

            }
            io.cucumber.messages.types.Rule rule = getRuleForTestCase(astNode);
            if (rule != null) {
                context.addStepEventBusEvent(new SetRuleEvent(Rule.from(rule)));
            }

        } catch (Throwable t) {
            LOGGER.error("Test case started failed with error ", t);
            throw t;
        }
    }

    private final static Map<UUID, TestResult> MANUAL_TEST_RESULTS_CACHE = new HashMap<>();

    private io.cucumber.messages.types.Rule getRuleForTestCase(TestSourcesModel.AstNode astNode) {
        Feature feature = getFeatureForTestCase(astNode);
        Scenario existingScenario = TestSourcesModel.getScenarioDefinition(astNode);
        List<FeatureChild> childrenList = feature.getChildren();
        for (FeatureChild featureChild : childrenList) {
            if (scenarioIsIncludedInARule(existingScenario, featureChild)) {
                return featureChild.getRule().get();
            }
        }
        return null;
    }

    private boolean scenarioIsIncludedInARule(Scenario existingScenario, FeatureChild featureChild) {
        return featureChild.getRule() != null && featureChild.getRule().isPresent()
            && featureChild.getRule().get().getChildren().stream().
            filter(rc -> rc.getScenario().isPresent()).
            map(rc -> rc.getScenario().get()).collect(Collectors.toList()).contains(existingScenario);
    }

    private Feature getFeatureForTestCase(TestSourcesModel.AstNode astNode) {
        while (astNode.parent != null) {
            astNode = astNode.parent;
        }
        return (Feature) astNode.node;
    }

    protected void handleTestCaseFinished(TestCaseFinished event) {
        TestCase testCase = event.getTestCase();
        LOGGER.debug("SRP:handleTestCaseFinished " + " " + testCase.getUri()
            + " " + Thread.currentThread() + " " + testCase.getId() + " at line " + testCase.getLocation().getLine());
        URI featurePath = testCase.getUri();

        scenarioIdFrom(testCase).ifPresent(scenarioId -> {
            if (getContext(featurePath).examplesAreRunning(scenarioId)) {
                handleResult(scenarioId, featurePath, testCase, event.getResult(), true);
                finishProcessingExampleLine(scenarioId, featurePath, testCase);
            }

            Status eventStatus = eventStatusFor(event);

            if (Status.FAILED.equals(eventStatus) && noAnnotatedResultIdDefinedFor(event)) {
                getContext(featurePath).addStepEventBusEvent(
                    new TestFailedEvent(scenarioId, event.getResult().getError())
                );
            } else {
                getContext(featurePath).addStepEventBusEvent(
                    new TestFinishedEvent(scenarioId, getContext(featurePath).examplesAreRunning(scenarioId))
                );
            }

            getContext(featurePath).storeAllStepEventBusEventsForLine(testCase.getLocation().getLine(), testCase);
            getContext(featurePath).clearStepQueue(testCase);
            getContext(featurePath).stepEventBus().clear();

            // We don't have the TestOutcome object ready yet, so we need to create a temporary one based on the event
            // The feature name is the first part of getContext(featurePath).getCurrentScenario(scenarioId) up to the first semicolon
            // The scenario name is the second part of getContext(featurePath).getCurrentScenario(scenarioId) after the first semicolon
            String featureName = getContext(featurePath).getCurrentScenario(scenarioId).split(";")[0];
            TestOutcome testOutcome = TestOutcome.forTestInStory(testCase.getName(),
                Story.called(featureName));
            testOutcome.setResult(serenityTestResultFrom(event.getResult().getStatus()));
            if (event.getResult().getError() != null) {
                testOutcome.testFailedWith(event.getResult().getError());
            }
            // We need to close the driver here to avoid wasting resources and causing timeouts with Selenium Grid services
            getContext(featurePath)
                .stepEventBus()
                .getBaseStepListener()
                .cleanupWebdriverInstance(getContext(featurePath).stepEventBus().isCurrentTestDataDriven(), testOutcome);

            // Update external links cache
            String key = featureName + "/" + testOutcome.getName();
            if (testOutcome.getExternalLink() != null) {
                EXTERNAL_LINK_CACHE.put(key, testOutcome.getExternalLink());
            }
            if (testOutcome.getSessionId() != null) {
                SESSION_ID_CACHE.put(key, testOutcome.getSessionId());
            }
        });

    }

    private static final Map<Status, TestResult> TEST_RESULT_MAP = Map.of(
        Status.PASSED, TestResult.SUCCESS,
        Status.FAILED, TestResult.FAILURE, Status.SKIPPED, TestResult.SKIPPED,
        Status.PENDING, TestResult.PENDING,
        Status.UNDEFINED, TestResult.UNDEFINED,
        Status.AMBIGUOUS, TestResult.UNDEFINED);

    private TestResult serenityTestResultFrom(Status status) {
        // Use a map to convert the Status enum to a Serenity TestResult value
        return TEST_RESULT_MAP.get(status);
    }

    private Status eventStatusFor(TestCaseFinished event) {
        if (MANUAL_TEST_RESULTS_CACHE.containsKey(event.getTestCase().getId())) {
            switch (MANUAL_TEST_RESULTS_CACHE.get(event.getTestCase().getId())) {
                case SUCCESS:
                    return Status.PASSED;
                case ABORTED:
                case FAILURE:
                case COMPROMISED:
                case ERROR:
                    return Status.FAILED;
                case PENDING:
                    return Status.PENDING;
                case SKIPPED:
                case IGNORED:
                    return Status.SKIPPED;
                default:
                    return Status.UNDEFINED;
            }
        } else {
            return event.getResult().getStatus();
        }
    }

    private boolean noAnnotatedResultIdDefinedFor(TestCaseFinished event) {
        BaseStepListener baseStepListener = getStepEventBus(event.getTestCase().getUri()).getBaseStepListener();
        return (baseStepListener.getTestOutcomes().isEmpty() || (latestOf(baseStepListener.getTestOutcomes()).getAnnotatedResult() == null));
    }

    private TestOutcome latestOf(List<TestOutcome> testOutcomes) {
        return testOutcomes.get(testOutcomes.size() - 1);
    }

    protected void handleTestStepStarted(TestStepStarted event) {
        ZonedDateTime startTime = ZonedDateTime.now();
        TestCase testCase = event.getTestCase();
        URI featurePath = testCase.getUri();
        Optional<String> scenarioId = scenarioIdFrom(testCase);
        if (scenarioId.isEmpty()) {
            throw new InvalidFeatureFileException("Unable to run scenario '" + testCase.getName() + "': No scenario ID found in " + featurePath);
        }
        LOGGER.debug("SRP:handleTestStepStarted " + " " + testCase.getUri() + " " + Thread.currentThread()
            + " " + testCase.getId() + " at line " + testCase.getLocation().getLine());

        StepDefinitionAnnotations.setScreenshotPreferencesTo(
            StepDefinitionAnnotationReader
                .withScreenshotLevel((TakeScreenshots) systemConfiguration.getScreenshotLevel()
                    .orElse(TakeScreenshots.UNDEFINED))
                .forStepDefinition(event.getTestStep().getCodeLocation())
                .getScreenshotPreferences());

        if (!(event.getTestStep() instanceof HookTestStep)) {
            if (event.getTestStep() instanceof PickleStepTestStep) {
                PickleStepTestStep pickleTestStep = (PickleStepTestStep) event.getTestStep();
                TestSourcesModel.AstNode astNode = featureLoader.getAstNode(testCase.getUri(), pickleTestStep.getStepLine());
                if (astNode != null) {
                    //io.cucumber.core.internal.gherkin.ast.Step step = (io.cucumber.core.internal.gherkin.ast.Step) astNode.node;
                    io.cucumber.messages.types.Step step = (io.cucumber.messages.types.Step) astNode.node;
                    if (!getContext(featurePath).isAddingScenarioOutlineSteps(scenarioId.get())) {
                        getContext(featurePath).queueStep(testCase, step);
                        getContext(featurePath).queueTestStep(testCase, event.getTestStep());
                    }
                    if (getContext(featurePath).isAScenarioOutline(scenarioId.get())) {
                        int lineNumber = testCase.getLocation().getLine();
                        getContext(featurePath).addStepEventBusEvent(new UpdateExampleLineNumberEvent(lineNumber));
                    }
                    io.cucumber.messages.types.Step currentStep = getContext(featurePath).getCurrentStep(testCase);
                    String stepTitle = stepTitleFrom(currentStep, pickleTestStep);
                    getContext(featurePath).addStepEventBusEvent(
                        new StepStartedEvent(ExecutedStepDescription.withTitle(stepTitle), startTime));
                    getContext(featurePath).addStepEventBusEvent(
                        new UpdateCurrentStepTitleEvent(normalized(stepTitle)));
                }
            }
        }
    }

    public void handleWrite(WriteEvent event) {
        LOGGER.debug("SRP:handleWrite " + " " + event.getTestCase().getUri());
        URI featurePath = event.getTestCase().getUri();
        getContext(featurePath).stepEventBus().stepStarted(ExecutedStepDescription.withTitle(event.getText()));
        getContext(featurePath).stepEventBus().stepFinished();
    }

    protected void handleTestStepFinished(TestStepFinished event) {
        TestCase testCase = event.getTestCase();
        LOGGER.debug("SRP:handleTestStepFinished " + " " + testCase.getUri() +
            " " + Thread.currentThread() + " " + testCase.getId() +
            " at line " + testCase.getLocation().getLine());
        if (!(event.getTestStep() instanceof HookTestStep)) {
            scenarioIdFrom(testCase)
                .ifPresent(scenarioId -> handleResult(scenarioId, testCase.getUri(), testCase, event.getResult(), false));
            StepDefinitionAnnotations.clear();
        }

    }

    protected void handleTestRunFinished(TestRunFinished event) {
        LOGGER.debug("SRP:handleTestRunFinished " + Thread.currentThread() + " " + contextURISet);

        for (URI featurePath : contextURISet) {
            getContext(featurePath).playAllTestEvents();
        }
        enrichOutcomes();
        generateReports();
        assureTestSuiteFinished();
    }

    private ReportService getReportService() {
        return SerenityReports.getReportService(systemConfiguration);
    }

    private void configureDriver(Feature feature, URI featurePath) {
        getStepEventBus(featurePath).setUniqueSession(systemConfiguration.shouldUseAUniqueBrowser());
        List<String> tags = getTagNamesFrom(feature.getTags());
        String requestedDriver = getDriverFrom(tags);
        String requestedDriverOptions = getDriverOptionsFrom(tags);
        if (isNotEmpty(requestedDriver)) {
            ThucydidesWebDriverSupport.useDefaultDriver(requestedDriver);
            ThucydidesWebDriverSupport.useDriverOptions(requestedDriverOptions);
        }
    }

    private List<String> getTagNamesFrom(List<Tag> tags) {
        List<String> tagNames = new ArrayList<>();
        for (Tag tag : tags) {
            tagNames.add(tag.getName());
        }
        return tagNames;
    }

    private String getDriverFrom(List<String> tags) {
        String requestedDriver = null;
        for (String tag : tags) {
            if (tag.startsWith("@driver:")) {
                requestedDriver = tag.substring(8);
            }
        }
        return requestedDriver;
    }

    private String getDriverOptionsFrom(List<String> tags) {
        String requestedDriver = null;
        for (String tag : tags) {
            if (tag.startsWith("@driver-options:")) {
                requestedDriver = tag.substring(16);
            }
        }
        return requestedDriver;
    }

    private void handleExamples(String mainScenarioId, URI featurePath, Feature currentFeature, List<Tag> scenarioOutlineTags, String id, List<Examples> examplesList) {
        lineFilters = LineFilters.forCurrentContext();
        String featureName = currentFeature.getName();
        List<Tag> currentFeatureTags = currentFeature.getTags();
        getContext(featurePath).doneAddingScenarioOutlineSteps(mainScenarioId);
        initializeExamples(mainScenarioId, featurePath);
        for (Examples examples : examplesList) {
            if (examplesAreNotExcludedByTags(examples, scenarioOutlineTags, currentFeatureTags)
                && lineFilters.examplesAreNotExcluded(examples, featurePath)) {
                List<TableRow> examplesTableRows = examples
                    .getTableBody()
                    .stream()
                    .filter(tableRow -> lineFilters.tableRowIsNotExcludedBy(tableRow, featurePath))
                    .collect(Collectors.toList());
                List<String> headers = getHeadersFrom(examples.getTableHeader().get());
                List<Map<String, String>> rows = getValuesFrom(examplesTableRows, headers);

                Map<Integer, Long> lineNumbersOfEachRow = new HashMap<>();

                for (int i = 0; i < examplesTableRows.size(); i++) {
                    TableRow tableRow = examplesTableRows.get(i);
                    lineNumbersOfEachRow.put(i, tableRow.getLocation().getLine());
                    addRow(mainScenarioId, featurePath, headers, tableRow);
                    if (examples.getTags() != null) {
                        exampleTags(featurePath).put(examplesTableRows.get(i).getLocation().getLine(), examples.getTags());
                    }
                }

                String scenarioId = scenarioIdFrom(featureName, id);
                LOGGER.debug("SRP:handleExamples " + Thread.currentThread() + " scenarioId " + scenarioId + " mainscenarioId " + mainScenarioId);
                boolean newScenario = !getContext(featurePath).hasScenarioId(scenarioId);
                LOGGER.debug("SRP:newScenario " + newScenario);

                String exampleTableName = trim(examples.getName());
                String exampleTableDescription = trim(examples.getDescription());
                if (newScenario) {
                    getContext(featurePath).setTable(mainScenarioId,
                        dataTableFrom(SCENARIO_OUTLINE_NOT_KNOWN_YET,
                            headers,
                            rows,
                            exampleTableName,
                            exampleTableDescription,
                            lineNumbersOfEachRow));
                } else {
                    getContext(featurePath).addTableRows(mainScenarioId, headers,
                        rows,
                        exampleTableName,
                        exampleTableDescription,
                        lineNumbersOfEachRow);
                }
                getContext(featurePath).addTableTags(mainScenarioId, tagsIn(examples));

                getContext(featurePath).addCurrentScenarioId(scenarioId);
            }
        }
    }

    private List<TestTag> tagsIn(Examples examples) {
        return examples.getTags().stream().map(tag -> TestTag.withValue(tag.getName().substring(1))).collect(Collectors.toList());
    }

    private boolean examplesAreNotExcludedByTags(Examples examples, List<Tag> scenarioOutlineTags, List<Tag> currentFeatureTags) {
        if (testRunHasFilterTags()) {
            return examplesMatchFilter(examples, scenarioOutlineTags, currentFeatureTags);
        }
        return true;
    }

    private boolean examplesMatchFilter(Examples examples, List<Tag> scenarioOutlineTags, List<Tag> currentFeatureTags) {
        List<Tag> allExampleTags = getExampleAllTags(examples, scenarioOutlineTags, currentFeatureTags);
        List<String> allTagsForAnExampleScenario = allExampleTags.stream().map(Tag::getName).collect(Collectors.toList());
        Expression tagValuesFromCucumberOptions = getCucumberRuntimeTags().get(0);
        return tagValuesFromCucumberOptions.evaluate(allTagsForAnExampleScenario);
    }

    private boolean testRunHasFilterTags() {
        List<Expression> tagFilters = getCucumberRuntimeTags();
        return (tagFilters != null) && tagFilters.size() > 0;
    }

    private List<Expression> getCucumberRuntimeTags() {
        if (CucumberWithSerenity.currentRuntimeOptions() == null) {
            return new ArrayList<>();
        } else {
            return CucumberWithSerenity.currentRuntimeOptions().getTagExpressions();
        }
    }

    private List<Tag> getExampleAllTags(Examples examples, List<Tag> scenarioOutlineTags, List<Tag> currentFeatureTags) {
        List<Tag> exampleTags = examples.getTags();
        List<Tag> allTags = new ArrayList<>();
        if (exampleTags != null)
            allTags.addAll(exampleTags);
        if (scenarioOutlineTags != null)
            allTags.addAll(scenarioOutlineTags);
        if (currentFeatureTags != null)
            allTags.addAll(currentFeatureTags);
        return allTags;
    }

    private List<String> getHeadersFrom(TableRow headerRow) {
        return headerRow.getCells().stream().map(TableCell::getValue).collect(Collectors.toList());
    }

    private List<Map<String, String>> getValuesFrom(List<TableRow> examplesTableRows, List<String> headers) {

        List<Map<String, String>> rows = new ArrayList<>();
        for (int row = 0; row < examplesTableRows.size(); row++) {
            Map<String, String> rowValues = new HashMap<>();
            int column = 0;
            List<String> cells = examplesTableRows.get(row).getCells().stream().map(TableCell::getValue).collect(Collectors.toList());
            for (String cellValue : cells) {
                String columnName = headers.get(column++);
                rowValues.put(columnName, cellValue);
            }
            rows.add(rowValues);
        }
        return rows;
    }

    private void addRow(String scenarioId, URI featurePath,
                        List<String> headers,
                        TableRow currentTableRow) {
        Map<String, String> row = new LinkedHashMap<>();
        for (int j = 0; j < headers.size(); j++) {
            List<String> cells = currentTableRow.getCells().stream().map(TableCell::getValue).collect(Collectors.toList());
            row.put(headers.get(j), cells.get(j));
        }
        exampleRows(scenarioId, featurePath).put(currentTableRow.getLocation().getLine(), row);
    }

    public static Optional<String> scenarioIdFrom(FeatureFileLoader featureLoader, TestCase testCase) {
        var featureUri = testCase.getUri();
        var node = featureLoader.getAstNode(featureUri, testCase.getLocation().getLine());
        var scenario = TestSourcesModel.getScenarioDefinition(node);
        return featureLoader.featureFrom(featureUri)
            .map(feature -> scenarioIdFrom(feature.getName(), TestSourcesModel.convertToId(scenario.getName())));
    }

    private Optional<String> scenarioIdFrom(TestCase testCase) {
        return scenarioIdFrom(featureLoader, testCase);
    }

    private static String scenarioIdFrom(String featureId, String scenarioIdOrExampleId) {
        return (featureId != null && scenarioIdOrExampleId != null) ? String.format("%s;%s", featureId, scenarioIdOrExampleId) : "";
    }

    private void initializeExamples(String scenarioId, URI featurePath) {
        getContext(featurePath).setExamplesRunning(scenarioId, true);
    }

    private Map<Long, Map<String, String>> exampleRows(String scenarioId, URI featurePath) {
        if (getContext(featurePath).getExampleRows(scenarioId) == null) {
            getContext(featurePath).setExampleRows(scenarioId, Collections.synchronizedMap(new HashMap<>()));
        }
        return getContext(featurePath).getExampleRows(scenarioId);
    }

    private Map<Long, List<Tag>> exampleTags(URI featurePath) {
        if (getContext(featurePath).getExampleTags() == null) {
            getContext(featurePath).setExampleTags(Collections.synchronizedMap(new HashMap<>()));
        }
        return getContext(featurePath).getExampleTags();
    }

    private DataTable dataTableFrom(String scenarioOutline,
                                    List<String> headers,
                                    List<Map<String, String>> rows,
                                    String name,
                                    String description,
                                    Map<Integer, Long> lineNumbersOfEachRow) {
        return DataTable.withHeaders(headers)
            .andScenarioOutline(scenarioOutline)
            .andMappedRows(rows, lineNumbersOfEachRow)
            .andTitle(name)
            .andDescription(description)
            .build();
    }

    private DataTable addTableRowsTo(DataTable table, List<String> headers,
                                     List<Map<String, String>> rows,
                                     String name,
                                     String description) {
        table.startNewDataSet(name, description);
        for (Map<String, String> row : rows) {
            table.appendRow(rowValuesFrom(headers, row));
        }
        return table;
    }

    private List<String> rowValuesFrom(List<String> headers, Map<String, String> row) {
        return headers.stream()
            .map(header -> row.get(header))
            .collect(toList());
    }

    private void startOfScenarioLifeCycle(String mainScenarioId, URI featurePath, TestCase testCase, Feature feature, String scenarioName, Scenario scenario, Integer currentLine) {
        ScenarioContextParallel context = getContext(featurePath);
        //StepEventBus stepEventBus = context.stepEventBus();
        boolean newScenario = !scenarioIdFrom(TestSourcesModel.convertToId(feature.getName()), TestSourcesModel.convertToId(scenario.getName())).equals(context.getCurrentScenario(mainScenarioId));
        String currentScenarioId = scenarioIdFrom(TestSourcesModel.convertToId(feature.getName()), TestSourcesModel.convertToId(scenario.getName()));
        context.setCurrentScenario(mainScenarioId, currentScenarioId);
        if (context.examplesAreRunning(mainScenarioId)) {
            if (newScenario) {
                startScenario(mainScenarioId, featurePath, testCase, feature, scenario, scenario.getName());
                context.addHighPriorityStepEventBusEvent(mainScenarioId,
                    new UseExamplesFromEvent(context.getTable(mainScenarioId)));
                context.addHighPriorityStepEventBusEvent(mainScenarioId,
                    new UseScenarioOutlineEvent(ScenarioOutlineDescription.from(scenario).getDescription()));
            } else {
                context.addStepEventBusEvent(
                    new AddNewExamplesFromEvent(context.getTable(mainScenarioId)));
            }
            startProcessingExampleLine(mainScenarioId, featurePath, testCase, Long.valueOf(currentLine), scenarioName);
        } else {
            startScenario(mainScenarioId, featurePath, testCase, feature, scenario, scenarioName);
        }
    }

    private void startScenario(String scenarioId, URI featurePath, TestCase testCase, Feature currentFeature, Scenario scenarioDefinition, String scenarioName) {
        ScenarioContextParallel context = getContext(featurePath);
        context.addHighPriorityStepEventBusEvent(scenarioId,
            new SetTestSourceEvent(TestSourceType.TEST_SOURCE_CUCUMBER.getValue()));

        LOGGER.debug("SRP:startScenario" + " " + featurePath + " "
            + Thread.currentThread() + " " + testCase.getId() + " at line " + testCase.getLocation().getLine());

        RestartBrowserForEach restartBrowserForEach = RestartBrowserForEach.configuredIn(SerenityInfrastructure.getEnvironmentVariables());
        if (restartBrowserForEach.restartBrowserForANew(RestartBrowserForEach.SCENARIO)) {
            reinitializeRemoteWebDriver();
        }

        context.addHighPriorityStepEventBusEvent(scenarioId,
            new TestStartedEvent(scenarioId,
                scenarioName,
                scenarioIdFrom(TestSourcesModel.convertToId(currentFeature.getName()),
                    TestSourcesModel.convertToId(scenarioName))));

        context.addStepEventBusEvent(
            new AddDescriptionToCurrentTestEvent(scenarioDefinition.getDescription()));

        context.addStepEventBusEvent(
            new AddTagsToCurrentTestEvent(convertCucumberTags(currentFeature.getTags())));
        context.addStepEventBusEvent(
            new AddTagsToCurrentTestEvent(tagsInEnclosingRule(currentFeature, scenarioDefinition)));
        if (isScenario(scenarioDefinition)) {
            context.addStepEventBusEvent(
                new AddTagsToCurrentTestEvent(convertCucumberTags(scenarioDefinition.getTags())));
        } else if (isScenarioOutline(scenarioDefinition)) {
            context.addStepEventBusEvent(
                new AddTagsToCurrentTestEvent(convertCucumberTags(scenarioDefinition.getTags())));
        }

        registerFeatureJiraIssues(featurePath, testCase, currentFeature.getTags());
        List<Tag> tags = getTagsOfScenarioDefinition(scenarioDefinition);
        registerScenarioJiraIssues(featurePath, testCase, tags);

        List<Tag> scenarioTags = tagsForScenario(featurePath, scenarioDefinition);
        context.setScenarioTags(scenarioId, scenarioTags);
        updateResultFromTags(scenarioId, featurePath, testCase, scenarioTags);
    }

    private List<TestTag> tagsInEnclosingRule(Feature feature, Scenario scenario) {
        List<io.cucumber.messages.types.Rule> nestedRules = feature.getChildren().stream()
            .filter(fc -> fc.getRule().isPresent())
            .map(fc -> fc.getRule().get())
            .filter(Objects::nonNull)
            .collect(toList());

        return nestedRules.stream()
            .filter(rule -> containsScenario(rule, scenario))
            .flatMap(rule -> convertCucumberTags(rule.getTags()).stream())
            .collect(toList());
    }

    private boolean containsScenario(io.cucumber.messages.types.Rule rule, Scenario scenario) {
        return rule.getChildren().stream().anyMatch(child -> child.getScenario().isPresent() && child.getScenario().get() == scenario);
    }

    private List<Tag> tagsForScenario(URI featurePath, Scenario scenarioDefinition) {
        List<Tag> scenarioTags = new ArrayList<>(getContext(featurePath).getFeatureTags());
        scenarioTags.addAll(getTagsOfScenarioDefinition(scenarioDefinition));
        return scenarioTags;
    }


    private boolean isScenario(Scenario scenarioDefinition) {
        return scenarioDefinition.getExamples().isEmpty();
    }

    private boolean isScenarioOutline(Scenario scenarioDefinition) {
        return scenarioDefinition.getExamples().size() > 0;
    }

    private List<Tag> getTagsOfScenarioDefinition(Scenario scenarioDefinition) {
        List<Tag> tags = new ArrayList<>();
        if (isScenario(scenarioDefinition)) {
            tags = scenarioDefinition.getTags();
        } else if (isScenarioOutline(scenarioDefinition)) {
            tags = scenarioDefinition.getTags();
        }
        return tags;
    }

    private void registerFeatureJiraIssues(URI featurePath, TestCase testCase, List<Tag> tags) {
        List<String> issues = extractJiraIssueTags(tags);
        if (!issues.isEmpty()) {
            getContext(featurePath).addStepEventBusEvent(new AddIssuesToCurrentStoryEvent(issues));
        }
    }

    private void registerScenarioJiraIssues(URI featurePath, TestCase testCase, List<Tag> tags) {
        List<String> issues = extractJiraIssueTags(tags);
        if (!issues.isEmpty()) {
            getContext(featurePath).addStepEventBusEvent(new AddIssuesToCurrentTestEvent(issues));
        }
    }

    private List<TestTag> convertCucumberTags(List<Tag> cucumberTags) {

        cucumberTags = completeManualTagsIn(cucumberTags);

        return cucumberTags.stream()
            .map(tag -> TestTag.withValue(tag.getName().substring(1)))
            .collect(toList());
    }

    private List<Tag> completeManualTagsIn(List<Tag> cucumberTags) {
        if (unqualifiedManualTag(cucumberTags).isPresent() && doesNotContainResultTag(cucumberTags)) {
            List<Tag> updatedTags = Lists.newArrayList(cucumberTags);

            Tag newManualTag = new Tag(unqualifiedManualTag(cucumberTags).get().getLocation(),
                "@manual:pending",
                UUID.randomUUID().toString());
            updatedTags.add(newManualTag);
            return updatedTags;
        } else {
            return cucumberTags;
        }
    }

    private boolean doesNotContainResultTag(List<Tag> tags) {
        return !tags.stream().noneMatch(tag -> tag.getName().startsWith("@manual:"));
    }

    private Optional<Tag> unqualifiedManualTag(List<Tag> tags) {
        return tags.stream().filter(tag -> tag.getName().equalsIgnoreCase("@manual")).findFirst();
    }

    private List<String> extractJiraIssueTags(List<Tag> cucumberTags) {
        List<String> issues = new ArrayList<>();
        for (Tag tag : cucumberTags) {
            if (tag.getName().startsWith("@issue:")) {
                String tagIssueValue = tag.getName().substring("@issue:".length());
                issues.add(tagIssueValue);
            }
            if (tag.getName().startsWith("@issues:")) {
                String tagIssuesValues = tag.getName().substring("@issues:".length());
                issues.addAll(Arrays.asList(tagIssuesValues.split(",")));
            }
        }
        return issues;
    }

    private void startProcessingExampleLine(String scenarioId, URI featurePath, TestCase testCase, Long lineNumber, String scenarioName) {
        Map<String, String> data = exampleRows(scenarioId, featurePath).get(lineNumber);
        getContext(featurePath).addStepEventBusEvent(
            new ClearStepFailuresEvent());

        RestartBrowserForEach restartBrowserForEach = RestartBrowserForEach.configuredIn(SerenityInfrastructure.getEnvironmentVariables());
        if (restartBrowserForEach.restartBrowserForANew(RestartBrowserForEach.EXAMPLE)) {
            reinitializeRemoteWebDriver();
        }

        getContext(featurePath).addStepEventBusEvent(
            new ExampleStartedEvent(data, scenarioName));
        if (exampleTags(featurePath).containsKey(lineNumber)) {
            List<Tag> currentExampleTags = exampleTags(featurePath).get(lineNumber);
            getContext(featurePath).addStepEventBusEvent(
                new AddTagsToCurrentTestEvent(convertCucumberTags(currentExampleTags)));
        }
    }

    private void reinitializeRemoteWebDriver() {
        WebDriver webDriver = SerenityWebdriverManager.inThisTestThread().getCurrentDriver();
        if ((webDriver != null) && (webDriver instanceof WebDriverFacade)) {
            ((WebDriverFacade) webDriver).reinitializeRemoteWebDriver();
        }
    }


    private void finishProcessingExampleLine(String scenarioId, URI featurePath, TestCase testCase) {
        getContext(featurePath).addStepEventBusEvent(new ExampleFinishedEvent());
        getContext(featurePath).decrementExampleCount(scenarioId);
        if (getContext(featurePath).getExampleCount(scenarioId) == 0) {
            getContext(featurePath).setExamplesRunning(scenarioId, false);
            setTableScenarioOutline(scenarioId, featurePath);
        } else {
            getContext(featurePath).setExamplesRunning(scenarioId, true);
        }
    }

    private void setTableScenarioOutline(String scenarioId, URI featurePath) {
        List<io.cucumber.messages.types.Step> steps = getContext(featurePath).getCurrentScenarioDefinition(scenarioId).getSteps();
        StringBuffer scenarioOutlineBuffer = new StringBuffer();
        for (io.cucumber.messages.types.Step step : steps) {
            scenarioOutlineBuffer.append(step.getKeyword()).append(step.getText()).append("\n\r");
        }
        String scenarioOutline = scenarioOutlineBuffer.toString();
        if (getContext(featurePath).getTable(scenarioId) != null) {
            getContext(featurePath).getTable(scenarioId).setScenarioOutline(scenarioOutline);
        }
    }


    private void handleBackground(URI featurePath, String scenarioId, Background background) {
        getContext(featurePath).setWaitingToProcessBackgroundSteps(scenarioId, true);
        String backgroundName = background.getName();
        if (backgroundName != null) {
            getContext(featurePath).addStepEventBusEvent(
                new SetBackgroundTitleEvent(backgroundName));
        }
        String backgroundDescription = background.getDescription();
        if (backgroundDescription == null) {
            backgroundDescription = "";
        }
        getContext(featurePath).addStepEventBusEvent(
            new SetBackgroundDescriptionEvent(backgroundDescription));

    }

    private void assureTestSuiteFinished() {
        contextURISet.forEach(featurePath -> {
            getContext(featurePath).clearStepQueue();
            getContext(featurePath).clearTestStepQueue();
            getContext(featurePath).clearTable();
            getContext(featurePath).addCurrentScenarioId(null);
        });

        contextURISet.forEach(this::cleanupTestResourcesForURI);
        contextURISet.clear();

        Serenity.done();

    }

    private void cleanupTestResourcesForURI(URI uri) {
        getStepEventBus(uri).testSuiteFinished();
        getStepEventBus(uri).dropAllListeners();
        getStepEventBus(uri).clear();
        StepEventBus.clearEventBusFor(uri);
    }

    private void handleResult(String scenarioId, URI featurePath, TestCase testCase, Result result, boolean isInDataDrivenTest) {
        io.cucumber.messages.types.Step currentStep = getContext(featurePath).nextStep(testCase);
        TestStep currentTestStep = getContext(featurePath).nextTestStep(testCase);
        recordStepResult(featurePath, result, currentStep, currentTestStep, isInDataDrivenTest);
        if (getContext(featurePath).noStepsAreQueued(testCase)) {
            recordFinalResult(scenarioId, featurePath, testCase);
        }
    }

    private void recordStepResult(URI featurePath, Result result, io.cucumber.messages.types.Step currentStep, TestStep currentTestStep, boolean isInDataDrivenTest) {
        ZonedDateTime endTime = ZonedDateTime.now();
        List<ScreenshotAndHtmlSource> screenshotList = getContext(featurePath).stepEventBus().takeScreenshots();
        getContext(featurePath).addStepEventBusEvent(new StepFinishedWithResultEvent(result, currentStep, currentTestStep, screenshotList, endTime, isInDataDrivenTest));
    }

    private void recordFinalResult(String scenarioId, URI featurePath, TestCase testCase) {
        ScenarioContextParallel context = getContext(featurePath);
        if (context.isWaitingToProcessBackgroundSteps(scenarioId)) {
            context.setWaitingToProcessBackgroundSteps(scenarioId, false);
        } else {
            updateResultFromTags(scenarioId, featurePath, testCase, context.getScenarioTags(scenarioId));
        }
    }

    private void updateResultFromTags(String scenarioId, URI featurePath, TestCase testCase, List<Tag> scenarioTags) {
        if (isManual(scenarioTags)) {
            updateManualResultsFrom(scenarioId, featurePath, testCase, scenarioTags);
        } else if (isPending(scenarioTags)) {
            getContext(featurePath).addStepEventBusEvent(new SetTestPendingEvent());
        } else if (isSkippedOrWIP(scenarioTags)) {
            getContext(featurePath).addStepEventBusEvent(new SetTestSkippedEvent());
            updateCurrentScenarioResultTo(featurePath, TestResult.SKIPPED);
        } else if (isIgnored(scenarioTags)) {
            getContext(featurePath).addStepEventBusEvent(new SetTestIgnoredEvent());
            updateCurrentScenarioResultTo(featurePath, TestResult.IGNORED);
        }
    }

    private void updateManualResultsFrom(String scenarioId, URI featurePath, TestCase testCase, List<Tag> scenarioTags) {
        getContext(featurePath).addStepEventBusEvent(new SetTestManualEvent(getContext(featurePath), scenarioTags, scenarioId));
    }

    private void updateCurrentScenarioResultTo(URI featurePath, TestResult testResult) {
        getContext(featurePath).addStepEventBusEvent(new OverrideResultToEvent(testResult));
    }

    public static String stepTitleFrom(io.cucumber.messages.types.Step currentStep, TestStep testStep) {
        if (currentStep != null && testStep instanceof PickleStepTestStep)
            return currentStep.getKeyword()
                + ((PickleStepTestStep) testStep).getStep().getText()
                + embeddedTableDataIn((PickleStepTestStep) testStep);
        return "";
    }

    private static String embeddedTableDataIn(PickleStepTestStep currentStep) {
        if (currentStep.getStep().getArgument() != null) {
            StepArgument stepArgument = currentStep.getStep().getArgument();
            if (stepArgument instanceof DataTableArgument) {
                List<Map<String, Object>> rowList = new ArrayList<Map<String, Object>>();
                for (List<String> row : ((DataTableArgument) stepArgument).cells()) {
                    Map<String, Object> rowMap = new HashMap<String, Object>();
                    rowMap.put("cells", row);
                    rowList.add(rowMap);
                }
                return convertToTextTable(rowList);
            }
        }
        return "";
    }

    private static String convertToTextTable(List<Map<String, Object>> rows) {
        StringBuilder textTable = new StringBuilder();
        textTable.append(System.lineSeparator());
        for (Map<String, Object> row : rows) {
            textTable.append("|");
            for (String cell : (List<String>) row.get("cells")) {
                textTable.append(" ");
                textTable.append(cell);
                textTable.append(" |");
            }
            if (row != rows.get(rows.size() - 1)) {
                textTable.append(System.lineSeparator());
            }
        }
        return textTable.toString();
    }

    public static final Map<String, ExternalLink> EXTERNAL_LINK_CACHE = new ConcurrentHashMap<>();
    public static final Map<String, String> SESSION_ID_CACHE = new ConcurrentHashMap<>();

    private void enrichOutcomes() {
        getAllTestOutcomes().forEach(
            outcome -> {
                String key = outcome.getUserStory().getName() + "/" + outcome.getName();
                if (EXTERNAL_LINK_CACHE.containsKey(key)) {
                    outcome.setLink(EXTERNAL_LINK_CACHE.get(key));
                }
                if (SESSION_ID_CACHE.containsKey(key)) {
                    outcome.setSessionId(SESSION_ID_CACHE.get(key));
                }
            }
        );
    }

    private void generateReports() {
        List<TestOutcome> allTestOutcomes = getAllTestOutcomes();
        LOGGER.debug("SRP:AllTestOutcomes " + allTestOutcomes.size());
        getReportService().generateReportsFor(allTestOutcomes);
    }


    public List<TestOutcome> getAllTestOutcomes() {
        List<BaseStepListener> allBaseStepListeners = new ArrayList<>();
        localContexts.forEach((uri, context) -> {
                LOGGER.debug("SRP:AllTestOutcomes for uri " + uri);
                context.collectAllBaseStepListeners(allBaseStepListeners);
            }
        );
        return allBaseStepListeners.stream().map(BaseStepListener::getTestOutcomes).flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private String normalized(String value) {
        return value.replaceAll(OPEN_PARAM_CHAR, "{").replaceAll(CLOSE_PARAM_CHAR, "}");
    }

    private String trim(String stringToBeTrimmed) {
        return (stringToBeTrimmed == null) ? null : stringToBeTrimmed.trim();
    }
}
