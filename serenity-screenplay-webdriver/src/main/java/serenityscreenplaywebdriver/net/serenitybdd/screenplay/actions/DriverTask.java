package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.WebDriver;

import java.util.function.Consumer;

public class DriverTask implements Performable {
    private final Consumer<WebDriver> action;

    public DriverTask(Consumer<WebDriver> action) {
        this.action = action;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        action.accept(BrowseTheWeb.as(actor).getDriver());
    }
}
