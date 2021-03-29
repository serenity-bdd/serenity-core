package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.questions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.Value;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

@Subject("#field")
public class ReadProfileField implements Question<String> {

    private Target field;

    public ReadProfileField(Target field) {
        this.field = field;
    }

    public String answeredBy(Actor actor) {
        return Value.of(field).viewedBy(actor).value();
    }
}
