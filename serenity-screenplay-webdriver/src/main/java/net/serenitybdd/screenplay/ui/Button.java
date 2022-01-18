package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;

/**
 * An HTML element representing a button.
 * This element will match an HTML button element containing the specified text, or a button-styled input field with a matching value, id or data-test attribute.
 */
public class Button {

    private static final String ARIA_LABEL = "input[type='button'][aria-label='{0}' i],input[type='submit'][aria-label='{0}' i],button[aria-label='{0}' i]";

    private static final String[] LOCATORS = {
            "xpath:.//button[translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=translate('{0}','ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')]",
            "css:input[type='submit'][value='{0}' i],input[type='submit'][id='{0}' i],input[type='submit'][data-test='{0}' i],input[type='submit'][name='{0}' i]," +
                    "input[type='button'][value='{0}' i],input[type='button'][id='{0}' i],input[type='button'][data-test='{0}' i],input[type='button'][name='{0}' i]," +
                    ARIA_LABEL + "," +
                    "input[class*='{0}' i]"
    };

    private final static String BY_ICON = ".//button[./i[contains(@class,'{0}')]]";

    /**
     * Locate a button element with a given name.
     */
    public static SearchableTarget called(String name) {
        return Target.the("'" + name + "' button").locatedByFirstMatching(LOCATORS).of(name);
    }

    /**
     * Locate a button using the ARIA label value
     */
    public static SearchableTarget withLabel(String name) {
        return Target.the("'" + name + "' button").locatedByFirstMatching("css:" + ARIA_LABEL).of(name);
    }

    public static Target withIcon(String icon) {
        return Target.the("Button with icon '" + icon + "'").locatedByFirstMatching(BY_ICON).of(icon);
    }
}