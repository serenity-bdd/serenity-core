package net.serenitybdd.screenplay.webtests;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.serenitybdd.screenplay.GivenWhenThen.when;

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
