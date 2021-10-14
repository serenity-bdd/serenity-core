package net.serenitybdd.screenplay.questions.targets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.Attribute;
import net.serenitybdd.screenplay.targets.Target;

import java.util.Collection;

@Subject("#target")
public class TargetAttributes implements Question<Collection<String>> {

    private final Target target;
    private final String name;

    TargetAttributes(Target target, String name) {
        this.target = target;
        this.name = name;
    }

    @Override
    public Collection<String> answeredBy(Actor actor) {
        return Attribute.ofEach(target).named(name).answeredBy(actor);
    }
}
