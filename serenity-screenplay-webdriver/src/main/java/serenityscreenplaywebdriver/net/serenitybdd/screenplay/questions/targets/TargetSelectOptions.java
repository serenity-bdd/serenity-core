package serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.targets;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.SelectOptions;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

import java.util.List;

@Subject("#target")
public class TargetSelectOptions implements Question<List<String>> {

    private final Target target;

    TargetSelectOptions(Target target) {
        this.target = target;
    }

    @Override
    public List<String> answeredBy(Actor actor) {
        return SelectOptions.of(target).viewedBy(actor).resolve();
    }
}
