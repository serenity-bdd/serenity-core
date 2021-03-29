package serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.targets;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.SelectedVisibleTextValue;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

@Subject("#target")
public class TargetSelectedVisibleText implements Question<String> {

    private final Target target;

    TargetSelectedVisibleText(Target target) {
        this.target = target;
    }

    @Override
    public String answeredBy(Actor actor) {
        return SelectedVisibleTextValue.of(target).viewedBy(actor).asString();
    }
}
