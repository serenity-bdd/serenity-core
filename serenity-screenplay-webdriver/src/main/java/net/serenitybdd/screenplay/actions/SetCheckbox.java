package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.WebElement;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Check or uncheck an HTML checkbox.
 */
public class SetCheckbox {
    public static SetCheckboxInteraction of(Target target) { return new SetCheckboxInteractionForTarget(target); }

    public static SetCheckboxInteraction of(String cssOrXpathForElement) {
        return new SetCheckboxInteractionForTarget(Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public static SetCheckboxInteraction of(WebElementFacade element) {
        return new SetCheckboxInteractionForElement(element);
    }

    public static class SetCheckboxInteractionForTarget implements SetCheckboxInteraction {
        private final Target target;

        public SetCheckboxInteractionForTarget(Target target) {
            this.target = target;
        }

        public ClickInteraction toTrue() {
            return instrumented(CheckCheckboxOfTarget.class, target, true);
        }

        public ClickInteraction toFalse() {
            return instrumented(CheckCheckboxOfTarget.class, target, false);
        }
    }

    public static class SetCheckboxInteractionForElement implements SetCheckboxInteraction {
        private final WebElement element;

        public SetCheckboxInteractionForElement(WebElement element) {
            this.element = element;
        }

        public ClickInteraction toTrue() {
            return instrumented(CheckCheckboxOfElement.class, element, true);
        }

        public ClickInteraction toFalse() {
            return instrumented(CheckCheckboxOfElement.class, element, false);
        }
    }
}
