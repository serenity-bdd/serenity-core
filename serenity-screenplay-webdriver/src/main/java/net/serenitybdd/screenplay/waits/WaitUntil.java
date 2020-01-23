package net.serenitybdd.screenplay.waits;


import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import org.hamcrest.Matcher;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class WaitUntil {

    public static WaitUntilTargetIsReady the(Target target, Matcher<WebElementState> expectedState) {
        return  instrumented(WaitUntilTargetIsReady.class, target, expectedState);
    }

    public static WaitUntilTargetIsReady the(String xpathOrCSSSelector, Matcher<WebElementState> expectedState) {
        return  instrumented(WaitUntilTargetIsReady.class, Target.the(xpathOrCSSSelector).locatedBy(xpathOrCSSSelector), expectedState);
    }

    public static WaitUntilTargetIsReady the(By byLocator, Matcher<WebElementState> expectedState) {
        return  instrumented(WaitUntilTargetIsReady.class, Target.the(byLocator.toString()).located(byLocator), expectedState);
    }

    public static Interaction angularRequestsHaveFinished() {
        return instrumented(WaitUntilAngularIsReady.class);
    }
}