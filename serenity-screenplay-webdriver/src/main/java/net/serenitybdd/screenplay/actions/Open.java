package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Interaction;

public class Open {

    public static OpenPage browserOn(PageObject targetPage) {
        return new OpenPage(targetPage);
    }

    public static Open browserOn() {
        return new Open();
    }

    public static OpenUrl url(String targetUrl) {
        return new OpenUrl(targetUrl);
    }

    public static OpenAt relativeUrl(String targetUrl) {
        return new OpenAt(targetUrl);
    }

    public Interaction the(PageObject targetPage) {
        return new OpenPage(targetPage);
    }

    public Interaction thePageNamed(String pageName) {
        return new OpenPageWithName(pageName);
    }

    public Interaction the(Class<? extends PageObject> targetPageClass) {
        return new OpenPageFromClass(targetPageClass);
    }

}