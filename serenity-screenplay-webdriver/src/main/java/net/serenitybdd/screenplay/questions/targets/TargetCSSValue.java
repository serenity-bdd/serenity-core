package net.serenitybdd.screenplay.questions.targets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.CSSValue;
import net.serenitybdd.screenplay.targets.Target;

@Subject("#target")
public class TargetCSSValue implements Question<String> {

    private final Target target;
    private final String name;

    TargetCSSValue(Target target, String name) {
        this.target = target;
        this.name = name;
    }

    @Override
    public String answeredBy(Actor actor) {
        return CSSValue.of(target).named(name).answeredBy(actor);
    }
}
