package net.serenitybdd.junit5;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.TestSourceType;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Use this extension to run Serenity BDD tests using JUnit 5
 */
public class SerenityJUnit5Extension implements TestInstancePostProcessor,  AfterEachCallback, BeforeEachCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerenityJUnit5Extension.class);

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) {
        Serenity.injectDriverInto(testInstance);
        Serenity.injectAnnotatedPagesObjectInto(testInstance);
        Serenity.injectScenarioStepsInto(testInstance);
        Serenity.injectDependenciesInto(testInstance);
    }

    private StepEventBus eventBusFor(ExtensionContext context) {
        if (!StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            StepEventBus eventBus = StepEventBus.eventBusFor(context.getUniqueId());
            if (!eventBus.isBaseStepListenerRegistered()) {
                eventBus.registerListener(new BaseStepListener(ConfiguredEnvironment.getConfiguration().getOutputDirectory()));
            }
            StepEventBus.setCurrentBusToEventBusFor(context.getTestMethod());
        }
        return StepEventBus.getEventBus();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        context.getTestMethod().ifPresent(
            method -> {
                if (!eventBusFor(context).isBaseStepListenerRegistered()) {
                    eventBusFor(context).registerListener(new BaseStepListener(ConfiguredEnvironment.getConfiguration().getOutputDirectory()));
                }
                eventBusFor(context).getBaseStepListener().addTagsToCurrentStory(JUnit5Tags.forMethod(method));
                eventBusFor(context).setTestSource(TestSourceType.TEST_SOURCE_JUNIT5.getValue());
            }
        );
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (!StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            LOGGER.warn("NO BASE STEP LISTENER FOUND IN THREAD " + Thread.currentThread());
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

}
