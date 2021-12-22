package net.serenitybdd.screenplay.playwright.interactions;

public class Open {
    public static OpenUrl url(String url) {
        return new OpenUrl(url);
    }
}
