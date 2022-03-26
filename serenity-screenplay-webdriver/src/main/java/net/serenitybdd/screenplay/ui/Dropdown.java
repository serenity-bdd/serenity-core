package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.fieldWithLabel;

public class Dropdown {

    private static final String BY_ID_OR_NAME = "css:select[id='{0}' i],select[name='{0}' i],select[data-test='{0}' i],[aria-label='{0}' i]";
    private static final String BY_DEFAULT_OPTION = ".//select[option[1][normalize-space(.)='{0}']]";

    /**
     * Locate a button element with a given id, name or aria-label.
     */
    public static SearchableTarget withNameOrId(String nameOrId) {
        return Target.the("'" + nameOrId + "' dropdown").locatedByFirstMatching(BY_ID_OR_NAME).of(nameOrId);
    }


    public static SearchableTarget withLabel(String labelText) {
        return Target.the("the '" + labelText + "' dropdown").locatedBy(fieldWithLabel(labelText));
    }

    public static SearchableTarget withDefaultOption(String defaultOption) {
        return Target.the("the '" + defaultOption + "' dropdown").locatedBy(BY_DEFAULT_OPTION).of(defaultOption);
    }

    /**
     * Locate an dropdown using an arbitrary CSS or XPath expression
     */
    public static SearchableTarget locatedBy(String selector) {
        return TargetFactory.forElementOfType("dropdown").locatedByXPathOrCss(selector);
    }

    public static SearchableTarget located(By selector) {
        return TargetFactory.forElementOfType("dropdown").locatedBy(selector);
    }


}
