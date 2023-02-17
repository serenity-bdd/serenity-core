package io.cucumber.core.plugin;

import io.cucumber.messages.types.Tag;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.steps.StepEventBus;

import java.net.URI;
import java.util.List;

public class ScenarioTagProcessor {
    public static TestResult processScenarioTags(List<Tag> tags, URI scenarioUri) {
        StepEventBus eventBus = StepEventBus.eventBusFor(scenarioUri);
        if (TaggedScenario.isManual(tags)) {
            eventBus.testIsManual();
            eventBus.enableDryRun();
            TestResult manualResult = TestResult.SUCCESS;
            if (TaggedScenario.manualResultDefinedIn(tags).isPresent()) {
                manualResult = TaggedScenario.manualResultDefinedIn(tags).get();
                eventBus.getBaseStepListener().recordManualTestResult(manualResult);
            }
            return manualResult;
        } else if (TaggedScenario.isPending(tags)) {
            eventBus.testPending();
            return TestResult.PENDING;
        } else if (TaggedScenario.isIgnored(tags)) {
            eventBus.testIgnored();
            return TestResult.IGNORED;
        } else if (TaggedScenario.isSkippedOrWIP(tags)) {
            eventBus.testSkipped();
            return TestResult.SKIPPED;
        } else {
            return TestResult.SUCCESS;
        }
    }
}
