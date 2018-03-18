package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.WebDriver;

class TargetResolver extends PageObject {
    TargetResolver(WebDriver driver) {
        super(driver);
    }

    static TargetResolver switchIFrameIfRequired(Actor theActor, Target target) {
        WebDriver driver = BrowseTheWeb.as(theActor).getDriver();
        TargetResolver resolver = new TargetResolver(driver);
        IFrameSwitcher iFrameSwitcher = IFrameSwitcher.getInstance(driver);
        iFrameSwitcher.switchToIFrame(target);
        return resolver;
    }

}
