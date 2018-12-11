package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MoveMouse {

    public static WithChainableActions to(Target target) {
        return new MoveMouseToTarget(target);
    }

    public static WithChainableActions to(String xpathOrCSSSelector) {
        return new MoveMouseToTarget(Target.the(xpathOrCSSSelector).locatedBy(xpathOrCSSSelector));
    }

    public static WithChainableActions to(WebElement element) {
        return new MoveMouseToWebElement(element);
    }

    public static WithChainableActions to(By... locators) {
        return new MoveMouseToBy(locators);
    }

}