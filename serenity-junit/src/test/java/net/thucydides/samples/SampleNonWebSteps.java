package net.thucydides.samples;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

@SuppressWarnings("serial")
public class SampleNonWebSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesRunner.class);

    public SampleNonWebSteps() {
    }

    @Step("A pending step")
    @Pending
    public void stepThatIsPending() {}

    @Step
    @Ignore
    public void stepThatIsIgnored() {}

    @Step
    public void stepThatSucceeds() {
    }

    @Step
    public void anotherStepThatSucceeds() {}

    @Step
    public void stepThatFails() {
        throw new AssertionError("Oh bother!");
    }

    @Step
    public void anotherStepThatFails() {
        stepThatSucceeds();
        throw new AssertionError("Oh crap!");
    }

    @Step
    public void stepWithAParameter(String param) {}

    @Step
    public void stepWithTwoParameters(String param, int i) {}

    public void methodWithError() {
        String s = null;
        s.length();
    }

    @Step
    public void stepWithFailingNonStepMethod() {
        methodWithError();
    }

    public void methodWithMessagelessError() {
        fail();
    }
}
