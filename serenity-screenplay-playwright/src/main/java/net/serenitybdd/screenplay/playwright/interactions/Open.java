package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.core.pages.PageObject;

public class Open {
    public static OpenUrl url(String url) {
        return new OpenUrl(url);
    }

    public static OpenPageFromClass page(Class<? extends PageObject> targetPageClass) {
        return new OpenPageFromClass(targetPageClass);
    }
}
