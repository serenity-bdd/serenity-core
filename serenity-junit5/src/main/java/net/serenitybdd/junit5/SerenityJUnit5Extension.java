package net.serenitybdd.junit5;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.SerenityListeners;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.engine.TestTag;
import org.junit.platform.launcher.TestIdentifier;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Use this extension to run Serenity BDD tests using JUnit 5
 */
public class SerenityJUnit5Extension implements TestInstancePostProcessor, AfterEachCallback, BeforeEachCallback {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) {
        Serenity.injectDriverInto(testInstance);
        Serenity.injectAnnotatedPagesObjectInto(testInstance);
        Serenity.injectScenarioStepsInto(testInstance);
        Serenity.injectDependenciesInto(testInstance);
    }

    private StepEventBus eventBusFor(ExtensionContext context) {
        System.out.println("SETTING EVENT BUS FOR TEST " + context.getUniqueId());
        if (!StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            StepEventBus eventBus = StepEventBus.eventBusFor(context.getUniqueId());
            if (!eventBus.isBaseStepListenerRegistered()) {
                eventBus.registerListener(new BaseStepListener(ConfiguredEnvironment.getConfiguration().getOutputDirectory()));
            }
            StepEventBus.setCurrentBusToEventBusFor(context.getTestMethod());
        }
        System.out.println("  ->EVENT BUS = " + StepEventBus.getEventBus());
        return StepEventBus.getEventBus();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (!StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            System.out.println("NO BASE STEP LISTENER FOUND IN THREAD " + Thread.currentThread());
        }
        TestOutcome outcome = StepEventBus.getEventBus().getBaseStepListener().getCurrentTestOutcome();
        String methodName = outcome.getQualifiedMethodName();
        context.getTestMethod().ifPresent(
                method -> {
                    // Make sure it's the right test - not sure when this gets called in parallel testing.
                    if (method.getName().equals(methodName)) {
                        // Failing test
                        if (outcome.getTestFailureCause() != null) {
                            throw outcome.getTestFailureCause().asRuntimeException();
                        } else if (outcome.isPending()) {
                            throw new PendingTestException(context.getDisplayName());
                        } else if (outcome.isSkipped()) {
                            throw new SkippedTestException(context.getDisplayName());
                        }
                    }
                }
        );
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        context.getTestMethod().ifPresent(
                method -> {
                    if (!eventBusFor(context).isBaseStepListenerRegistered()) {
                        eventBusFor(context).registerListener(new BaseStepListener(ConfiguredEnvironment.getConfiguration().getOutputDirectory()));
                    }
                    eventBusFor(context).getBaseStepListener().addTagsToCurrentStory(JUnit5Tags.forMethod(method));
                }
        );
    }

    private void configureTagsFor(TestIdentifier serenityTest) {
        Set<TestTag> testTags = serenityTest.getTags();
        List<net.thucydides.core.model.TestTag> tags = testTags.stream()
                .map(tag -> net.thucydides.core.model.TestTag.withValue(tag.getName()))
                .collect(Collectors.toList());
        StepEventBus.getEventBus().getBaseStepListener().addTagsToCurrentStory(tags);
    }

}
