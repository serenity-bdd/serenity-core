package net.serenitybdd.screenplay.questions.targets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.Attribute;
import net.serenitybdd.screenplay.targets.Target;

@Subject("#target")
public class TargetTextContent implements Question<String> {

    private final Target target;

    TargetTextContent(Target target) {
        this.target = target;
    }

    @Override
    public String answeredBy(Actor actor) {
        return Attribute.of(target).named("textContent").viewedBy(actor).asString();
    }
}
