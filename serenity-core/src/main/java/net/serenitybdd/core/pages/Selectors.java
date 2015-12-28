package net.serenitybdd.core.pages;

import org.openqa.selenium.By;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

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
        if (hasCssPrefix(xpathOrCssSelector))
            return By.cssSelector(withoutPrefix(xpathOrCssSelector));

        if (hasXPathPrefix(xpathOrCssSelector))
            return By.xpath(withoutPrefix(xpathOrCssSelector));

        if (isXPath(xpathOrCssSelector)) {
            return By.xpath(xpathOrCssSelector);
        } else {
            return By.cssSelector(xpathOrCssSelector);
        }
    }

    private static String withoutPrefix(String xpathOrCssSelector) {
        int selectorStarts = xpathOrCssSelector.indexOf(":") + 1;
        return xpathOrCssSelector.substring(selectorStarts);
    }

    private static boolean hasCssPrefix(String xpathOrCssSelector) {
        return xpathOrCssSelector.startsWith("css:");
    }

    private static boolean hasXPathPrefix(String xpathOrCssSelector) {
        return xpathOrCssSelector.startsWith("xpath:");
    }
}
