package net.serenitybdd.screenplay.ui;

import net.serenitybdd.core.i8n.LocalisedLabels;
import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.fieldWithLabel;

/**
 * An HTML element representing a button.
 * This element will match an HTML button element containing the specified text, or a button-styled input field with a matching value, id or data-test attribute.
 */
public class Button {

    private static final String ARIA_LABEL = "input[type='button'][aria-label='{0}' i],input[type='submit'][aria-label='{0}' i],button[aria-label='{0}' i]";
    private static final String BUTTON_WITH_TEXT = ".//button[contains(normalize-space(.),'{0}')]";

    private static final String[] LOCATORS = {
            "xpath:.//button[translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=translate('{0}','ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')]",
            "css:input[type='submit'][value='{0}' i],input[type='submit'][id='{0}' i],input[type='submit'][data-test='{0}' i],input[type='submit'][name='{0}' i]," +
                    "input[type='button'][value='{0}' i],input[type='button'][id='{0}' i],input[type='button'][data-test='{0}' i],input[type='button'][name='{0}' i]," +
                    ARIA_LABEL + "," +
                    "input[class*='{0}' i]"
    };

    private static final String WITH_NAME_OR_ID = "css:input[type='submit'][type='button'][id='{0}' i],input[type='submit'],"
                                                  + "[type='button'][name='{0}' i],input[type='submit'][type='button'][data-test='{0}' i],input[type='submit'][type='button'][aria-label='{0}' i],"
                                                  + "button[id='{0}' i],button[name='{0}' i],button[data-test='{0}' i],button[aria-label='{0}' i]";

    private static final String[] WITH_TEXT = {
            "xpath:.//button[translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=translate('{0}','ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')]",
            "css:input[type='submit'][value='{0}' i],input[type='button'][value='{0}' i]"
    };

    private final static String BY_ICON = ".//button[./i[contains(@class,'{0}')]]";

    /**
     * Locate a button element with a given text.
     */
    public static SearchableTarget withText(String name) {
        return Target.the("'" + name + "' button").locatedByFirstMatching(WITH_TEXT).of(name);
    }
    /**
     * Locate a button element with a given id, name or aria-label.
     */
    public static SearchableTarget withNameOrId(String nameOrId) {
        return Target.the("'" + nameOrId + "' button").locatedByFirstMatching(WITH_NAME_OR_ID).of(nameOrId);
    }

    /**
     * Locate a button using an internationalised element.
     * Internationalised elements are defined in the serenity.conf file in the i8n section, e.g.
     * <p>
     * locale = fr
     * i8n {
     * "Search" {
     * en: "Search"
     * fr: "Recherche"
     * }
     * "Sign Up" {
     * en: "Sign Up"
     * fr: "Se connecter"
     * }
     * }
     */
    public static SearchableTarget withLocalisedLabelFor(String name) {
        String localisedName = LocalisedLabels.forCurrentLocale().getLabelFor(name);
        return Target.the("'" + localisedName + "' button").locatedByFirstMatching(LOCATORS).of(localisedName);
    }

    /**
     * Locate a button using an HTML Label value
     */
    public static SearchableTarget withLabel(String labelText) {
        return Target.the(labelText + " field").locatedBy(fieldWithLabel(labelText));
    }

    /**
     * Locate a button using the ARIA label value
     */
    public static SearchableTarget withAriaLabel(String name) {
        return Target.the("'" + name + "' button").locatedByFirstMatching("css:" + ARIA_LABEL).of(name);
    }

    /**
     * A button containing an icon
     */
    public static SearchableTarget withIcon(String icon) {
        return Target.the("Button with icon '" + icon + "'").locatedByFirstMatching(BY_ICON).of(icon);
    }

    /**
     * Look for an element with a given CSS class
     */
    public static SearchableTarget withCSSClass(String className) {
        return TargetFactory.forElementOfType("element").withCSSClass(className);
    }

    /**
     * Locate an element that contains a specified text value in its body.
     * This will not include text that is contained in nested elements.
     */
    public static SearchableTarget containingText(String text) {
        return Target.the("button containing '" + text + "'").locatedByFirstMatching(BUTTON_WITH_TEXT).of(text);
    }

    /**
     * Locate a button using an arbitrary CSS or XPath expression
     */
    public static SearchableTarget locatedBy(String selector) {
        return TargetFactory.forElementOfType("button").locatedByXPathOrCss(selector);
    }

    public static SearchableTarget located(By selector) {
        return TargetFactory.forElementOfType("button").locatedBy(selector);
    }
}