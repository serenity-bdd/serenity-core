package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class Link {

    private final static String BY_NAME_OR_ID = "css:a[id='{0}'],a[name='{0}'],a[data-test='{0}'],a[aria-label='{0}']";

    private final static String BY_LINK_TEXT = ".//a[translate(normalize-space(.),\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\",\"abcdefghijklmnopqrstuvwxyz\")=translate(\"{0}\",\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\",\"abcdefghijklmnopqrstuvwxyz\")]";

    private final static String BY_PARTIAL_LINK_TEXT = ".//a[contains(translate(normalize-space(.),\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\",\"abcdefghijklmnopqrstuvwxyz\"),translate(\"{0}\",\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\",\"abcdefghijklmnopqrstuvwxyz\"))]";
    private final static String STARTING_WITH = ".//a[starts-with(translate(normalize-space(.),\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\",\"abcdefghijklmnopqrstuvwxyz\"),translate(\"{0}\",\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\",\"abcdefghijklmnopqrstuvwxyz\"))]";
    private final static String BY_LINK_TITLE = "css:a[title='{0}' i]";
    private final static String BY_ICON = ".//a[./i[contains(@class,'{0}')]]";

    /**
     * Locate a link element with a given name or id.
     */
    public static SearchableTarget withNameOrId(String name) {
        return Target.the("the '" + name + "' link").locatedBy(BY_NAME_OR_ID).of(name);
    }

    /**
     * Look for an element with a given CSS class
     */
    public static SearchableTarget withCSSClass(String className) {
        return TargetFactory.forElementOfType("link").withCSSClass(className);
    }


    /**
     * Locate a link element with a given text.
     */
    public static SearchableTarget withText(String name) {
        return Target.the("the '" + name + "' link").locatedByFirstMatching(BY_LINK_TEXT).of(name);
    }


    /**
     * Locate a link element containing given text.
     */
    public static SearchableTarget containing(String text) {
        return Target.the("'" + text + "' link").locatedByFirstMatching(BY_PARTIAL_LINK_TEXT).of(text);
    }

    /**
     * Locate a link element starting with given text.
     */
    public static SearchableTarget startingWith(String text) {
        return Target.the("'" + text + "' link").locatedByFirstMatching(STARTING_WITH).of(text);
    }

    public static SearchableTarget withTitle(String title) {
        return Target.the("link entitled '" + title + "'").locatedByFirstMatching(BY_LINK_TITLE).of(CSSAttributeValue.withEscapedQuotes(title));
    }

    public static SearchableTarget withIcon(String icon) {
        return Target.the("link with icon '" + icon + "'").locatedByFirstMatching(BY_ICON).of(icon);
    }

    /**
     * Locate an image using an arbitrary CSS or XPath expression
     */
    public static SearchableTarget locatedBy(String selector) {
        return TargetFactory.forElementOfType("link").locatedByXPathOrCss(selector);
    }

    public static SearchableTarget located(By selector) {
        return TargetFactory.forElementOfType("link").locatedBy(selector);
    }


}
