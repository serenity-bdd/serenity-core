package net.serenitybdd.screenplay.webtests.questions;

import net.serenitybdd.core.targets.Target;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.WebQuestion;

@Subject("#field")
public class ReadProfileOptionValue extends WebQuestion implements Question<String> {

    private Target field;


    public ReadProfileOptionValue(Target field) {
        this.field = field;
    }

    public String answeredBy(Actor actor) {
        return $(field).getSelectedVisibleTextValue();
    }
}
