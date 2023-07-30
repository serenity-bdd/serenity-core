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
import net.serenitybdd.cucumber.formatting.ScenarioOutlineDescription;
import net.serenitybdd.cucumber.util.PathUtils;
import net.serenitybdd.cucumber.util.StepDefinitionAnnotationReader;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Rule;
import net.thucydides.core.model.*;
import net.thucydides.core.model.screenshots.StepDefinitionAnnotations;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.requirements.FeatureFilePath;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.steps.*;
import net.thucydides.core.steps.events.*;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.core.util.Inflector;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SerenityWebdriverManager;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
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

    private Configuration systemConfiguration;

    private final static String FEATURES_ROOT_PATH = "/features/";
    private final static String FEATURES_CLASSPATH_ROOT_PATH = ":features/";

    private FeatureFileLoader featureLoader = new FeatureFileLoader();

    private LineFilters lineFilters;


    private static final Logger LOGGER = LoggerFactory.getLogger(SerenityReporter.class);

    private final Set<URI> contextURISet = new CopyOnWriteArraySet<>();

    /**
     * key = feature URI; value = ScenarioContextParallel
     */
    private final Map<URI, ScenarioContextParallel> localContexts = Collections.synchronizedMap(new HashMap<>());

    private ScenarioContextParallel getContext(URI featureURI) {
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

    private FeaturePathFormatter featurePathFormatter = new FeaturePathFormatter();

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

    private EventHandler<TestSourceRead> testSourceReadHandler = this::handleTestSourceRead;
    private EventHandler<TestCaseStarted> caseStartedHandler = this::handleTestCaseStarted;
    private EventHandler<TestCaseFinished> caseFinishedHandler = this::handleTestCaseFinished;
    private EventHandler<TestStepStarted> stepStartedHandler = this::handleTestStepStarted;
    private EventHandler<TestStepFinished> stepFinishedHandler = this::handleTestStepFinished;
    private EventHandler<TestRunStarted> runStartedHandler = this::handleTestRunStarted;
    private EventHandler<TestRunFinished> runFinishedHandler = this::handleTestRunFinished;
    private EventHandler<WriteEvent> writeEventHandler = this::handleWrite;

    private void handleTestRunStarted(TestRunStarted event) {
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

    private void handleTestSourceRead(TestSourceRead event) {
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
            try {
                pathURIAsString = URLDecoder.decode(fullPathUri.toString(), StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                pathURIAsString = fullPathUri.toString();
            }
        } else {
            pathURIAsString = fullPathUri.toString();
        }

        if (pathURIAsString.contains(FEATURES_ROOT_PATH)) {
            return StringUtils.substringAfterLast(pathURIAsString, FEATURES_ROOT_PATH);
        } else {
            return pathURIAsString;
        }
    }

    private Optional<Feature> featureFrom(URI featureFileUri) {

        LOGGER.debug("Running feature from " + featureFileUri.toString());
        if (!featureFileUri.toString().contains(FEATURES_ROOT_PATH) && !featureFileUri.toString().contains(FEATURES_CLASSPATH_ROOT_PATH)) {
            LOGGER.warn("Feature from " + featureFileUri + " is not under the 'features' directory. Requirements report will not be correctly generated!");
        }
        String defaultFeatureId = PathUtils.getAsFile(featureFileUri).getName().replace(".feature", "");
        String defaultFeatureName = Inflector.getInstance().humanize(defaultFeatureId);

        parseGherkinIn(featureFileUri);

        if (isEmpty(featureLoader.getFeatureName(featureFileUri))) {
            return Optional.empty();
        }

        Feature feature = featureLoader.getFeature(featureFileUri);
        if (feature.getName().isEmpty()) {
            feature = featureLoader.featureWithDefaultName(feature, defaultFeatureName);
        }
        return Optional.of(feature);
    }

    private void parseGherkinIn(URI featureFileUri) {
        try {
            featureLoader.getFeature(featureFileUri);
        } catch (Throwable ignoreParsingErrors) {
            LOGGER.warn("Could not parse the Gherkin in feature file " + featureFileUri + ": file ignored");
        }
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

    private void handleTestCaseStarted(TestCaseStarted event) {
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
                    if (currentScenarioDefinition.getExamples().size() > 0) {
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
                TestSourcesModel.getBackgroundForTestCase(astNode).ifPresent(background -> handleBackground(featurePath, event.getTestCase(), background));
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
            t.printStackTrace();
            throw t;
        }
    }

    private static Map<UUID, TestResult> MANUAL_TEST_RESULTS_CACHE = new HashMap<>();

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

    private void handleTestCaseFinished(TestCaseFinished event) {
        LOGGER.debug("SRP:handleTestCaseFinished " + " " + event.getTestCase().getUri()
                + " " + Thread.currentThread() + " " + event.getTestCase().getId() + " at line " + event.getTestCase().getLocation().getLine());
        URI featurePath = event.getTestCase().getUri();
        Optional<Feature> currentFeature = featureFrom(featurePath);

        TestSourcesModel.AstNode astNode = featureLoader.getAstNode(featurePath, event.getTestCase().getLocation().getLine());
        Scenario currentScenarioDefinition = TestSourcesModel.getScenarioDefinition(astNode);
        String scenarioId = scenarioIdFrom(currentFeature.get().getName(), TestSourcesModel.convertToId(currentScenarioDefinition.getName()));

        if (getContext(featurePath).examplesAreRunning(scenarioId)) {
            handleResult(scenarioId, featurePath, event.getTestCase(), event.getResult());
            finishProcessingExampleLine(scenarioId, featurePath, event.getTestCase());
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
            //getContext(featurePath).storeAllStepEventBusEventsForLine(event.getTestCase().getLocation().getLine(),event.getTestCase());
        }
        getContext(featurePath).storeAllStepEventBusEventsForLine(event.getTestCase().getLocation().getLine(), event.getTestCase());
        getContext(featurePath).clearStepQueue(event.getTestCase());
        getContext(featurePath).stepEventBus().clear();

        // We need to close the driver here to avoid wasting resources and causing timeouts with Selenium Grid services
        getContext(featurePath).stepEventBus().getBaseStepListener().cleanupWebdriverInstance(getContext(featurePath).stepEventBus().isCurrentTestDataDriven());

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
        //BaseStepListener baseStepListener = getStepEventBus(event.getTestCase().getUri()).getBaseStepListener();
        BaseStepListener baseStepListener = getStepEventBus(event.getTestCase().getUri()).getBaseStepListener();
        return (baseStepListener.getTestOutcomes().isEmpty() || (latestOf(baseStepListener.getTestOutcomes()).getAnnotatedResult() == null));
    }

    private TestOutcome latestOf(List<TestOutcome> testOutcomes) {
        return testOutcomes.get(testOutcomes.size() - 1);
    }

    private void handleTestStepStarted(TestStepStarted event) {
        URI featurePath = event.getTestCase().getUri();
        TestSourcesModel.AstNode mainAstNode = featureLoader.getAstNode(featurePath, event.getTestCase().getLocation().getLine());
        Scenario currentScenarioDefinition = TestSourcesModel.getScenarioDefinition(mainAstNode);
        Optional<Feature> currentFeature = featureFrom(featurePath);
        String scenarioId = scenarioIdFrom(currentFeature.get().getName(), TestSourcesModel.convertToId(currentScenarioDefinition.getName()));
        LOGGER.debug("SRP:handleTestStepStarted " + " " + event.getTestCase().getUri() + " " + Thread.currentThread()
                + " " + event.getTestCase().getId() + " at line " + event.getTestCase().getLocation().getLine());

        // Broadcaster.getEventBus().register();

        StepDefinitionAnnotations.setScreenshotPreferencesTo(
                StepDefinitionAnnotationReader
                        .withScreenshotLevel((TakeScreenshots) systemConfiguration.getScreenshotLevel()
                                .orElse(TakeScreenshots.UNDEFINED))
                        .forStepDefinition(event.getTestStep().getCodeLocation())
                        .getScreenshotPreferences());

        if (!(event.getTestStep() instanceof HookTestStep)) {
            if (event.getTestStep() instanceof PickleStepTestStep) {
                PickleStepTestStep pickleTestStep = (PickleStepTestStep) event.getTestStep();
                TestSourcesModel.AstNode astNode = featureLoader.getAstNode(event.getTestCase().getUri(), pickleTestStep.getStepLine());
                if (astNode != null) {
                    //io.cucumber.core.internal.gherkin.ast.Step step = (io.cucumber.core.internal.gherkin.ast.Step) astNode.node;
                    io.cucumber.messages.types.Step step = (io.cucumber.messages.types.Step) astNode.node;
                    if (!getContext(featurePath).isAddingScenarioOutlineSteps(scenarioId)) {
                        getContext(featurePath).queueStep(event.getTestCase(), step);
                        getContext(featurePath).queueTestStep(event.getTestCase(), event.getTestStep());
                    }
                    if (getContext(featurePath).isAScenarioOutline(scenarioId)) {
                        int lineNumber = event.getTestCase().getLocation().getLine();
                        getContext(featurePath).addStepEventBusEvent(new UpdateExampleLineNumberEvent(lineNumber));
                    }
                    io.cucumber.messages.types.Step currentStep = getContext(featurePath).getCurrentStep(event.getTestCase());
                    String stepTitle = stepTitleFrom(currentStep, pickleTestStep);
                    getContext(featurePath).addStepEventBusEvent(
                            new StepStartedEvent(ExecutedStepDescription.withTitle(stepTitle)));
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

    private void handleTestStepFinished(TestStepFinished event) {
        LOGGER.debug("SRP:handleTestStepFinished " + " " + event.getTestCase().getUri() +
                " " + Thread.currentThread() + " " + event.getTestCase().getId() +
                " at line " + event.getTestCase().getLocation().getLine());
        if (!(event.getTestStep() instanceof HookTestStep)) {
            URI featurePath = event.getTestCase().getUri();
            TestSourcesModel.AstNode astNode = featureLoader.getAstNode(featurePath, event.getTestCase().getLocation().getLine());
            Optional<Feature> currentFeature = featureFrom(featurePath);
            String scenarioId = "";
            if ((astNode != null) && currentFeature.isPresent()) {
                Scenario currentScenarioDefinition = TestSourcesModel.getScenarioDefinition(astNode);
                scenarioId = scenarioIdFrom(currentFeature.get().getName(), TestSourcesModel.convertToId(currentScenarioDefinition.getName()));
                handleResult(scenarioId, event.getTestCase().getUri(), event.getTestCase(), event.getResult());
            }
            StepDefinitionAnnotations.clear();
        }

    }

    private void handleTestRunFinished(TestRunFinished event) {
        LOGGER.debug("SRP:handleTestRunFinished " + Thread.currentThread() + " " + contextURISet);
        try {
            contextURISet.forEach(featurePath -> {
                getContext(featurePath).playAllTestEvents();
            });
        } catch (Throwable th) {
            th.printStackTrace();
        }
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

    private String scenarioIdFrom(String featureId, String scenarioIdOrExampleId) {
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

        reinitializeRemoteWebDriver();

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

        reinitializeRemoteWebDriver();

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
        if ((webDriver !=  null) && (webDriver instanceof WebDriverFacade)) {
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


    private void handleBackground(URI featurePath, TestCase testCase, Background background) {
        getContext(featurePath).setWaitingToProcessBackgroundSteps(true);
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

    private void handleResult(String scenarioId, URI featurePath, TestCase testCase, Result result) {
        io.cucumber.messages.types.Step currentStep = getContext(featurePath).nextStep(testCase);
        TestStep currentTestStep = getContext(featurePath).nextTestStep(testCase);
        recordStepResult(featurePath, testCase, result, currentStep, currentTestStep);
        if (getContext(featurePath).noStepsAreQueued(testCase)) {
            recordFinalResult(scenarioId, featurePath, testCase);
        }
    }

    private void recordStepResult(URI featurePath, TestCase testCase, Result result, io.cucumber.messages.types.Step currentStep, TestStep currentTestStep) {
        List<ScreenshotAndHtmlSource> screenshotList = getContext(featurePath).stepEventBus().takeScreenshots();
        getContext(featurePath).addStepEventBusEvent(new RecordStepResultEvent(result, currentStep, currentTestStep, screenshotList));
    }

    private void recordFinalResult(String scenarioId, URI featurePath, TestCase testCase) {
        ScenarioContextParallel context = getContext(featurePath);
        if (context.isWaitingToProcessBackgroundSteps()) {
            context.setWaitingToProcessBackgroundSteps(false);
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
