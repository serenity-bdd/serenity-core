package net.serenitybdd.screenplay.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.pages.Pages;
import net.thucydides.model.util.NameConverter;

public class OpenPageFromClass implements Interaction {

    private String targetPageName;
    private Class<? extends PageObject> targetPageClass;

    public OpenPageFromClass() {}
    public OpenPageFromClass(Class<? extends PageObject> targetPageClass) {
        this.targetPageClass = targetPageClass;
        this.targetPageName = NameConverter.humanize(targetPageClass.getSimpleName());
    }

    public OpenPageFromClassWithParameters withParameters(String... parameters) {
        return new OpenPageFromClassWithParameters(targetPageClass, targetPageName, parameters);
    }

    @Step("{0} opens the #targetPageName")
    public <T extends Actor> void performAs(T theUser) {
        PageObject targetPage = new Pages(BrowseTheWeb.as(theUser).getDriver()).getPage(targetPageClass);
        targetPage.setDriver(BrowseTheWeb.as(theUser).getDriver());
        targetPage.open();
    }
}
