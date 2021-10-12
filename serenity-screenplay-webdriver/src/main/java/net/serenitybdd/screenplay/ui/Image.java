package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.fieldWithLabel;


/**
 * An HTML IMG element
 */
public class Image {

    private static final String BY_ID_OR_NAME = "css:input[id='{0}' i],input[name='{0}' i],input[data-test='{0}' i]";

    /**
     * Locate a field with a given name or id.
     */
    public static Target called(String name) {
        return Target.the("'" + name + "' button").locatedBy(BY_ID_OR_NAME).of(name);
    }

    /**
     * Locate an HTML input field with a specified placeholder name
     */
    public static Target withAltText(String altText) {
        String altTextAttribute = AttributeValue.withEscapedQuotes(altText);
        return Target.the("'" + altTextAttribute + "' image")
                     .located(By.cssSelector("img[alt='" + altTextAttribute + "']"));
    }
}