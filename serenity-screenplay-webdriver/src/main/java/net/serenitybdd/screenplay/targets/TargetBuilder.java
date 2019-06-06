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
        return new XPathOrCssTarget(targetElementName, cssOrXPathSelector, iFrame, Optional.empty());
    }

    public Target located(By locator) {
        return new ByTarget(targetElementName, locator, iFrame);
    }

    public LocatesCrossPlatform locatedForAndroid(By androidLocator) {
        return new CrossPlatformTargetBuilder(targetElementName, androidLocator, iFrame);
    }

    static class CrossPlatformTargetBuilder implements LocatesCrossPlatform {

        private String targetElementName;
        private By androidLocator;
        private Optional<IFrame> iFrame;

        CrossPlatformTargetBuilder(String targetElementName, By androidLocator, Optional<IFrame> iFrame) {
            this.targetElementName = targetElementName;
            this.androidLocator = androidLocator;
            this.iFrame = iFrame;
        }

        public Target locatedForIOS(By iosLocator) {
            return new ByTarget(this.targetElementName, this.androidLocator, iosLocator, this.iFrame);
        }

    }
}
