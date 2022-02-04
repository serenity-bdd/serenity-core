package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

    public SearchableTarget locatedBy(String cssOrXPathSelector) {
        return new XPathOrCssTarget(targetElementName, cssOrXPathSelector, iFrame, Optional.empty());
    }

    /**
     * Locate an element using a location strategy function.
     * The function takes a Serenity Page Object representing the current web page, and returns a list of matching WebElementFacade objects.
     */
    public SearchableTarget locatedBy(Function<SearchContext, List<WebElementFacade>> locationStrategy) {
        return new LambdaTarget(targetElementName, locationStrategy, iFrame, Optional.empty());
    }

    public SearchableTarget locatedByFirstMatching(String... cssOrXPathSelectors) {
        return new MultiXPathOrCssTarget(targetElementName, iFrame, Optional.empty(), cssOrXPathSelectors);
    }

    public SearchableTarget located(By locator) {
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
            return new ByMobileTarget(this.targetElementName, this.androidLocator, iosLocator, this.iFrame);
        }
    }
}
