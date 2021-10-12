package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.fieldWithLabel;


/**
 * An HTML INPUT field
 */
public class InputField {

    private static final String BY_ID_OR_NAME = "css:input[id='{0}' i],input[name='{0}' i],input[data-test='{0}' i],textarea[id='{0}' i],textarea[name='{0}' i],textarea[data-test='{0}' i]";

    /**
     * Locate a field with a given name or id.
     */
    public static Target called(String name) {
        return Target.the("'" + name + "' button").locatedBy(BY_ID_OR_NAME).of(name);
    }

    /**
     * Locate an HTML input field with a specified placeholder name
     */
    public static Target withPlaceholder(String placeholderName) {
        String placeholderAttribute = AttributeValue.withEscapedQuotes(placeholderName);
        return Target.the("'" + placeholderName + "' field")
                     .located(By.cssSelector("[placeholder='" + placeholderAttribute + "']"));
    }

    public static Target withLabel(String labelText) {
        return Target.the(labelText + " field").locatedBy(fieldWithLabel(labelText));
    }


}