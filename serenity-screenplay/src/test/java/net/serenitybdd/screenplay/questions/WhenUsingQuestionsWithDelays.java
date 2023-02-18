package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.steps.StepEventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.serenitybdd.screenplay.EventualConsequence.eventually;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

public class WhenUsingQuestionsWithDelays {

    @Before
    public void setup() {
        Serenity.initialize(this);
        StepEventBus.getParallelEventBus().testStarted("some test");
    }

    @After
    public void  cleanup() {
        Serenity.done();
    }

    @Test
    public void should_not_fail_when_an_expected_exception_is_thrown() {
        Actor jane = Actor.named("Jane");
        Clicker clicker = new Clicker();

        jane.should(
                eventually(seeThat(TheClickerValueWithAnExpectedException.of(clicker),
                           equalTo(10)))
                        .ignoringExceptions(IllegalStateException.class)
        );

        assertThat(theTestResult()).isEqualTo(TestResult.SUCCESS);
    }

    class Clicker {
        int count = 0;

        int click() {
            return ++count;
        }
    }



    static class TheClickerValueWithAnExpectedException implements Question<Integer> {
        private final Clicker clicker;

        TheClickerValueWithAnExpectedException(Clicker clicker) {
            this.clicker = clicker;
        }

        public static TheClickerValueWithAnExpectedException of(Clicker clicker) {
            return new TheClickerValueWithAnExpectedException(clicker);
        }

        @Override
        public Integer answeredBy(Actor actor) {
            clicker.click();
            if (clicker.count < 10) {
                throw new IllegalStateException("Ignore this");
            }
            return clicker.count;
        }
    }


    private TestResult theTestResult() {
        return StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes().get(0).getResult();
    }
}
