package net.thucydides.samples;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Assume;
import org.junit.Ignore;
import org.openqa.selenium.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
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

    @Step
    public void stepWithFailingAssumption() {
        Assume.assumeThat(true, is(false));
    }

    public void throw_exception() {
        throw new IllegalArgumentException("Your argument is invalid");
    }

    public void throw_element_not_found_exception() {
        throw new NoSuchElementException("It ain't there, boss");
    }


    static int testCount = 1;

    public void aStepThatFailsOnOddTries() {
        boolean shouldPass = testCount++ % 2 == 0;
        assertThat(shouldPass).isTrue();

    }


    public final static class CurrencyIn$ {
        int value;

        public CurrencyIn$(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "$" + value + ".00";
        }
    }

    @Step("a step with an object parameter called {0}")
    public void a_customized_step_with_object_parameters(CurrencyIn$ currency) {}

    @Step("a step with two object parameters called '{0}' and '{1}'")
    public void a_customized_step_with_two_parameters(String param1, String param2) {}

}
