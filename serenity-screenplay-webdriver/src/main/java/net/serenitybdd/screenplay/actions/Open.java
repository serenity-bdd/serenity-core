package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.PageObject;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Open {

    public static OpenPage browserOn(PageObject targetPage) {
        return instrumented(OpenPage.class, targetPage);
    }

    public static OpenPage browserOn() {
        return instrumented(OpenPage.class);
    }

    public static OpenUrl url(String targetUrl) {
        return instrumented(OpenUrl.class, targetUrl);
    }


}
