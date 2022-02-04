package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.containingTextAndMatchingCSS;

public class PageElementBuilder {

    private final String xpathOrCssSelector;

    PageElementBuilder(String selector) {
        this.xpathOrCssSelector = selector;
    }

    public SearchableTarget describedAs(String name) {
        return Target.the(name).locatedBy(xpathOrCssSelector);
    }

    public SearchableTarget containingText(String text) {
        return Target.the("the element containing text '" + text + "'").locatedBy(containingTextAndMatchingCSS(xpathOrCssSelector, text));
    }
}