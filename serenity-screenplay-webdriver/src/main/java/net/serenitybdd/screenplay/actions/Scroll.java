package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Scroll {

    public static ScrollToTarget to(String cssOrXpathForElement) {
        return instrumented(ScrollToTarget.class, Target.the(cssOrXpathForElement).locatedBy(cssOrXpathForElement));
    }

    public static ScrollToTarget to(Target target) {
       return new ScrollToTarget(target);
   }

    public static ScrollToWebElement to(WebElement element) {
        return new ScrollToWebElement(element);
    }

    public static ScrollToBy to(By... locators) {
        return new ScrollToBy(locators);
    }

}
