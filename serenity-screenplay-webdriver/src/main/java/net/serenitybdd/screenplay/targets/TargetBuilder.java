package net.serenitybdd.screenplay.targets;

import org.openqa.selenium.By;

public class TargetBuilder<T> {
    private String targetElementName;

    public TargetBuilder(String targetElementName) {
        this.targetElementName = targetElementName;
    }

    public Target locatedBy(String cssOrXPathSelector) {
        return new XPathOrCssTarget(targetElementName, cssOrXPathSelector);
    }

    public Target located(By locator) {
        return new ByTarget(targetElementName, locator);
    }
}
