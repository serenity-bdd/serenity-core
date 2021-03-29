package serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.targets;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.Attribute;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

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
