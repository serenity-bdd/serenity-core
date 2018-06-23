package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.matchers.statematchers.CheckForAbsenceHint;
import net.serenitybdd.screenplay.matchers.statematchers.MissingWebElement;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

@Subject("#target")
public class WebElementQuestion implements Question<WebElementState>, AcceptsHints {

    private final Target target;
    private boolean checkForAbsence = false;

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
        return (checkForAbsence) ? checkForAbsenceBy(actor) : checkForPresenceBy(actor);
    }

    private WebElementState checkForAbsenceBy(Actor actor) {
        List<WebElementFacade> matchingElements = target.resolveAllFor(actor);

        if (matchingElements.isEmpty()) { return new MissingWebElement(target.getName()); }

        return matchingElements.get(0);
    }

    private WebElementState checkForPresenceBy(Actor actor) {
        try {
            return target.resolveFor(actor);
        } catch (NoSuchElementException unresolvedTarget) {
            return new UnresolvedTargetWebElementState(target.getName());
        }
    }

    @Override
    public void apply(Set<Class<? extends QuestionHint>> hints) {
        checkForAbsence = hints.contains(CheckForAbsenceHint.class);
    }
}
