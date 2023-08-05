package net.serenitybdd.screenplay.actions;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.annotations.Step;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;

public class OpenPageWithName implements Interaction {

    private String pageName;
    private EnvironmentVariables environmentVariables;

    public OpenPageWithName() {}

    public OpenPageWithName(String pageName) {
        this.pageName = pageName;
        this.environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
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
