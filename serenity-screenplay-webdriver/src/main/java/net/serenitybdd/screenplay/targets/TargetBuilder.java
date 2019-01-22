package net.serenitybdd.screenplay.targets;

import org.openqa.selenium.By;

import java.util.Optional;

import static java.util.Optional.empty;

public class TargetBuilder<T> {
    private String targetElementName;
    private Optional<IFrame> iFrame=empty();


    public TargetBuilder(String targetElementName) {
        this.targetElementName = targetElementName;
    }

    public TargetBuilder inIFrame(IFrame iFrame) {
        this.iFrame = Optional.ofNullable(iFrame);
        return this;
    }

    public Target locatedBy(String cssOrXPathSelector) {
        return new XPathOrCssTarget(targetElementName, cssOrXPathSelector, iFrame);
    }

    public Target located(By locator) {
        return new ByTarget(targetElementName, locator, iFrame);
    }
    
    // Target support multi located element on Android and iOS both (#1531)
    public Target locatedForAndroid(MobileBy locator) {
        // TO DO
    }
    
    public Target locatedForIOS(MobileBy locator) {
        // TO DO
    }

}
