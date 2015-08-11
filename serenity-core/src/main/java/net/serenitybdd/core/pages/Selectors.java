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
        if (isXPath(xpathOrCssSelector)) {
            return By.xpath(xpathOrCssSelector);
        } else {
            return By.cssSelector(xpathOrCssSelector);
        }
    }
}
