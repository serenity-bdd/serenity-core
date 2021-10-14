package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;


/**
 * An HTML IMG element
 */
public class Image {

    private static final String BY_ID_OR_NAME = "css:img[id='{0}' i],img[name='{0}' i],img[data-test='{0}' img.{0}]";

    /**
     * Locate a field with a given name or id.
     */
    public static Target called(String name) {
        return Target.the("'the " + name + "' image").locatedBy(BY_ID_OR_NAME).of(name);
    }

    /**
     * Locate an HTML input field with a specified placeholder name
     */
    public static Target withAltText(String altText) {
        String altTextAttribute = CSSAttributeValue.withEscapedQuotes(altText);
        return Target.the("'" + altTextAttribute + "' image")
                     .located(By.cssSelector("img[alt='" + altTextAttribute + "']"));
    }
}