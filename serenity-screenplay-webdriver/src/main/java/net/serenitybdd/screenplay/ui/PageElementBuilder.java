package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;

import static net.serenitybdd.core.pages.RenderedPageObjectView.containingTextAndMatchingCSS;

public class PageElementBuilder {

    private final String selector;

    PageElementBuilder(String selector) {
        this.selector = selector;
    }

    public Target describedAs(String name) {
        return Target.the(name).locatedBy(selector);
    }

    public Target containingText(String text) {
        return Target.the("the element containing text '" + text + "'").locatedBy(containingTextAndMatchingCSS(selector, text));
    }
}