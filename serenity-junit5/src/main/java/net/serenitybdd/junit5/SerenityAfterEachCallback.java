package net.serenitybdd.junit5;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.StepEventBus;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class SerenityAfterEachCallback implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (!StepEventBus.getParallelEventBus().isBaseStepListenerRegistered()) {
            System.out.println("NO BASE STEP LISTENER FOUND IN THREAD " + Thread.currentThread());
        }
        TestOutcome outcome = StepEventBus.getParallelEventBus().getBaseStepListener().getCurrentTestOutcome();
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
