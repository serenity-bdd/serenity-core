package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.questions.SelectedStatus;
import net.serenitybdd.screenplay.targets.Target;

public class CheckCheckboxOfTarget extends ClickOnClickable {
    private final Target target;
    private final boolean expectedToBeChecked;

    public CheckCheckboxOfTarget(Target target, boolean expectedToBeChecked) {
        this.target = target;
        this.expectedToBeChecked = expectedToBeChecked;
    }

    @Override
    @Step("{0} sets value of checkbox #target to #expectedToBeChecked")
    public <T extends Actor> void performAs(T actor) {
        boolean isSelected = actor.asksFor(SelectedStatus.of(target));
        if (isSelected != expectedToBeChecked) actor.attemptsTo(Click.on(target));
    }
}
