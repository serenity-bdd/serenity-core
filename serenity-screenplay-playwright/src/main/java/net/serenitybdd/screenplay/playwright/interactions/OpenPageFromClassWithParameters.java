package net.serenitybdd.screenplay.playwright.interactions;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.PageUrls;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.thucydides.core.annotations.Step;
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
    public <T extends Actor> void performAs(T actor) {
        PageObject targetPage = new Pages().getPage(targetPageClass);
        String url = new PageUrls(targetPage).getStartingUrl(withParameters(parameterValues));
        actor.attemptsTo(Open.url(url));
    }
}
