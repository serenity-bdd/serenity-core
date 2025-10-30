package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Click on a button or element.
 */
public class ClickAndHold {

    public static Performable on(String cssOrXpathForElement) {
        return new ClickAndHoldCSSOrXPath(cssOrXpathForElement);
    }

    public static Performable on(By locator) {
        return new ClickAndHoldBy(locator);
    }

    public static Performable on(Target target) {
        return new ClickAndHoldTarget(target);
    }

    public static Performable on(WebElementFacade element) {
        return new ClickAndHoldElement(element);
    }

    public static class ClickAndHoldElement extends ChainablePerformable {
        private final WebElement element;

        public ClickAndHoldElement(WebElement element) {
            this.element = element;
        }

        public <T extends Actor> void performAs(T actor) {
            addActionAtStart(actions -> actions.clickAndHold(element));
            performSubsequentActionsAs(actor);
        }
    }

    public static class ClickAndHoldTarget extends ChainablePerformable {
        private final Target target;

        public ClickAndHoldTarget(Target target) {
            this.target = target;
        }

        public <T extends Actor> void performAs(T actor) {
            WebElement element = target.resolveFor(actor);
            addActionAtStart(actions -> actions.clickAndHold(element));
            performSubsequentActionsAs(actor);
        }
    }

    public static class ClickAndHoldCSSOrXPath extends ChainablePerformable {
        private final String cssOrXPath;

        public ClickAndHoldCSSOrXPath(String cssOrXPath) {
            this.cssOrXPath = cssOrXPath;
        }

        public <T extends Actor> void performAs(T actor) {
            WebElement element = BrowseTheWeb.as(actor).find(cssOrXPath);
            addActionAtStart(actions -> actions.clickAndHold(element));
            performSubsequentActionsAs(actor);
        }
    }


    public static class ClickAndHoldBy extends ChainablePerformable {
        private final List<By> locators;

        public ClickAndHoldBy(By... locators) {
            this.locators = NewList.copyOf(locators);
        }

        public <T extends Actor> void performAs(T actor) {
            WebElement element = WebElementLocator.forLocators(locators).andActor(actor);
            addActionAtStart(actions -> actions.clickAndHold(element));
            performSubsequentActionsAs(actor);
        }
    }
}
