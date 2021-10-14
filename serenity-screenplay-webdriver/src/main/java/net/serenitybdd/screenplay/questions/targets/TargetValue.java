package net.serenitybdd.screenplay.questions.targets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.targets.Target;

@Subject("#target")
public class TargetValue implements Question<String> {

    private final Target target;

    TargetValue(Target target) {
        this.target = target;
    }

    @Override
    public String answeredBy(Actor actor) {
        return Value.of(target).answeredBy(actor);
    }
}
