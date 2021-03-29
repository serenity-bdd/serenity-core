package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.questions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.questions.SelectedVisibleTextValue;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.targets.Target;

@Subject("#field")
public class ReadProfileOptionValue implements Question<String> {

    private Target field;


    public ReadProfileOptionValue(Target field) {
        this.field = field;
    }
 
    public String answeredBy(Actor actor) {
        return SelectedVisibleTextValue.of(field).viewedBy(actor).value();
    }
}
