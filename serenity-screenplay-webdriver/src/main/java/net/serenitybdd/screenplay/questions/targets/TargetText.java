package net.serenitybdd.screenplay.questions.targets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.targets.Target;

@Subject("#target")
public class TargetText implements Question<String> {

    private final Target target;

    TargetText(Target target) {
        this.target = target;
    }

    /**
     * Return text values even if they are off screen
     * By default, Selenium will throw an exception for off-screen elements.
     */
    public TargetTextContent ignoringVisibility() {
        return new TargetTextContent(target);
    }

    @Override
    public String answeredBy(Actor actor) {
        return Text.of(target).viewedBy(actor).asString();
    }
}
