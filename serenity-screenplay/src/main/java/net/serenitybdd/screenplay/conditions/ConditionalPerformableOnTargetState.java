package net.serenitybdd.screenplay.conditions;

import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

import org.hamcrest.Matcher;

import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.conditions.ConditionalPerformable;
import net.serenitybdd.screenplay.targets.Target;

public class ConditionalPerformableOnTargetState extends ConditionalPerformable {
    private final Target target;

    private final Matcher<WebElementState> expectedState;

    public ConditionalPerformableOnTargetState(Target target, Matcher<WebElementState> expectedState) {
        super();
        this.target = target;
        this.expectedState = expectedState;
    }

    @Override
    protected Boolean evaluatedConditionFor(Actor actor) {
        return expectedState.matches(the(target).answeredBy(actor));
    }
}
