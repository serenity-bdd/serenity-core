package net.serenitybdd.screenplay.questions.targets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.SelectedValue;
import net.serenitybdd.screenplay.targets.Target;

@Subject("#target")
public class TargetSelectedValue implements Question<String> {

    private final Target target;

    TargetSelectedValue(Target target) {
        this.target = target;
    }

    @Override
    public String answeredBy(Actor actor) {
        return SelectedValue.of(target).answeredBy(actor);
    }
}
