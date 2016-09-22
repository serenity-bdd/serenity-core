package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;

class TargetResolver extends PageObject {
    TargetResolver(WebDriver driver) {
        super(driver);
    }

    static TargetResolver switchIFrameIfRequired(WebDriver driver, Target target) {
        TargetResolver resolver = new TargetResolver(driver);
        IFrameSwitcher iFrameSwitcher = IFrameSwitcher.getInstance(driver);
        iFrameSwitcher.switchToIFrame(target);
        return resolver;
    }

}
