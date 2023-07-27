package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.util.EnvironmentVariables;

public class OpenPageWithName implements Interaction {

    private String pageName;
    private EnvironmentVariables environmentVariables;

    public OpenPageWithName() {}

    public OpenPageWithName(String pageName) {
        this.pageName = pageName;
        this.environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
        System.out.println(environmentVariables);
    }

    @Step("{0} opens the #pageName page")
    public <T extends Actor> void performAs(T theUser) {
        String pageUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(pageName)
                .orElse(environmentVariables.getProperty(pageName));
        if (pageUrl == null) {
            throw new UnknownPageException("No page defined with the name '" + pageUrl + "'");
        }
        BrowseTheWeb.as(theUser).getDriver().get(pageUrl);
    }

}
