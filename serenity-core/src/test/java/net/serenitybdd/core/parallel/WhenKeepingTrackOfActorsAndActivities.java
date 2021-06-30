package net.serenitybdd.core.parallel;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class WhenKeepingTrackOfActorsAndActivities {

    BaseStepListener defaultStepListener;
    File temporaryDirectory;

    @Before
    public void registerAStepEventBus() throws IOException {
        File temporaryDirectory = Files.createTempDirectory("testdata").toFile();
        defaultStepListener = new BaseStepListener(temporaryDirectory);
        StepEventBus.getEventBus().registerListener(defaultStepListener);
    }

    @After
    public void clearSession() {
        Serenity.clearSessionVariable(Agent.IN_THE_CURRENT_SESSION);
    }

    @Test
    public void agentsMustRegisterToBeAssignedAListener() {
        Agent james = new SpecialAgent("James","007");

        Agency agency = new Agency();

        // We need to register an agent to be assigned an agent-specific event bus
        agency.registerAgent(james);

        assertThat(agency.baseListenerFor(james))
                .isPresent()
                .matches(baseStepListener -> baseStepListener.get() != defaultStepListener);
    }

    @Test
    public void eachAgentHasASeparateListener() {
        Agent james = new SpecialAgent("James","007");
        Agent max = new SpecialAgent("Max","87");

        Agency agency = new Agency();

        // We need to register an agent to be assigned an agent-specific event bus
        agency.registerAgent(james);
        agency.registerAgent(max);

        BaseStepListener listenerForJames = agency.baseListenerFor(james).get();
        BaseStepListener listenerForMax = agency.baseListenerFor(max).get();

        assertThat(listenerForJames).isNotEqualTo(listenerForMax);
    }

    /**
     * The $AGENT session variable is used to identify which agent is running in a particular thread
     * As soon as this session variable is set, events in that thread will be sent the agent's step listener.
     */
    @Test
    public void theAGENTsessionVariableDeterminesWhichListenerToUse() {
        Agent james = new SpecialAgent("James","007");

        StepEventBus.getEventBus().registerAgent(james);

        assertThat(StepEventBus.getEventBus().getBaseStepListener()).isEqualTo(defaultStepListener);

        Serenity.setSessionVariable(Agent.IN_THE_CURRENT_SESSION).to(james);

        assertThat(StepEventBus.getEventBus().getBaseStepListener()).isNotEqualTo(defaultStepListener);

    }

    @Test
    public void theAgentSpecificListenerIsIdenticalAcrossMultipleThreads() {
        Agent james = new SpecialAgent("James","007");

        StepEventBus.getEventBus().registerAgent(james);

        BaseStepListener expectedListener = Agency.getInstance().baseListenerFor(james).get();

        Serenity.setSessionVariable(Agent.IN_THE_CURRENT_SESSION).to(james);
        asList(
                (Runnable) () -> checkThatTheStepListenerIs(james, expectedListener),
                (Runnable) () -> checkThatTheStepListenerIs(james, expectedListener),
                (Runnable) () -> checkThatTheStepListenerIs(james, expectedListener)
        ).parallelStream().forEach(Runnable::run);
    }

    @Test
    public void thereCanBeMultipleActorsAcrossMultipleThreadsWithActorSpecificListeners() {
        Agent james = new SpecialAgent("James","007");
        Agent max = new SpecialAgent("Max","87");

        StepEventBus.getEventBus().registerAgents(james,max);
        BaseStepListener listenerForJames = Agency.getInstance().baseListenerFor(james).get();
        BaseStepListener listenerForMax = Agency.getInstance().baseListenerFor(max).get();

        asList(
                (Runnable) () -> checkThatTheStepListenerIs(james, listenerForJames),
                (Runnable) () -> checkThatTheStepListenerIs(james, listenerForJames),
                (Runnable) () -> checkThatTheStepListenerIs(max, listenerForMax),
                (Runnable) () -> checkThatTheStepListenerIs(max, listenerForMax)
        ).parallelStream().forEach(Runnable::run);
    }

    private void checkThatTheStepListenerIs(Agent agent, BaseStepListener expectedStepListener) {
        Serenity.setSessionVariable(Agent.IN_THE_CURRENT_SESSION).to(agent);
        assertThat(StepEventBus.getEventBus().getBaseStepListener()).isEqualTo(expectedStepListener);
    }

    @Test
    public void StepEventsAreLoggedSeparatelyForEachAgent() {
        Agent james = new SpecialAgent("James","007");
        Agent max = new SpecialAgent("Max","87");

        StepEventBus.getEventBus().testStarted("simple test");

        StepEventBus.getEventBus().registerAgents(james,max);

        Serenity.setSessionVariable(Agent.IN_THE_CURRENT_SESSION).to(james);
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("James does something"));
        StepEventBus.getEventBus().stepFinished();
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("James does something else"));
        StepEventBus.getEventBus().stepFinished();

        Serenity.setSessionVariable(Agent.IN_THE_CURRENT_SESSION).to(max);
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("Max does something"));
        StepEventBus.getEventBus().stepFinished();
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("Max does something else"));
        StepEventBus.getEventBus().stepFinished();

        StepEventBus.getEventBus().mergeActivitiesToDefaultStepListener(james, max);

        StepEventBus.getEventBus().testFinished();

        TestOutcome testOutcome = defaultStepListener.getTestOutcomes().get(0);

        List<String> topLevelSteps = testOutcome.getTestSteps().stream().map(step -> step.getDescription()).collect(Collectors.toList());
        assertThat(topLevelSteps).containsExactly("James","Max");

        List<String> allSteps = testOutcome.getFlattenedTestSteps().stream().map(step -> step.getDescription()).collect(Collectors.toList());
        assertThat(allSteps).containsExactly("James","James does something", "James does something else","Max", "Max does something", "Max does something else");
    }


    @Test
    public void StepEventsAreLoggedSeparatelyForEachAgentDuringParallelExecution() {
        Agent james = new SpecialAgent("James","007");
        Agent max = new SpecialAgent("Max","87");
        Agent johnny = new SpecialAgent("Johnny","English");

        StepEventBus.getEventBus().testStarted("simple test");

        StepEventBus.getEventBus().registerAgents(james,max, johnny);

        asList(james, max, johnny)
                .parallelStream()
                .forEach(this::performTwoStepsFor);

        StepEventBus.getEventBus().mergeActivitiesToDefaultStepListener(james, max, johnny);

        StepEventBus.getEventBus().testFinished();

        TestOutcome testOutcome = defaultStepListener.getTestOutcomes().get(0);

        List<String> topLevelSteps = testOutcome.getTestSteps().stream().map(TestStep::getDescription).collect(Collectors.toList());
        assertThat(topLevelSteps).containsExactly("James","Max", "Johnny");

        List<String> allSteps = testOutcome.getFlattenedTestSteps().stream().map(TestStep::getDescription).collect(Collectors.toList());
        assertThat(allSteps).containsExactly(
                "James","James does something", "James does something else",
                "Max", "Max does something", "Max does something else",
                "Johnny", "Johnny does something", "Johnny does something else");
    }

    private void performTwoStepsFor(Agent actor) {
        System.out.println("Performing for " + actor.getName());
        Serenity.setSessionVariable(Agent.IN_THE_CURRENT_SESSION).to(actor);
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle(actor.getName() + " does something"));
        StepEventBus.getEventBus().stepFinished();
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle(actor.getName() + " does something else"));
        StepEventBus.getEventBus().stepFinished();
        Serenity.clearSessionVariable(Agent.IN_THE_CURRENT_SESSION);
        System.out.println("Performing done for " + actor.getName());
    }


}
