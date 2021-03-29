package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.SelectedStatus;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

public class CheckCheckboxOfTarget extends ClickOnClickable {
    private final Target target;
    private final boolean expectedToBeChecked;

    public CheckCheckboxOfTarget(Target target, boolean expectedToBeChecked) {
        this.target = target;
        this.expectedToBeChecked = expectedToBeChecked;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        boolean isSelected = actor.asksFor(SelectedStatus.of(target).asABoolean());
        if(isSelected != expectedToBeChecked) actor.attemptsTo(Click.on(target));
    }
}
