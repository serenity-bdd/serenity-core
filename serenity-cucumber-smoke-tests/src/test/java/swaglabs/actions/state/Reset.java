package swaglabs.actions.state;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.InTheBrowser;

public class Reset {
    public static Performable applicationState() {

        return InTheBrowser.perform(
                browser -> {
                    browser.evaluateJavascript("window.localStorage.clear()");
                    browser.getDriver().navigate().refresh();
                }
        ).withNoReporting();
    }
}
