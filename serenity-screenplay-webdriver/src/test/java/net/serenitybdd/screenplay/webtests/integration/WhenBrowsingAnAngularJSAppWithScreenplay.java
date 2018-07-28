package net.serenitybdd.screenplay.webtests.integration;

import com.paulhammant.ngwebdriver.ByAngular;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.questions.targets.TheTarget;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.thucydides.core.annotations.Managed;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.hasItems;

@RunWith(SerenityRunner.class)
public class WhenBrowsingAnAngularJSAppWithScreenplay {

    private static Target NEW_TODO = Target.the("New Todo")
                                                       .located(ByAngular.model("newTodo"));

    private static Target ITEMS = Target.the("The visible todo items")
                                                     .located(ByAngular.repeater("todo in todos"));

    @Managed(driver = "firefox")
    private WebDriver driver;

    @Test
    public void shouldProvideAccessToAngularLocatorsAndWaitForAngularToFinish() {

        Actor tim = Actor.named("Tim").describedAs("A todo-list enthusiast");
        tim.can(BrowseTheWeb.with(driver));

        tim.attemptsTo(
                Open.url("http://todomvc.com/examples/angularjs/#/"),
                Enter.theValue("Walk the dog").into(NEW_TODO).thenHit(Keys.ENTER),
                WaitUntil.angularRequestsHaveFinished()
        );

        tim.attemptsTo(
                Enter.theValue("Feed the cat").into(ByAngular.model("newTodo")).thenHit(Keys.ENTER),
                WaitUntil.angularRequestsHaveFinished()
        );

        tim.should(seeThat(TheTarget.textValuesOf(ITEMS), hasItems("Walk the dog","Feed the cat")));
    }
}
