package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;


/**
 * An HTML IMG element
 */
public class Image {

    private static final String BY_ID_OR_NAME = "css:img[id='{0}' i],img[name='{0}' i],img[data-test='{0}' i],img[class*='{0}' i]";

    /**
     * Locate a field with a given name or id.
     */
    public static Target called(String name) {
        return Target.the("'the " + name + "' image").locatedBy(BY_ID_OR_NAME).of(name);
    }

    /**
     * Locate a image with a specified alt text
     */
    public static Target withAltText(String altText) {
        String altTextAttribute = CSSAttributeValue.withEscapedQuotes(altText);
        return Target.the("'" + altTextAttribute + "' image")
                     .located(By.cssSelector("img[alt='" + altTextAttribute + "']"));
    }

    /**
     * Locate a image with a specified src attribute
     */
    public static Target withSrc(String srcValue) {
        String srcAttribute = CSSAttributeValue.withEscapedQuotes(srcValue);
        return Target.the("'" + srcValue + "' image")
                .located(By.cssSelector("img[src='" + srcAttribute + "']"));
    }

    public static Target withSrcEndingWith(String srcValue) {
        String srcAttribute = CSSAttributeValue.withEscapedQuotes(srcValue);
        return Target.the("'" + srcValue + "' image")
                .located(By.cssSelector("img[src$='" + srcAttribute + "']"));
    }

    public static Target withSrcStartingWith(String srcValue) {
        String srcAttribute = CSSAttributeValue.withEscapedQuotes(srcValue);
        return Target.the("'" + srcValue + "' image")
                .located(By.cssSelector("img[src^='" + srcAttribute + "']"));
    }
}