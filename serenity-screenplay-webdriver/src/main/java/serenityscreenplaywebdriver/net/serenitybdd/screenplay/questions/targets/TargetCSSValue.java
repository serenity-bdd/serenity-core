package serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.targets;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.CSSValue;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

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
        return CSSValue.of(target).named(name) .viewedBy(actor).asString();
    }
}
