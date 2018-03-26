package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Actor;
import org.openqa.selenium.WebDriver;

class TargetResolver extends PageObject {

    private TargetResolver(WebDriver driver, Target target) {
        super(driver);
        IFrameSwitcher.getInstance().switchToIFrame(driver, target);
    }

    static TargetResolver create(WebDriver driver, Target target) {
        return new TargetResolver(driver, target);
    }

}
