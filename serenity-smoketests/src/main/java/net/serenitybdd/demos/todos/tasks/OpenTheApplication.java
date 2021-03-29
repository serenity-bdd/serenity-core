package net.serenitybdd.demos.todos.tasks;

import net.serenitybdd.demos.todos.pages.ApplicationHomePage;
import serenityscreenplay.screenplay.Actor;
import serenityscreenplay.screenplay.Performable;
import net.thucydides.core.annotations.Step;

import static serenityscreenplay.screenplay.abilities.BrowseTheWeb.as;

public class OpenTheApplication implements Performable {

    @Step("{0} adds a todo item called #thingToDo")
    public <T extends Actor> void performAs(T actor) {
        as(actor).onPage(ApplicationHomePage.class).openApplication();
    }

}
