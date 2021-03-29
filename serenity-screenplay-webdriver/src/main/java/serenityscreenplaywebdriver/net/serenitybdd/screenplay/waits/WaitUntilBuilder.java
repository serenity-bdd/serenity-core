package serenityscreenplaywebdriver.net.serenitybdd.screenplay.waits;

import serenitycore.net.serenitybdd.core.pages.WebElementState;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplay.net.serenitybdd.screenplay.SilentInteraction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;
import org.hamcrest.Matcher;

import static serenityscreenplay.net.serenitybdd.screenplay.EventualConsequence.eventually;
import static serenityscreenplay.net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.WebElementQuestion.the;

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
                actor.should(eventually(seeThat(the(target), expectedState))
                        .withNoReporting()
                        .waitingForNoLongerThan(amount).seconds());
            }
        };
    }

    public Interaction milliseconds() {
        return new SilentInteraction() {
            @Override
            public <T extends Actor> void performAs(T actor) {
                actor.should(eventually(seeThat(the(target), expectedState))
                        .withNoReporting()
                        .waitingForNoLongerThan(amount).milliseconds());
            }
        };
    }


}
