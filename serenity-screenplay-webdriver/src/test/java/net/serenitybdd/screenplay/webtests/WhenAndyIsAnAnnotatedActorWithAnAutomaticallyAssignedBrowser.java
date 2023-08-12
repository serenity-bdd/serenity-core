package net.serenitybdd.screenplay.webtests;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.annotations.CastMember;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SerenityRunner.class)
public class WhenAndyIsAnAnnotatedActorWithAnAutomaticallyAssignedBrowser {

    @CastMember(driver = "firefox", options = "--headless")
    Actor andy;

    @Test
    public void annotatedActorsAreInstantiatedAutomatically() {
        assertThat(andy, notNullValue());
    }

    @Test
    public void annotatedActorsCanInteractWithABrowser() {
        andy.attemptsTo(
                Open.url("classpath:/sample-web-site/index.html")
        );
    }
}
