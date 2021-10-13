package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;

/**
 * An HTML element representing a button.
 */
public class Button {

    private static final String[] LOCATORS = {
            "xpath:.//button[translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=translate('{0}','ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')]",
            "css:input[type='submit'][value='{0}' i],input[type='submit'][id='{0}'],input[type='submit'][data-test='{0}'],input.{0}"
    };

    /**
     * Locate a button element with a given name.
     */
    public static SearchableTarget called(String name) {
        return Target.the("'" + name + "' button").locatedByFirstMatching(LOCATORS).of(name);
    }
}