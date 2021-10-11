package net.serenitybdd.screenplay.webtests.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.SelectedVisibleTextValue;
import net.serenitybdd.screenplay.targets.Target;


@Subject("#field")
public class ReadProfileOptionValue implements Question<String> {

    private Target field;


    public ReadProfileOptionValue(Target field) {
        this.field = field;
    }
 
    public String answeredBy(Actor actor) {
        return SelectedVisibleTextValue.of(field).answeredBy(actor);
    }
}
