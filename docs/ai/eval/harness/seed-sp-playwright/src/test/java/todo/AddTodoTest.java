package todo;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.playwright.actors.PlaywrightCast;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import net.serenitybdd.screenplay.playwright.interactions.Enter;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.serenitybdd.screenplay.playwright.interactions.Press;
import net.serenitybdd.screenplay.playwright.questions.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.hasItem;

@ExtendWith(SerenityJUnit5Extension.class)
class AddTodoTest {

    static final String NEW_TODO = ".new-todo";
    static final String ITEMS = ".todo-list li label";

    static Performable addATodo(String item) {
        return Task.where("{0} adds a todo called " + item,
                Enter.theValue(item).into(NEW_TODO),
                Press.keys("Enter"));
    }

    @BeforeEach
    void setTheStage() {
        OnStage.setTheStage(new PlaywrightCast());
    }

    @Test
    void shouldShowTheNewTodo() {
        var toby = OnStage.theActorCalled("Toby");
        toby.attemptsTo(
                Open.url("https://todo-app.serenity-js.org"),
                addATodo("Buy milk"),
                Ensure.that(ITEMS).hasText("Buy milk"));
        // Playwright's Text.ofEach(..) is a Question<List<String>> (Selenium's is Collection<String>)
        toby.should(seeThat(Text.ofEach(ITEMS), hasItem("Buy milk")));
    }
}
