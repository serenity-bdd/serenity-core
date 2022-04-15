package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.HumanReadableTaskName;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.matchers.statematchers.CheckForAbsenceHint;
import net.serenitybdd.screenplay.matchers.statematchers.MissingWebElement;
import net.serenitybdd.screenplay.targets.HasByLocator;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.ui.LocatorStrategies;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

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

    public static Question<WebElementState> stateOf(String xpathOrCssSelector) {
        return new WebElementQuestion(Target.the(xpathOrCssSelector).locatedBy(xpathOrCssSelector));
    }

    public static Question<WebElementState> stateOf(By byLocator) {
        return new WebElementQuestion(Target.the(byLocator.toString()).located(byLocator));
    }

    public static Question<WebElementState> valueOf(Target target) {
        return stateOf(target);
    }

    public static Question<WebElementState> valueOf(String xpathOrCssSelector) {
        return stateOf(xpathOrCssSelector);
    }

    public static Question<WebElementState> valueOf(By byLocator) {
        return stateOf(byLocator);
    }

    public static Question<WebElementState> the(Target target) {
        return stateOf(target);
    }

    public static Question<WebElementState> the(String xpathOrCssSelector) {
        return stateOf(xpathOrCssSelector);
    }

    public static Question<WebElementState> the(By byLocator) {
        return stateOf(byLocator);
    }

    @Override
    public WebElementState answeredBy(Actor actor) {
        return (checkForAbsence) ? checkForAbsenceBy(actor) : checkForPresenceBy(actor);
    }

    private WebElementState checkForAbsenceBy(Actor actor) {
        List<WebElementFacade> matchingElements = target.resolveAllFor(actor);

        if (matchingElements.isEmpty()) {
            return new MissingWebElement(target.getName());
        }

        return matchingElements.get(0);
    }

    private WebElementState checkForPresenceBy(Actor actor) {
        List<WebElementFacade> matchingElements = target.resolveAllFor(actor);
        //if nothing is found, create a UnresolvedTargetWebElementState but try to give him the selector of the target
        if (matchingElements.isEmpty()) {
            UnresolvedTargetWebElementState unresolvedTarget = new UnresolvedTargetWebElementState(target.getName());
            try {
                unresolvedTarget.setSelector(target.getCssOrXPathSelector());
            } catch (Exception e) {
                try {
                    if (target instanceof HasByLocator)
                        unresolvedTarget.setSelector(((HasByLocator) target).getLocator().toString());
                    else {
                        unresolvedTarget.setSelector(target.selectors(BrowseTheWeb.as(actor).getDriver()).toString());
                    }
                } catch (Exception ignored) {
                }
            }
            return unresolvedTarget;
        } else {
            return matchingElements.get(0);
        }
//
//        try {
//            return target.resolveFor(actor);
//        } catch (NoSuchElementException unresolvedTarget) {
//            return new UnresolvedTargetWebElementState(target.getName());
//        } catch (SerenityManagedException targetProbablyNotFound) {
//            if (targetProbablyNotFound.getMessage().contains("No element was found")) {
//                return new UnresolvedTargetWebElementState(target.getName());
//            } else {
//                throw targetProbablyNotFound;
//            }
//        }
    }

    @Override
    public void apply(Set<Class<? extends QuestionHint>> hints) {
        checkForAbsence = hints.contains(CheckForAbsenceHint.class);
    }
}
