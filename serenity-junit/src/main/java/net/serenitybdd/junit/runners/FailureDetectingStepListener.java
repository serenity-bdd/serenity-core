package net.serenitybdd.junit.runners;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.steps.StepListenerAdapter;
import net.thucydides.model.steps.TestFailureCause;

import java.util.ArrayList;
import java.util.List;

public class FailureDetectingStepListener extends StepListenerAdapter {

    private boolean lastTestFailed = false;
    private final List<String> failureMessages = new ArrayList<>();
    private TestFailureCause testFailureCause;

    public void reset() {
        lastTestFailed = false;
        failureMessages.clear();
    }

    public boolean lastTestFailed() {
        return lastTestFailed;
    }

    public void testFailed(TestOutcome testOutcome, Throwable cause) {
        lastTestFailed = true;
        String failingStep = testOutcome.getFailingStep().isPresent() ? testOutcome.getFailingStep().get().getDescription() + ":" : "";
        failureMessages.add(failingStep + testOutcome.getErrorMessage());
        testFailureCause = TestFailureCause.from(cause);
    }

    public void testStarted(String description) {
        lastTestFailed = false;
    }

    @Override
    public void testStarted(String description, String id) {
        lastTestFailed = false;
    }


    public TestFailureCause getTestFailureCause(){
        return testFailureCause;
    }

    public List<String> getFailureMessages() {
        return NewList.copyOf(failureMessages);
    }
}
