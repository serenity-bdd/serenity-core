package net.serenitybdd.screenplay.questions.targets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.SelectedVisibleTextValue;
import net.serenitybdd.screenplay.targets.Target;

@Subject("#target")
public class TargetSelectedVisibleText implements Question<String> {

    private final Target target;

    TargetSelectedVisibleText(Target target) {
        this.target = target;
    }

    @Override
    public String answeredBy(Actor actor) {
        return SelectedVisibleTextValue.of(target).answeredBy(actor);
    }
}
