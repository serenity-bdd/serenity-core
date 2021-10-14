package net.serenitybdd.screenplay.questions.targets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.Attribute;
import net.serenitybdd.screenplay.targets.Target;

@Subject("#target")
public class TargetAttribute implements Question<String> {

    private final Target target;
    private final String name;

    TargetAttribute(Target target, String name) {
        this.target = target;
        this.name = name;
    }

    @Override
    public String answeredBy(Actor actor) {
        return Attribute.of(target).named(name).answeredBy(actor);
    }
}
