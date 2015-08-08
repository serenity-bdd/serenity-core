package net.serenitybdd.screenplay.state;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;

import java.lang.reflect.Method;

/**
 * Created by john on 7/08/2015.
 */
public class TestStateEngine {

    EnvironmentVariables environmentVariables;

    public TestStateEngine() {
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }


    public boolean shouldRunStep(Method methodOrStep) {
        return !(aPreviousStepHasFailed() || testIsPending() || isDryRun() || isPending(methodOrStep) || isIgnored(methodOrStep));
    }

    public boolean shouldRunStep() {
        return !(aPreviousStepHasFailed() || testIsPending() || isDryRun());
    }

    private boolean testIsPending() {
        return StepEventBus.getEventBus().currentTestIsSuspended();
    }

    private boolean isPending(final Method method) {
        return (method.getAnnotation(Pending.class) != null);
    }

    private boolean isIgnored(final Method method) {
        return TestAnnotations.isIgnored(method);
    }

    private boolean aPreviousStepHasFailed() {
        boolean aPreviousStepHasFailed = false;
        if (StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed()) {
            aPreviousStepHasFailed = true;
        }
        return aPreviousStepHasFailed;
    }

    private boolean isDryRun() {
        return ThucydidesSystemProperty.THUCYDIDES_DRY_RUN.booleanFrom(environmentVariables);
    }

    public TestState getState() {
        if (shouldRunStep()) {
            return TestState.RUNNABLE;
        }
        if (isDryRun()) {
            return TestState.DRY_RUN;
        }
        if (testIsPending()) {
            return TestState.PENDING;
        }
        return TestState.FAILED;

    }


    public static TestState currentState() {
        return new TestStateEngine().getState();
    }

}
