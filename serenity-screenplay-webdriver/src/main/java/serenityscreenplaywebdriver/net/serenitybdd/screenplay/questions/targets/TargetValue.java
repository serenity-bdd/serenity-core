package serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.targets;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.Value;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

@Subject("#target")
public class TargetValue implements Question<String> {

    private final Target target;

    TargetValue(Target target) {
        this.target = target;
    }

    @Override
    public String answeredBy(Actor actor) {
        return Value.of(target).viewedBy(actor).asString();
    }
}
