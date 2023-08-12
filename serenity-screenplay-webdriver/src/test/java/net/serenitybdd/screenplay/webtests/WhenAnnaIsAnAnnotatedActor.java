package net.serenitybdd.screenplay.webtests;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.serenitybdd.annotations.Managed;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SerenityRunner.class)
public class WhenAnnaIsAnAnnotatedActor {

    @Managed(driver = "firefox", options = "--headless")
    WebDriver annasBrowser;

    @CastMember(browserField = "annasBrowser")
    Actor anna;

    @Test
    public void annotatedActorsAreInstantiatedAutomatically() {
        assertThat(anna, notNullValue());
    }

    @Test
    public void annotatedActorsAreNamed() {
        assertThat(anna.getName(), equalTo("Anna"));
    }

    @Test
    public void annotatedActorsDeclaredInAClassWithADriverCanBrowseTheWebWithThatDriver() {
        assertThat(BrowseTheWeb.as(anna).getDriver(), equalTo(annasBrowser));
    }

    @Test
    public void annotatedActorsCanInteractWithABrowser() {
        anna.attemptsTo(
                Open.url("classpath:/sample-web-site/index.html")
        );
    }
}
