package net.thucydides.core.steps;

import net.thucydides.core.steps.events.AddIssuesToCurrentStoryEvent;
import net.thucydides.core.steps.events.AddIssuesToCurrentTestEvent;
import net.thucydides.core.steps.events.StepEventBusEventBase;
import net.thucydides.model.configuration.SystemPropertiesConfiguration;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

/** Regression coverage for scenario issue leakage during parallel Cucumber event replay (#3752). */
public class WhenReplayingScenarioIssueEvents {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private StepEventBus eventBus;
    private BaseStepListener listener;

    public static class IssueTagFeature {}

    @Before
    public void startFeature() throws IOException {
        MockEnvironmentVariables environment = new MockEnvironmentVariables();
        eventBus = new StepEventBus(environment, new SystemPropertiesConfiguration(environment));
        listener = new BaseStepListener(temporaryFolder.newFolder());
        eventBus.registerListener(listener);
        eventBus.testSuiteStarted(IssueTagFeature.class);
    }

    @After
    public void finishFeature() {
        eventBus.testSuiteFinished();
        eventBus.dropListener(listener);
    }

    @Test
    public void each_scenario_should_keep_only_its_own_issue() {
        recordScenario("first scenario", "TEST-101");
        recordScenario("second scenario", "TEST-102");

        List<TestOutcome> outcomes = listener.getTestOutcomes();
        assertThat(outcomes.get(0).getIssueKeys(), containsInAnyOrder("TEST-101"));
        assertThat(outcomes.get(1).getIssueKeys(), containsInAnyOrder("TEST-102"));
    }

    @Test
    public void all_replayed_issues_should_belong_only_to_the_current_scenario() {
        recordScenario("scenario with multiple issues", "TEST-101", "TEST-102");
        recordScenario("untagged scenario");

        List<TestOutcome> outcomes = listener.getTestOutcomes();
        assertThat(outcomes.get(0).getIssueKeys(), containsInAnyOrder("TEST-101", "TEST-102"));
        assertThat(outcomes.get(1).getIssueKeys(), empty());
    }

    @Test
    public void an_untagged_scenario_should_not_inherit_previous_scenario_issues() {
        recordScenario("first scenario", "TEST-101");
        recordScenario("second scenario", "TEST-102");
        recordScenario("untagged scenario");

        assertThat(listener.getTestOutcomes().get(2).getIssueKeys(), empty());
    }

    @Test
    public void feature_issues_should_be_inherited_without_leaking_scenario_issues() {
        replay(new AddIssuesToCurrentStoryEvent(singletonList("FEATURE-100")));
        recordScenario("tagged scenario", "TEST-101");
        recordScenario("untagged scenario");

        List<TestOutcome> outcomes = listener.getTestOutcomes();
        assertThat(outcomes.get(0).getIssueKeys(), containsInAnyOrder("FEATURE-100", "TEST-101"));
        assertThat(outcomes.get(1).getIssueKeys(), containsInAnyOrder("FEATURE-100"));
    }

    @Test
    public void feature_issues_should_apply_to_every_scenario() {
        replay(new AddIssuesToCurrentStoryEvent(singletonList("FEATURE-100")));
        recordScenario("first scenario");
        recordScenario("second scenario");

        for (TestOutcome outcome : listener.getTestOutcomes()) {
            assertThat(outcome.getIssueKeys(), containsInAnyOrder("FEATURE-100"));
        }
    }

    private void recordScenario(String name, String... issues) {
        eventBus.testStarted(name);
        if (issues.length > 0) {
            replay(new AddIssuesToCurrentTestEvent(List.of(issues)));
        }
        eventBus.testFinished();
    }

    private void replay(StepEventBusEventBase event) {
        event.setStepEventBus(eventBus);
        event.play();
    }
}
