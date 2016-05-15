package net.serenitybdd.screenplay.questions.targets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.SelectedOptions;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;

@Subject("#target")
public class TargetSelectedOptions implements Question<List<String>> {

    private final Target target;

    TargetSelectedOptions(Target target) {
        this.target = target;
    }

    @Override
    public List<String> answeredBy(Actor actor) {
        return SelectedOptions.of(target).viewedBy(actor).resolve();
    }
}
