package serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.targets;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.Attribute;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

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
        return Attribute.of(target).named(name).viewedBy(actor).asString();
    }
}
