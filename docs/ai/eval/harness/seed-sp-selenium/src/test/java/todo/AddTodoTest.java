package todo;

import net.serenitybdd.annotations.Managed;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.ensure.Ensure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.hasItem;

@ExtendWith(SerenityJUnit5Extension.class)
class AddTodoTest {

    @Managed(driver = "chrome")
    WebDriver browser;

    @BeforeEach
    void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @Test
    void shouldShowTheNewTodo() {
        var toby = OnStage.theActorCalled("Toby");
        toby.attemptsTo(
                Open.url("https://todo-app.serenity-js.org"),
                AddATodo.called("Buy milk"));
        toby.should(seeThat(TheTodos.displayed(), hasItem("Buy milk")));
    }

    @Test
    void shouldShowTheNewTodoUsingEnsure() {
        var toby = OnStage.theActorCalled("Toby");
        toby.attemptsTo(
                Open.url("https://todo-app.serenity-js.org"),
                AddATodo.called("Buy milk"),
                Ensure.that(TodoList.ITEMS).hasText("Buy milk"));
    }
}
