package com.serenity.screenplay.features;

import static com.serenity.screenplay.ui.DashboardScreen.ADD_BUTTON;
import static net.serenitybdd.screenplay.GivenWhenThen.givenThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;

@RunWith(SerenityRunner.class)
@WithTags({
        @WithTag("android"),
        @WithTag("ios"),
})
/**
 * @author jacob
 */
public class WhenUserStartAppStory {
	
	Actor jacob = Actor.named("Jacob");
	
    @Managed
    public WebDriver hisMobileDevice;
    
	@Before
 	public void jacobCanBrowseTheMobileApp() {
 		OnStage.setTheStage(new OnlineCast());
 	}

    @Test
    public void when_launch_application() {
    	
    	givenThat(jacob).can(BrowseTheWeb.with(hisMobileDevice));
    	
    	givenThat(jacob).attemptsTo(Click.on(ADD_BUTTON));

    }
}
