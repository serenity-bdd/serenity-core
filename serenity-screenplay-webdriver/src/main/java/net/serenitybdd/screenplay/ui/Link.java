package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;

public class Link {

    private final static String BY_LINK_TEXT = ".//a[translate(normalize-space(.),\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\",\"abcdefghijklmnopqrstuvwxyz\")=translate(\"{0}\",\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\",\"abcdefghijklmnopqrstuvwxyz\")]";
    private final static String BY_PARTIAL_LINK_TEXT = ".//a[contains(translate(normalize-space(.),\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\",\"abcdefghijklmnopqrstuvwxyz\"),translate(\"{0}\",\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\",\"abcdefghijklmnopqrstuvwxyz\"))]";
    private final static String BY_LINK_TITLE = "css:a[title='{0}' i]";

    /**
     * Locate a link element with a given name.
     */
    public static Target called(String name) {
        return Target.the("the '" + name + "' link").locatedByFirstMatching(BY_LINK_TEXT).of(name);
    }

    /**
     * Locate a link element containing given text.
     */
    public static Target containing(String text) {
        return Target.the("'" + text + "' link").locatedByFirstMatching(BY_PARTIAL_LINK_TEXT).of(text);
    }
    public static Target withTitle(String title) {
        return Target.the("link entitled '" + title + "'").locatedByFirstMatching(BY_LINK_TITLE).of(CSSAttributeValue.withEscapedQuotes(title));
    }
}
