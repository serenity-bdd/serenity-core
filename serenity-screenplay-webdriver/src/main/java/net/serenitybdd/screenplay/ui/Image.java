package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;


/**
 * An HTML IMG element
 */
public class Image {

    /**
     * Locate a image with a specified alt text
     */
    public static SearchableTarget withAltText(String altText) {
        String altTextAttribute = CSSAttributeValue.withEscapedQuotes(altText);
        return Target.the("'" + altTextAttribute + "' image")
                .located(By.cssSelector("img[alt='" + altTextAttribute + "']"));
    }

    /**
     * Locate a image with a specified src attribute
     */
    public static SearchableTarget withSrc(String srcValue) {
        String srcAttribute = CSSAttributeValue.withEscapedQuotes(srcValue);
        return Target.the("'" + srcValue + "' image")
                .located(By.cssSelector("img[src='" + srcAttribute + "']"));
    }

    public static SearchableTarget withSrcEndingWith(String srcValue) {
        String srcAttribute = CSSAttributeValue.withEscapedQuotes(srcValue);
        return Target.the("'" + srcValue + "' image")
                .located(By.cssSelector("img[src$='" + srcAttribute + "']"));
    }

    public static SearchableTarget withSrcStartingWith(String srcValue) {
        String srcAttribute = CSSAttributeValue.withEscapedQuotes(srcValue);
        return Target.the("'" + srcValue + "' image")
                .located(By.cssSelector("img[src^='" + srcAttribute + "']"));
    }

    /**
     * Locate an image using an arbitrary CSS or XPath expression
     */
    public static SearchableTarget locatedBy(String selector) {
        return TargetFactory.forElementOfType("image").locatedByXPathOrCss(selector);
    }

    public static SearchableTarget located(By selector) {
        return TargetFactory.forElementOfType("image").locatedBy(selector);
    }

}