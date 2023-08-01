package io.cucumber.core.plugin;


import net.thucydides.core.steps.events.StepEventBusEvent;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Step;
import io.cucumber.messages.types.Tag;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestStep;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.DataTableRow;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.session.TestSession;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;



public class ScenarioContextParallel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioContextParallel.class);

    private final Map<String,List<StepEventBusEvent>> highPriorityEventBusEvents = Collections.synchronizedMap(new HashMap<>());
    private final Map<UUID,Queue<Step>> stepQueue = Collections.synchronizedMap(new HashMap<>());
    private final Map<UUID,Queue<TestStep>> testStepQueue = Collections.synchronizedMap(new HashMap<>());
    private final Map<String,Boolean> examplesRunningMap = Collections.synchronizedMap(new HashMap<>());
    private final Map<String,Boolean> addingScenarioOutlineStepsMap = Collections.synchronizedMap(new HashMap<>());
    private final Map<String,DataTable> tableMap = Collections.synchronizedMap(new HashMap<>());;
    //map1: keys are scenario ids
    //map2: keys are line numbers, entries are example rows (key=header, value=rowValue )
    private final Map<String,Map<Long, Map<String, String>>> exampleRowsMap = Collections.synchronizedMap(new HashMap<>());
    //keys are line numbers
    private Map<Long, List<Tag>> exampleTags;

    private final Map<String,AtomicInteger> exampleCountMap = Collections.synchronizedMap(new HashMap<>());

    //key- ScenarioId
    private final Map<String,Boolean> waitingToProcessBackgroundSteps = Collections.synchronizedMap(new HashMap<>());;

    private final List<String> currentScenarioIdList = Collections.synchronizedList(new ArrayList<>());

    //key - scenarioId
    private final Map<String,Scenario> currentScenarioDefinitionMap = Collections.synchronizedMap(new HashMap<>());

    private final Map<String,String> currentScenarioMap =  Collections.synchronizedMap(new HashMap<>());;

    private List<Tag> featureTags = new ArrayList<>();

    private final FeaturePathFormatter featurePathFormatter = new FeaturePathFormatter();

    private final List<BaseStepListener> baseStepListeners;


    // key-line in feature file; value - list with StepBusEvents corresponding to this line.
    private final Map<Integer,List<StepEventBusEvent>> allTestEventsByLine = Collections.synchronizedMap(new TreeMap<>());

    private final URI scenarioContextURI;

    private StepEventBus stepEventBus;

    // key - scenarioId
    private final Map<String,List<Tag>> scenarioTags = Collections.synchronizedMap(new HashMap<>());;


    public ScenarioContextParallel(URI scenarioContextURI) {
        this.baseStepListeners = Collections.synchronizedList(new ArrayList<>());
        this.scenarioContextURI = scenarioContextURI;
        this.stepEventBus = stepEventBus(scenarioContextURI);
    }

    public synchronized Scenario currentScenarioOutline(String scenarioId) {
        return currentScenarioDefinitionMap.get(scenarioId);
    }

    public synchronized Queue<Step> getStepQueue(TestCase testCase) {
        stepQueue.computeIfAbsent(testCase.getId(), k -> new LinkedList<>());
        return stepQueue.get(testCase.getId());
    }

    public synchronized Queue<TestStep> getTestStepQueue(TestCase testCase/*String scenarioId*/) {
        testStepQueue.computeIfAbsent(testCase.getId(), k -> new LinkedList<>());
        return testStepQueue.get(testCase.getId());
    }

    public synchronized boolean examplesAreRunning(String scenarioId) {
        if(!examplesRunningMap.containsKey(scenarioId)) {
            return false;
        }
        return examplesRunningMap.get(scenarioId);
    }

    public synchronized Map<Long, Map<String, String>> getExampleRows(String scenarioId) {
        return exampleRowsMap.get(scenarioId);
    }

    public synchronized void setExampleRows(String scenarioId,Map<Long, Map<String, String>> exampleRows) {
        this.exampleRowsMap.put(scenarioId,exampleRows);
    }

    public synchronized Map<Long, List<Tag>> getExampleTags() {
        return exampleTags;
    }

    //TODO - use a map with scenarioId as key
    public synchronized void setExampleTags(Map<Long, List<Tag>> exampleTags) {
        this.exampleTags =  exampleTags;
    }

    public synchronized int getExampleCount(String scenarioId)  {
        if (exampleCountMap.containsKey(scenarioId)) {
            return exampleCountMap.get(scenarioId).get();
        }
        return 0;
    }

    public synchronized int decrementExampleCount(String scenarioId) {
        if(exampleCountMap.get(scenarioId) != null) {
            return exampleCountMap.get(scenarioId).decrementAndGet();
        }
        //single example
        return 0;
    }

    public synchronized DataTable getTable(String scenarioId) {
        return tableMap.get(scenarioId);
    }

    public synchronized boolean isWaitingToProcessBackgroundSteps(String scenarioId) {
        return waitingToProcessBackgroundSteps.getOrDefault(scenarioId, false);
    }

    public synchronized void addCurrentScenarioId(String scenarioId) {
        if(scenarioId != null) {
            currentScenarioIdList.add(scenarioId);
        }
        else {
            currentScenarioIdList.clear();
        }
    }

    public synchronized Scenario getCurrentScenarioDefinition(String scenarioId) {
        return currentScenarioDefinitionMap.get(scenarioId);
    }

    public synchronized String getCurrentScenario(String scenarioId) {
        return currentScenarioMap.get(scenarioId);
    }

    public synchronized void setCurrentScenario(String scenarioId,String currentScenario) {
        this.currentScenarioMap.put(scenarioId,currentScenario);
    }

    public synchronized List<Tag> getFeatureTags() {
        return featureTags;
    }

    public synchronized boolean isAddingScenarioOutlineSteps(String scenarioId) {
        return addingScenarioOutlineStepsMap.get(scenarioId) != null ? addingScenarioOutlineStepsMap.get(scenarioId) : false;
    }

    public synchronized void doneAddingScenarioOutlineSteps(String scenarioId) {
        this.addingScenarioOutlineStepsMap.put(scenarioId,false);
    }

    public synchronized void setFeatureTags(List<Tag> tags) {
        this.featureTags = new ArrayList<>(tags);
    }

    public synchronized void setCurrentScenarioDefinitionFrom(String scenarioId,TestSourcesModel.AstNode astNode) {
        this.currentScenarioDefinitionMap.put(scenarioId, TestSourcesModel.getScenarioDefinition(astNode));
    }

    public synchronized boolean isAScenarioOutline(String scenarioId) {
        return currentScenarioDefinitionMap.get(scenarioId) != null  &&
                currentScenarioDefinitionMap.get(scenarioId).getExamples().size() > 0;
    }

    public synchronized void startNewExample(String scenarioId) {
        examplesRunningMap.put(scenarioId,true);
        addingScenarioOutlineStepsMap.put(scenarioId,true);
    }

    public synchronized void setExamplesRunning(String scenarioId,boolean examplesRunning) {
        examplesRunningMap.put(scenarioId, examplesRunning);
    }

    /*public synchronized List<Tag> getScenarioTags() {
        return currentScenarioDefinition.getTags();
    }

    public synchronized String getScenarioName() {
        return currentScenarioDefinition.getName();
    }

    public synchronized List<Examples> getScenarioExamples() {
        return currentScenarioDefinition.getExamples();
    }*/

    public synchronized void clearStepQueue(TestCase testCase/*String scenarioId*/) {
        getStepQueue(testCase).clear();
    }

    public synchronized void clearStepQueue() {
        //TODO check
        stepQueue.clear();
        //simpleStepQueue.clear();
    }

    public synchronized void clearTestStepQueue() {
        testStepQueue.clear();
        //simpleStepTestQueue.clear();
    }

    public synchronized void queueStep(TestCase testCase/*String scenarioId,*/,Step step) {
        getStepQueue(testCase).add(step);
    }

    public synchronized void queueTestStep(/*String scenarioId*/TestCase testCase,TestStep testStep) {
        getTestStepQueue(testCase).add(testStep);
    }

    public synchronized Step getCurrentStep(TestCase testCase/*String scenarioId*/) {
        return getStepQueue(testCase/*scenarioId*/).peek();
    }

    public synchronized Step nextStep(TestCase testCase/*String scenarioId*/) {
        return getStepQueue(testCase/*scenarioId*/).poll();
    }

    public synchronized TestStep nextTestStep(TestCase testCase/*String scenarioId*/) {
        return getTestStepQueue(testCase/*scenarioId*/).poll();
    }

    public synchronized boolean noStepsAreQueued(/*String scenarioId*/TestCase testCase) {
        return getStepQueue(testCase/*scenarioId*/).isEmpty();
    }

    public synchronized boolean hasScenarioId(String scenarioId) {
        return (currentScenarioIdList.contains(scenarioId));
    }

    public synchronized void setTable(String scenarioId,DataTable table) {
        this.tableMap.put(scenarioId,table);
        exampleCountMap.put(scenarioId,new AtomicInteger(table.getSize()));
    }

    public synchronized void addTableRows(String scenarioId,List<String> headers,
                             List<Map<String, String>> rows,
                             String name,
                             String description,
                             Map<Integer, Long> lineNumbersOfEachRow) {
        DataTable table = tableMap.get(scenarioId);
        table.startNewDataSet(name, description);

        AtomicInteger rowNumber = new AtomicInteger();
        rows.forEach(
                row -> table.appendRow(newRow(headers, lineNumbersOfEachRow, rowNumber.getAndIncrement(), row))
        );
        table.updateLineNumbers(lineNumbersOfEachRow);
        exampleCountMap.put(scenarioId,new AtomicInteger(table.getSize()));
    }

    @NotNull
    private DataTableRow newRow(List<String> headers,
                                Map<Integer, Long> lineNumbersOfEachRow,
                                int rowNumber,
                                Map<String, String> row) {
        return new DataTableRow(
                rowValuesFrom(headers, row),
                lineNumbersOfEachRow.getOrDefault(rowNumber, 0L));
    }

    private List<String> rowValuesFrom(List<String> headers, Map<String, String> row) {
        return headers.stream().map(row::get).collect(toList());
    }

    public synchronized void addTableTags(String scenarioId,List<TestTag> tags) {
        DataTable table = tableMap.get(scenarioId);
        table.addTagsToLatestDataSet(tags);
    }

    public synchronized void clearTable() {
        tableMap.clear();
    }

    private synchronized StepEventBus stepEventBus(URI featurePath) {
        URI prefixedPath = featurePathFormatter.featurePathWithPrefixIfNecessary(featurePath);
        return StepEventBus.eventBusFor(prefixedPath);
    }

    public synchronized StepEventBus stepEventBus() {
        return this.stepEventBus;
    }

    public void setStepEventBus(StepEventBus stepEventBus) {
        this.stepEventBus = stepEventBus;
    }

    public void addBaseStepListener(BaseStepListener baseStepListener){
        baseStepListeners.add(baseStepListener);
        stepEventBus.registerListener(baseStepListener);
    }


    public synchronized void collectAllBaseStepListeners(List<BaseStepListener>  allBaseStepListeners){
        allBaseStepListeners.addAll(baseStepListeners);
    }


    public void setWaitingToProcessBackgroundSteps(String scenarioId, boolean waitingToProcessBackgroundSteps) {
        this.waitingToProcessBackgroundSteps.put(scenarioId,waitingToProcessBackgroundSteps);
    }

    /**
     * Some events have to be added ad the beginning of the event list.
     *
     * @param scenarioId
     * @param event
     */
    public void addHighPriorityStepEventBusEvent(String scenarioId, StepEventBusEvent event) {
        LOGGER.debug("SRP:addHighPriorityStepEventBusEvent " + event + " " +  Thread.currentThread() + " " + scenarioId);
        List<StepEventBusEvent> eventList = highPriorityEventBusEvents.computeIfAbsent(scenarioId,k->Collections.synchronizedList(new LinkedList<>()));
        eventList.add(event);
        event.setStepEventBus(stepEventBus);
    }

    public void addStepEventBusEvent(StepEventBusEvent event) {
        if(TestSession.isSessionStarted()) {
            TestSession.addEvent(event);
            event.setStepEventBus(stepEventBus);
        } else {
            LOGGER.debug("SRP:ignored event " + event + " " +  Thread.currentThread() + " because session not opened ", new Exception());
        }
    }

    /**
     * Called with TestCaseFinished
     * @param line
     * @param testCase
     */
    public void storeAllStepEventBusEventsForLine(int line, TestCase testCase){
        if(TestSession.isSessionStarted()) {
            TestSession.closeSession();
            List<StepEventBusEvent> stepEventBusEvents = TestSession.getSessionEvents();
            List<StepEventBusEvent> clonedEvents = new ArrayList<>(stepEventBusEvents);
            allTestEventsByLine.put(line,clonedEvents);
            TestSession.cleanupSession();
        }
    }

    /**
     * Called with TestRunFinished - all tests events are replayed
     */
    public synchronized void playAllTestEvents() {
        LOGGER.debug("SRP:playAllTestEvents for URI " +  scenarioContextURI + "--" + allTestEventsByLine);
        allTestEventsByLine.entrySet().forEach((entry) -> replayAllTestCaseEventsForLine(entry.getKey(),entry.getValue()));
        stepEventBus.clear();
        StepEventBus.getParallelEventBus().clear();
    }

    private void replayAllTestCaseEventsForLine(Integer lineNumber, List<StepEventBusEvent> stepEventBusEvents) {
        LOGGER.debug("SRP:PLAY session events for line   " + lineNumber);
        Optional<StepEventBusEvent> eventWithScenarioId = stepEventBusEvents.stream().filter(event -> !event.getScenarioId().isEmpty()).findFirst();
        LOGGER.debug("SRP:EventWithscenarioId   " + eventWithScenarioId);
        if(eventWithScenarioId.isPresent() && highPriorityEventBusEvents.get(eventWithScenarioId.get().getScenarioId()) != null){
            List<StepEventBusEvent> highPriorityEvents = highPriorityEventBusEvents.get(eventWithScenarioId.get().getScenarioId());
            for(StepEventBusEvent currentStepBusEvent : highPriorityEvents) {
               LOGGER.trace("SRP:PLAY session high priority event  " + currentStepBusEvent);
               currentStepBusEvent.play();
            }
            highPriorityEventBusEvents.remove(eventWithScenarioId.get().getScenarioId());
        }
        for(StepEventBusEvent currentStepBusEvent : stepEventBusEvents) {
           LOGGER.trace("SRP:PLAY session event  " + currentStepBusEvent + " " +  Thread.currentThread() + " " + currentStepBusEvent.hashCode());
           currentStepBusEvent.play();
       }
    }

    public List<Tag> getScenarioTags(String scenarioId) {
        return scenarioTags.get(scenarioId);
    }

    public void setScenarioTags(String scenarioId,List<Tag> scenarioTags) {
        this.scenarioTags.put(scenarioId,scenarioTags);
    }
}

