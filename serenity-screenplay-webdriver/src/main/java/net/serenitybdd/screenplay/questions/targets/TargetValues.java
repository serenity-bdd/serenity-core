package net.serenitybdd.screenplay.questions.targets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.targets.Target;

import java.util.Collection;

@Subject("#target")
public class TargetValues implements Question<Collection<String>> {

    private final Target target;

    TargetValues(Target target) {
        this.target = target;
    }

    @Override
    public Collection<String> answeredBy(Actor actor) {
        return Value.ofEach(target).answeredBy(actor);
    }
}
