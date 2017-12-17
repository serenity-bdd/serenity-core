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

public class WaitUntil {

    public static WaitUntilTargetIsReady the(Target target, Matcher<WebElementState> expectedState) {
        return  instrumented(WaitUntilTargetIsReady.class, target, expectedState);
    }

    public static Interaction angularRequestsHaveFinished() {
        return instrumented(WaitUntilAngularIsReady.class);
    }
}