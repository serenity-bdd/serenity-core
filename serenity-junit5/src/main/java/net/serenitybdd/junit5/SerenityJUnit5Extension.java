package net.serenitybdd.junit5;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.annotations.environment.AnnotatedEnvironmentProperties;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.logging.ConsoleLoggingListener;
import net.thucydides.model.steps.TestSourceType;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
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
        SystemEnvironmentVariables.currentEnvironment().reset();
    }

    private StepEventBus eventBusFor(ExtensionContext context) {
        if (!StepEventBus.getParallelEventBus().isBaseStepListenerRegistered()) {
            StepEventBus eventBus = StepEventBus.eventBusFor(context.getUniqueId());
            if (!eventBus.isBaseStepListenerRegistered()) {
                eventBus.registerListener(new BaseStepListener(ConfiguredEnvironment.getConfiguration().getOutputDirectory()));
//                eventBus.registerListener(new ConsoleLoggingListener(ConfiguredEnvironment.getEnvironmentVariables()));
                eventBus.registerListener(SerenityInfrastructure.getLoggingListener());
            }
            StepEventBus.setCurrentBusToEventBusFor(context.getTestMethod());
        }
        return StepEventBus.getParallelEventBus();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        context.getTestMethod().ifPresent(
            method -> {
                AnnotatedEnvironmentProperties.apply(method);
                if (!eventBusFor(context).isBaseStepListenerRegistered()) {
                    eventBusFor(context).registerListener(new BaseStepListener(ConfiguredEnvironment.getConfiguration().getOutputDirectory()));
                    eventBusFor(context).registerListener(new ConsoleLoggingListener(ConfiguredEnvironment.getEnvironmentVariables()));
                }
                eventBusFor(context).getBaseStepListener().addTagsToCurrentStory(JUnit5Tags.forMethod(method));
                eventBusFor(context).setTestSource(TestSourceType.TEST_SOURCE_JUNIT5.getValue());
            }
        );
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (!StepEventBus.getParallelEventBus().isBaseStepListenerRegistered()) {
            LOGGER.warn("NO BASE STEP LISTENER FOUND IN THREAD " + Thread.currentThread());
        }
        TestOutcome outcome = StepEventBus.getParallelEventBus().getBaseStepListener().getCurrentTestOutcome();
        String methodName = outcome.getQualifiedMethodName();
        String displayName = context.getDisplayName();

        context.getTestMethod().ifPresent(
                method -> {
                    // Make sure it's the right test - not sure when this gets called in parallel testing.
                    if (methodName != null && (methodName.equals(method.getName())|| methodName.equals(displayName))) {
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
