package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.fieldWithLabel;

/**
 * Interact with a label element
 * This can be useful when the input element itself is not accessible
 */
public class Label {

    private static final String FOR_FIELD = "css:[for='{0}']";
    private static final String CONTENTS = "xpath://label[translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=translate('{0}','ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')]";

    /**
     * Locate a label with a given text
     */
    public static SearchableTarget withText(String text) {
        return Target.the("'" + text + "' field").locatedBy(CONTENTS).of(text);
    }

    /**
     * Locate a label for a given field
     */
    public static SearchableTarget forField(String fieldId) {
        return Target.the("'" + fieldId + "' field label").locatedBy(FOR_FIELD).of(fieldId);
    }

    /**
     * Locate a button using an arbitrary CSS or XPath expression
     */
    public static SearchableTarget locatedBy(String selector) {
        return TargetFactory.forElementOfType("label").locatedByXPathOrCss(selector);
    }

    /**
     * Locate a button using an arbitrary CSS or XPath expression
     */
    public static SearchableTarget located(By selector) {
        return TargetFactory.forElementOfType("label").locatedBy(selector);
    }
}
