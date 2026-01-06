package net.serenitybdd.screenplay.waits;

import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.SilentInteraction;
import net.serenitybdd.screenplay.targets.Target;
import org.hamcrest.Matcher;

import java.time.Duration;

import static net.serenitybdd.screenplay.EventualConsequence.eventually;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

public class WaitUntilBuilder {
    private final Target target;
    private final Matcher<WebElementState> expectedState;
    private long amount;

    public WaitUntilBuilder(long amount, Target target, Matcher<WebElementState> expectedState) {
        this.amount = amount;
        this.target = target;
        this.expectedState = expectedState;
    }

    public Interaction seconds() {
        return new SilentInteraction() {
            @Override
            public <T extends Actor> void performAs(T actor) {
                // Set timeout on the target itself, not just on the EventualConsequence wrapper
                // This ensures element lookups respect the specified timeout
                Target targetWithTimeout = target.waitingForNoMoreThan(Duration.ofSeconds(amount));
                actor.should(eventually(seeThat(the(targetWithTimeout), expectedState))
                        .withNoReporting()
                        .waitingForNoLongerThan(amount).seconds());
            }
        };
    }

    public Interaction milliseconds() {
        return new SilentInteraction() {
            @Override
            public <T extends Actor> void performAs(T actor) {
                // Set timeout on the target itself, not just on the EventualConsequence wrapper
                // This ensures element lookups respect the specified timeout
                Target targetWithTimeout = target.waitingForNoMoreThan(Duration.ofMillis(amount));
                actor.should(eventually(seeThat(the(targetWithTimeout), expectedState))
                        .withNoReporting()
                        .waitingForNoLongerThan(amount).milliseconds());
            }
        };
    }


}
