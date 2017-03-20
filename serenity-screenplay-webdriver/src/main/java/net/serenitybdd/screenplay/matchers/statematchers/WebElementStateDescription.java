package net.serenitybdd.screenplay.matchers.statematchers;

import net.serenitybdd.core.pages.WebElementState;

public class WebElementStateDescription {

    public static String forElement(WebElementState item) {
        return (item.isCurrentlyVisible())
                ?  "the element identified by " + selectorFor(item) + " [" + item.getTextValue() + "]"
                : "no matching element found by " + selectorFor(item);

    }

    private static String selectorFor(WebElementState item) {
        String fullSelector = item.toString();
        fullSelector = fullSelector.replace("By.","");
        fullSelector = fullSelector.replace("Selector:",":");
        return fullSelector;
    }
}

