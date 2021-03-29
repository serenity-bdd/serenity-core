package serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.targets;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.CSSValue;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

import java.util.List;

@Subject("#target")
public class TargetCSSValues implements Question<List<String>> {

    private final Target target;
    private final String name;

    TargetCSSValues(Target target, String name) {
        this.target = target;
        this.name = name;
    }

    @Override
    public List<String> answeredBy(Actor actor) {
        return CSSValue.of(target).named(name) .viewedBy(actor).asList();
    }
}
