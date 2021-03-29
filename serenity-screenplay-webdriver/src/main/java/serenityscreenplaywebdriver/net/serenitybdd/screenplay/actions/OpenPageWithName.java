package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenitymodel.net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.exceptions.UnknownPageException;
import serenitymodel.net.thucydides.core.annotations.Step;
import serenitymodel.net.thucydides.core.guice.Injectors;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;

public class OpenPageWithName implements Interaction {

    private final String pageName;
    private final EnvironmentVariables environmentVariables;

    public OpenPageWithName(String pageName) {
        this.pageName = pageName;
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    @Step("{0} opens the #pageName")
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
