package net.serenitybdd.screenplay.webtests;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.webtests.questions.ProfileQuestion;
import net.serenitybdd.screenplay.webtests.tasks.OpenTheApplication;
import net.serenitybdd.screenplay.webtests.tasks.UpdateHerProfile;
import net.serenitybdd.screenplay.webtests.tasks.ViewMyProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

@RunWith(SerenityRunner.class)
public class WhenSeveralActorsPerformNonWebActions {

    private Task createANewProfile() {
        return Task.where("Create a new profile");
    }
    private Task openNewProfile() {
        return Task.where("Open profile");
    }

    @Test
    public void multipleUsersCanShareTheSameBrowser() {
        Actor dana = new Actor("Dana");
        Actor jane = new Actor("Jane");

        when(dana).attemptsTo(createANewProfile());
        when(jane).attemptsTo(openNewProfile());
    }

}
