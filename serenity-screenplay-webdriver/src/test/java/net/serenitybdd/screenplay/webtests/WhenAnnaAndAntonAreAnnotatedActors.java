package net.serenitybdd.screenplay.webtests;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.thucydides.core.annotations.Managed;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
public class WhenAnnaAndAntonAreAnnotatedActors {

    @Managed(driver = "chrome", options = "--headless")
    WebDriver annasBrowser;

    @CastMember(name = "Anna The Annotated User", browserField = "annasBrowser")
    Actor anna;

    @Managed(driver = "chrome", options = "--headless")
    WebDriver antonsBrowser;

    @CastMember(name = "Anton The Other Annotated User", browserField = "antonsBrowser")
    Actor anton;

    @Test
    public void actorsCanHaveLongNames() {
        assertThat(anna.getName(), equalTo("Anna The Annotated User"));
        assertThat(anton.getName(), equalTo("Anton The Other Annotated User"));
    }

    @Test
    public void eachActorHasTheirOwnBrowser() {
        assertThat(BrowseTheWeb.as(anna).getDriver(), equalTo(annasBrowser));
        assertThat(BrowseTheWeb.as(anton).getDriver(), equalTo(antonsBrowser));
    }
}
