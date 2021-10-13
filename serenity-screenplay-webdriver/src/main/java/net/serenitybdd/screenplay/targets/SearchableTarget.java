package net.serenitybdd.screenplay.targets;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static net.serenitybdd.core.pages.RenderedPageObjectView.containingTextAndMatchingCSS;

public abstract class SearchableTarget extends Target {
    public SearchableTarget(String targetElementName, Optional<IFrame> iFrame) {
        super(targetElementName, iFrame);
    }

    public SearchableTarget(String targetElementName, Optional<IFrame> iFrame, Optional<Duration> timeout) {
        super(targetElementName, iFrame, timeout);
    }

    public abstract List<String> getCssOrXPathSelectors();

    public Target containingText(String text) {
        return Target.the(getName() + " containing text '" + text + "'")
                .locatedBy(containingTextAndMatchingCSS(getCssOrXPathSelectors(), text));
    }
}
