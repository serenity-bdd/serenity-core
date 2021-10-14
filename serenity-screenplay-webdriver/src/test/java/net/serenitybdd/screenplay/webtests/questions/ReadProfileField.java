package net.serenitybdd.screenplay.webtests.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.targets.Target;

@Subject("#field")
public class ReadProfileField implements Question<String> {

    private Target field;

    public ReadProfileField(Target field) {
        this.field = field;
    }

    public String answeredBy(Actor actor) {
        return Value.of(field).answeredBy(actor);
    }
}
