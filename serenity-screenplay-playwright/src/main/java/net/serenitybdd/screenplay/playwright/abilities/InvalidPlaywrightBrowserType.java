package net.serenitybdd.screenplay.playwright.abilities;

public class InvalidPlaywrightBrowserType extends RuntimeException {
    public InvalidPlaywrightBrowserType(String browserType) {
        super("Invalid Playwright browser type: " + browserType + "; Must be one of chromium, webkit or firefox");
    }
}
