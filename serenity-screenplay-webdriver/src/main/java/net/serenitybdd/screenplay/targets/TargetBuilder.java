package net.serenitybdd.screenplay.targets;

import org.openqa.selenium.By;

public class TargetBuilder<T> {
    private String targetElementName;
    private IFrame iFrame;


    public TargetBuilder(String targetElementName) {
        this.targetElementName = targetElementName;
    }

    public TargetBuilder inIFrame(IFrame iFrame) {
        this.iFrame = iFrame;
        return this;
    }

    public TargetBuilder inIFrame(String...iframes) {
        this.iFrame = IFrame.withPath(iframes);
        return this;
    }

    public Target locatedBy(String cssOrXPathSelector) {
        return new XPathOrCssTarget(targetElementName, cssOrXPathSelector, iFrame);
    }

    public Target located(By locator) {
        return new ByTarget(targetElementName, locator, iFrame);
    }

}
