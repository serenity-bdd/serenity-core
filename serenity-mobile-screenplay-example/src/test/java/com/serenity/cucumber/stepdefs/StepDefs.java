package com.serenity.cucumber.stepdefs;

import static com.serenity.screenplay.ui.DashboardScreen.ADD_BUTTON;
import static net.serenitybdd.screenplay.GivenWhenThen.givenThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

import org.openqa.selenium.WebDriver;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.thucydides.core.annotations.Managed;


public class StepDefs {
	
    @Managed(driver = "Appium")
    public WebDriver mobileDevice;
	
    @Before
    public void set_the_stage() {
        OnStage.setTheStage(new OnlineCast());
    }
	
    @When("^\"(.*)\" notes \"(.*)\" to his list$")
    public void he_notes_to_his_list(String actorName, String item) throws Throwable {
        theActorCalled(actorName).attemptsTo(Click.on(ADD_BUTTON));
        
    }
}
