package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.screenplay.Performable;

public class Open {
    public static Performable url(String url) {
        return new OpenUrl(url);
    }
}
