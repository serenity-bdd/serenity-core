package net.thucydides.core.pages;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

/**
 * Created by john on 30/01/15.
 */
public class WebElementDescriber {

    public String webElementDescription(WebElement webElement, ElementLocator locator) {

        StringBuffer description = new StringBuffer();
        description.append("<")
                .append(webElement.getTagName());

        boolean descriptiveFieldFound = false;
        if (StringUtils.isNotEmpty(webElement.getAttribute("id"))) {
            description.append(attributeValue(webElement, "id"));
            descriptiveFieldFound = true;
        }
        if (StringUtils.isNotEmpty(webElement.getAttribute("name"))) {
            description.append(attributeValue(webElement, "name"));
            descriptiveFieldFound = true;
        }
        if (!descriptiveFieldFound && StringUtils.isNotEmpty(webElement.getAttribute("href"))) {
            description.append(attributeValue(webElement, "href"));
            descriptiveFieldFound = true;
        }
        if (StringUtils.isNotEmpty(webElement.getAttribute("type"))) {
            description.append(attributeValue(webElement, "type"));
            descriptiveFieldFound = true;
        }
        if (StringUtils.isNotEmpty(webElement.getAttribute("value"))) {
            description.append(attributeValue(webElement, "value"));
            descriptiveFieldFound = true;
        }
        if (!descriptiveFieldFound && StringUtils.isNotEmpty(webElement.getAttribute("class"))) {
            description.append(attributeValue(webElement, "class"));
        }
        description.append(">");
        description.append(" - ").append(locator.toString());

        return description.toString();
    }

    private String attributeValue(WebElement webElement, String attribute) {
        return " " + attribute + "='" + webElement.getAttribute(attribute) + "'";
    }

}
