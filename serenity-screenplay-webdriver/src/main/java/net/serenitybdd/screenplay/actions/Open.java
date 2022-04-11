package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.PageObject;

/**
 * Open the browser of an actor on a specified page or URL.
 */
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

    public OpenPage the(PageObject targetPage) {
        return new OpenPage(targetPage);
    }

    public OpenPageWithName thePageNamed(String pageName) {
        return new OpenPageWithName(pageName);
    }

    public OpenPageFromClass the(Class<? extends PageObject> targetPageClass) {
        return new OpenPageFromClass(targetPageClass);
    }

}