package net.serenitybdd.screenplay.waits;


import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.questions.WebElementQuestion;
import net.serenitybdd.screenplay.targets.Target;
import org.hamcrest.Matcher;

import static net.serenitybdd.screenplay.EventualConsequence.eventually;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class WaitUntil implements Interaction {
    private final Target target;
    private final Matcher<WebElementState> expectedState;

    public WaitUntil(Target target, Matcher<WebElementState> expectedState) {
        this.target = target;
        this.expectedState = expectedState;
    }

    @Override
    public <A extends Actor> void performAs(A actor) {
        actor.should(eventually(seeThat(WebElementQuestion.the(target),
                expectedState)));
    }

    public static Interaction the(Target target, Matcher<WebElementState> expectedState) {
        return  instrumented(WaitUntil.class, target, expectedState);
    }
}
