package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;

import static net.serenitybdd.core.pages.RenderedPageObjectView.containingTextAndMatchingCSS;

public class PageElementBuilder {

    private final String selector;

    PageElementBuilder(String selector) {
        this.selector = selector;
    }

    public SearchableTarget describedAs(String name) {
        return Target.the(name).locatedBy(selector);
    }

    public SearchableTarget containingText(String text) {
        return Target.the("the element containing text '" + text + "'").locatedBy(containingTextAndMatchingCSS(selector, text));
    }
}