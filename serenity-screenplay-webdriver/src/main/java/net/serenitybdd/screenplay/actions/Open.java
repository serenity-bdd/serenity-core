package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Interaction;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Open {

    public static OpenPage browserOn(PageObject targetPage) {
        return instrumented(OpenPage.class, targetPage);
    }

    public static Open browserOn() {
        return new Open();
    }

    public static OpenUrl url(String targetUrl) {
        return instrumented(OpenUrl.class, targetUrl);
    }

    public static OpenAt relativeUrl(String targetUrl) {
        return instrumented(OpenAt.class, targetUrl);
    }

    public Interaction the(PageObject targetPage) {
        return instrumented(OpenPage.class, targetPage);
    }

    public Interaction thePageNamed(String pageName) {
        return instrumented(OpenPageWithName.class, pageName);
    }

    public Interaction the(Class<? extends PageObject> targetPageClass) {
        return instrumented(OpenPageFromClass.class, targetPageClass);
    }

}