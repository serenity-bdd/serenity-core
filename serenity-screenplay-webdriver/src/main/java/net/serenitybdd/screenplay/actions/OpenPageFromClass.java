package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.util.NameConverter;

public class OpenPageFromClass implements Interaction {

    private String targetPageName;

    private final Class<PageObject> targetPageClass;

    public OpenPageFromClass(Class<PageObject> targetPageClass) {
        this.targetPageClass = targetPageClass;
        this.targetPageName = NameConverter.humanize(targetPageClass.getSimpleName());
    }

    @Step("{0} opens the #targetPageName")
    public <T extends Actor> void performAs(T theUser) {
        PageObject targetPage = new Pages(BrowseTheWeb.as(theUser).getDriver()).getPage(targetPageClass);
        targetPage.setDriver(BrowseTheWeb.as(theUser).getDriver());
        targetPage.open();
    }

}
