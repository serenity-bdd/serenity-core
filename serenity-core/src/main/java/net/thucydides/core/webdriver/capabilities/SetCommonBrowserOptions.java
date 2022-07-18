package net.thucydides.core.webdriver.capabilities;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.remote.AbstractDriverOptions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Set browser options defined in AbstractDriverOptions.
 * This includes proxy, acceptInsecureCerts, pageLoadStrategy, strictFileInteractability and unhandledPromptBehaviour
 */
public class SetCommonBrowserOptions {
    private final Map<String, Object> specifiedOptions;

    public SetCommonBrowserOptions(Map<String, Object> specifiedOptions) {
        this.specifiedOptions = specifiedOptions;
    }

    public static SetCommonBrowserOptions from(Map<String, Object> specifiedOptions) {
        return new SetCommonBrowserOptions(specifiedOptions);
    }

    public static List<String> propertyNames() {
        return Arrays.asList("proxy","acceptInsecureCerts","pageLoadStrategy","strictFileInteractability","unhandledPromptBehaviour");
    }

    public void in(AbstractDriverOptions options) {
        for (String optionName : specifiedOptions.keySet()) {
            switch (optionName) {
                case "proxy":
                    options.setProxy((Proxy) specifiedOptions.get("proxy"));
                    break;
                case "acceptInsecureCerts":
                    options.setAcceptInsecureCerts(Boolean.parseBoolean(specifiedOptions.get("acceptInsecureCerts").toString()));
                    break;
                case "pageLoadStrategy":
                    options.setPageLoadStrategy(PageLoadStrategy.fromString(specifiedOptions.get("pageLoadStrategy").toString()));
                    break;
                case "strictFileInteractability":
                    options.setStrictFileInteractability(Boolean.parseBoolean(specifiedOptions.get("strictFileInteractability").toString()));
                    break;
                case "unhandledPromptBehavior":
                    options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.fromString(specifiedOptions.get("unhandledPromptBehavior").toString()));
                    break;
            }
        }
    }
}
