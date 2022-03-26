package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

class TargetFactory {
    private final String elementType;

    private static final String STRICTLY_CONTAINS_TEXT = "xpath:.//*[contains(normalize-space(text()),'{0}')]";

    TargetFactory(String elementType) {
        this.elementType = elementType;
    }

    static TargetFactory forElementOfType(String elementType) {
        return new TargetFactory(elementType);
    }

    public SearchableTarget containingText(String text) {
        return Target.the("the element containing text '" + text + "'").locatedBy(STRICTLY_CONTAINS_TEXT).of(text);
    }

    SearchableTarget withCSSClass(String className) {
        return Target.the("the '" + className + "' " + elementType).located(By.className(className));
    }

    public SearchableTarget locatedByXPathOrCss(String xpathOrCSSSelector) {
        return Target.the("the " + elementType).locatedBy(xpathOrCSSSelector);
    }

    public SearchableTarget locatedBy(By locator) {
        return Target.the("the " + elementType).located(locator);
    }
}
