package net.serenitybdd.core.selectors;

import org.openqa.selenium.By;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.util.HashMap;
import java.util.Map;

public class Selectors {

    public static boolean isXPath(String xpathExpression) {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        try {
            xpath.compile(xpathExpression);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public static By xpathOrCssSelector(String xpathOrCssSelector) {
        if (hasPrefix(xpathOrCssSelector)) {
            return SELECTORS.get(prefixOf(xpathOrCssSelector)).apply(xpathOrCssSelector);
        }

        if (isXPath(xpathOrCssSelector)) {
            return By.xpath(xpathOrCssSelector);
        } else {
            return By.cssSelector(xpathOrCssSelector);
        }
    }

    private static boolean hasPrefix(String xpathOrCssSelector) {
        return prefixOf(xpathOrCssSelector) != SelectorType.noSelectorType;
    }

    private static SelectorType prefixOf(String xpathOrCssSelector) {
        for(SelectorType type: SelectorType.values()) {
            if (xpathOrCssSelector.startsWith(type.name() + ":")) {
                return type;
            }
        }
        return SelectorType.noSelectorType;
    }

    private static String withoutPrefix(String xpathOrCssSelector) {
        int selectorStarts = xpathOrCssSelector.indexOf(":") + 1;
        return xpathOrCssSelector.substring(selectorStarts);
    }

    private static Map<SelectorType, SelectorConverter> SELECTORS = new HashMap();
    static {
        SELECTORS.put(SelectorType.css, path -> By.cssSelector(withoutPrefix(path)));

        SELECTORS.put(SelectorType.xpath, path -> By.xpath(withoutPrefix(path)));

        SELECTORS.put(SelectorType.linkText, path -> By.linkText(withoutPrefix(path)));

        SELECTORS.put(SelectorType.className, path -> By.className(withoutPrefix(path)));

        SELECTORS.put(SelectorType.id, path -> By.id(withoutPrefix(path)));

        SELECTORS.put(SelectorType.partialLinkText, path -> By.partialLinkText(withoutPrefix(path)));

        SELECTORS.put(SelectorType.name, path -> By.name(withoutPrefix(path)));

        SELECTORS.put(SelectorType.tagName, path -> By.tagName(withoutPrefix(path)));
    }
}
