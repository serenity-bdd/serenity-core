package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.NoSuchElementException;

@Subject("#target")
public class WebElementQuestion implements Question<WebElementState> {

    private final Target target;

    public WebElementQuestion(Target target) {
        this.target = target;
    }

    public static Question<WebElementState> stateOf(Target target) {
        return new WebElementQuestion(target);
    }

    public static Question<WebElementState> valueOf(Target target) {
        return stateOf(target);
    }

    public static Question<WebElementState> the(Target target) {
        return stateOf(target);
    }

    @Override
    public WebElementState answeredBy(Actor actor) {
        try {
            return target.resolveFor(actor);
        } catch (NoSuchElementException unresolvedTarget) {
            return new UnresolvedTargetWebElementState(target.getName());
        }
    }
}
