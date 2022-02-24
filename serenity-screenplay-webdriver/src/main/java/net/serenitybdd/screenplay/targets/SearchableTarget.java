package net.serenitybdd.screenplay.targets;

import net.serenitybdd.screenplay.ui.LocatorStrategies;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.containingTextAndMatchingCSS;
import static net.serenitybdd.screenplay.ui.LocatorStrategies.containingTextAndMatchingCSSIgnoringCase;

public abstract class SearchableTarget extends Target {
    public SearchableTarget(String targetElementName, Optional<IFrame> iFrame) {
        super(targetElementName, iFrame);
    }

    public SearchableTarget(String targetElementName, Optional<IFrame> iFrame, Optional<Duration> timeout) {
        super(targetElementName, iFrame, timeout);
    }

    public abstract List<String> getCssOrXPathSelectors();

    public SearchableTarget containingText(String text) {
        if (this instanceof HasByLocator) {
            By byLocator = ((HasByLocator) this).getLocator();
            return Target.the(getName() + " located by '" + byLocator + "' and containing text '" + text + "'")
                    .locatedBy(LocatorStrategies.containingTextAndBy(byLocator, text));
        } else {
            return Target.the(getName() + " containing text '" + text + "'")
                    .locatedBy(containingTextAndMatchingCSS(getCssOrXPathSelectors(), text));
        }
    }

    public SearchableTarget containingTextIgnoringCase(String text) {
        if (this instanceof HasByLocator) {
            By byLocator = ((HasByLocator) this).getLocator();
            return Target.the(getName() + " located by '" + byLocator + "' and containing text '" + text + "'")
                    .locatedBy(LocatorStrategies.containingTextAndByIgnoringCase(byLocator, text));
        } else {
            return Target.the(getName() + " containing text '" + text + "'")
                    .locatedBy(containingTextAndMatchingCSSIgnoringCase(getCssOrXPathSelectors(), text));
        }
    }

}
