package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.annotations.Step;
import net.thucydides.core.pages.Pages;

import java.util.Arrays;

import static net.serenitybdd.core.pages.PageObject.withParameters;

public class OpenPageFromClassWithParameters implements Interaction {

    private final String targetPageName;
    private final Class<? extends PageObject> targetPageClass;
    private final String[] parameterValues;
    private final String parametersString;

    public OpenPageFromClassWithParameters(Class<? extends PageObject> targetPageClass, String targetPageName, String... parameters) {
        this.targetPageClass = targetPageClass;
        this.targetPageName = targetPageName;
        this.parameterValues = parameters;
        this.parametersString = Arrays.toString(parameters);
    }

    @Step("{0} opens the #targetPageName with parameters #parametersString")
    public <T extends Actor> void performAs(T theUser) {
        PageObject targetPage = new Pages(BrowseTheWeb.as(theUser).getDriver()).getPage(targetPageClass);
        targetPage.setDriver(BrowseTheWeb.as(theUser).getDriver());
        targetPage.open(withParameters(parameterValues));
    }
}
