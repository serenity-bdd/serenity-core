package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.PageUrls;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.util.NameConverter;

public class OpenPageFromClass implements Interaction {

    private final String targetPageName;
    private final Class<? extends PageObject> targetPageClass;

    public OpenPageFromClass(Class<? extends PageObject> targetPageClass) {
        this.targetPageClass = targetPageClass;
        this.targetPageName = NameConverter.humanize(targetPageClass.getSimpleName());
    }

    public OpenPageFromClassWithParameters withParameters(String... parameters) {
        return new OpenPageFromClassWithParameters(targetPageClass, targetPageName, parameters);
    }

    @Step("{0} opens the #targetPageName")
    public <T extends Actor> void performAs(T actor) {
        PageObject targetPage = new Pages().getPage(targetPageClass);
        String url = new PageUrls(targetPage).getStartingUrl();
        actor.attemptsTo(Open.url(url));
    }
}
